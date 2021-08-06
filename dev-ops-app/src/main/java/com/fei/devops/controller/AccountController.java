package com.fei.devops.controller;

import com.fei.annotations.component.Resource;
import com.fei.annotations.web.Controller;
import com.fei.annotations.web.RequestMapping;
import com.fei.annotations.web.RequestParam;
import com.fei.annotations.web.ResponseParam;
import com.fei.app.utils.ToolUtil;
import com.fei.devops.component.GitlabComponent;
import com.fei.devops.component.GitlabComponent.GitlabToken;
import com.fei.devops.component.GitlabComponent.GitlabUser;
import com.fei.devops.entity.GitlabTokenEntity;
import com.fei.module.EsEntityDao;
import com.fei.web.component.JwtBean;
import com.fei.web.component.Session;
import com.fei.web.component.Token;
import com.fei.web.response.Response;
import com.fei.web.router.BizException;
import java.io.IOException;
import java.util.Date;

/**
 * 账号接口
 *
 * @author jianying9
 */
@Controller(value = "/account", name = "账号")
public class AccountController
{

    @Resource
    private GitlabComponent gitlabComponent;

    @Resource
    private EsEntityDao<GitlabTokenEntity> gitlabTokenEntityDao;

    @Resource
    private JwtBean jwtBean;

    public static class AuthView
    {

        @ResponseParam(desc = "auth的jwt信息")
        public String auth;

        @ResponseParam(desc = "更新auth的jwt信息")
        public String refreshToken;

    }

    /**
     * 登录
     *
     * @param code
     * @param redirectUri
     * @return
     * @throws BizException
     */
    @RequestMapping(value = "/loginByGitlab", desc = "通过gitlab的auth code登录")
    public AuthView login(
            @RequestParam(desc = "code") String code,
            @RequestParam(desc = "重定向uri") String redirectUri
    ) throws BizException, IOException
    {
        //通过code获取token
        GitlabToken gitlabToken = this.gitlabComponent.getToken(code, redirectUri);
        //获取当前用户信息
        GitlabUser gitlabUser = this.gitlabComponent.getCurrentUser(gitlabToken);
        //保存token
        GitlabTokenEntity gitlabTokenEntity = ToolUtil.copy(gitlabToken, GitlabTokenEntity.class);
        gitlabTokenEntity.id = gitlabUser.id;
        this.gitlabTokenEntityDao.upsert(gitlabTokenEntity);
        //登录成功,生成jwt token
        AuthView authView = new AuthView();
        authView.auth = this.jwtBean.createToken(gitlabUser.id, gitlabUser.name);
        //刷新token30天有效
        long time = System.currentTimeMillis() + 3600000l * 24l * 30l;
        Date refreshExpireTime = new Date(time);
        authView.refreshToken = this.jwtBean.createToken(gitlabUser.id, gitlabUser.name, refreshExpireTime);
        return authView;
    }

    public static class SessionView
    {

        @ResponseParam(desc = "用户id")
        public String id;

        @ResponseParam(desc = "用户")
        public String name;

    }

    /**
     * 获取当前登录用户信息
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "/get", auth = true, desc = "获取当前登录信息")
    public SessionView get(Session session)
    {
        SessionView sessionDto = ToolUtil.copy(session, SessionView.class);
        return sessionDto;
    }

    /**
     * 通过refreshAuth获得新的auth
     *
     * @param refreshToken
     * @return
     * @throws BizException
     */
    @RequestMapping(value = "/refresh", desc = "通过refreshAuth重新获取auth")
    public AuthView refresh(
            @RequestParam(desc = "刷新请求token") String refreshToken
    ) throws BizException
    {
        Token token = this.jwtBean.verifyToken(refreshToken);
        if (token == null) {
            //验证失败
            throw new BizException(Response.FAILED, "用户校验失败");
        } else if (token.expired) {
            //过期
            throw new BizException(Response.FAILED, "用户信息已过期");
        } else {
            AuthView authView = new AuthView();
            //登录成功,生成token
            authView.auth = this.jwtBean.createToken(token.id, token.name);
            return authView;
        }
    }
}
