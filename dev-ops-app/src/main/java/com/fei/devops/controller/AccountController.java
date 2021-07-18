package com.fei.devops.controller;

import com.fei.annotations.component.Resource;
import com.fei.annotations.web.Controller;
import com.fei.annotations.web.RequestMapping;
import com.fei.annotations.web.RequestParam;
import com.fei.annotations.web.ResponseParam;
import com.fei.devops.entity.AccountEntity;
import com.fei.app.utils.ToolUtil;
import com.fei.module.EsEntityDao;
import com.fei.web.component.JwtBean;
import com.fei.web.component.Session;
import com.fei.web.component.Token;
import com.fei.web.response.Response;
import com.fei.web.router.BizException;
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
    private EsEntityDao<AccountEntity> accountEntityDao;

    @Resource
    private JwtBean jwtBean;

    public static class AuthView
    {

        @ResponseParam(desc = "auth的jwt信息")
        public String auth;

        @ResponseParam(desc = "更新auth的jwt信息")
        public String refreshAuth;

    }

    /**
     * 登录
     *
     * @param account
     * @param password
     * @return
     * @throws BizException
     */
    @RequestMapping(value = "/login", desc = "账号登录")
    public AuthView login(
            @RequestParam(desc = "账号") String account,
            @RequestParam(desc = "密码") String password
    ) throws BizException
    {
        AccountEntity accountEntity = this.accountEntityDao.get(account);
        if (accountEntity == null || accountEntity.enabled == false) {
            //账号不存在
            throw new BizException("account_error", "账号不存在");
        } else if (accountEntity.password.equals(password) == false) {
            //密码错误
            throw new BizException("password_error", "密码错误");
        } else {
            //登录成功,生成token
            AuthView authView = new AuthView();
            authView.auth = this.jwtBean.createToken(accountEntity.userId, accountEntity.userName);
            //刷新token30有效
            long time = System.currentTimeMillis() + 3600000 * 24 * 30;
            Date refreshExpireTime = new Date(time);
            authView.refreshAuth = this.jwtBean.createToken(accountEntity.userId, accountEntity.userName, refreshExpireTime);
            return authView;
        }
    }

    public static class SessionView
    {

        @ResponseParam(desc = "用户id")
        public String userId;

        @ResponseParam(desc = "用户")
        public String userName;

        @ResponseParam(desc = "到期时间")
        public Date expireDate;

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
     * @param refreshAuth
     * @return
     * @throws BizException
     */
    @RequestMapping(value = "/refresh", desc = "通过refreshAuth重新获取auth")
    public AuthView refresh(
            @RequestParam(desc = "刷新请求token") String refreshAuth
    ) throws BizException
    {
        Token token = this.jwtBean.verifyToken(refreshAuth);
        if (token == null) {
            //验证失败
            throw new BizException(Response.FAILED, "用户校验失败");
        } else if (token.expired) {
            //过期
            throw new BizException(Response.FAILED, "用户信息已过期");
        } else {
            AuthView authView = new AuthView();
            //登录成功,生成token
            authView.auth = this.jwtBean.createToken(token.userId, token.userName);
            return authView;
        }
    }
}
