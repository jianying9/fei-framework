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

    public static class LoginD
    {

        @RequestParam(desc = "账号")
        public String account;

        @RequestParam(desc = "密码")
        public String password;

    }

    public static class AuthV
    {

        @ResponseParam(desc = "auth的jwt信息")
        public String auth;

        @ResponseParam(desc = "更新auth的jwt信息")
        public String refreshAuth;

    }

    /**
     * 登录
     *
     * @param loginD
     * @return
     * @throws BizException
     */
    @RequestMapping(value = "/login", desc = "账号登录")
    public AuthV login(LoginD loginD) throws BizException
    {
        AccountEntity accountEntity = this.accountEntityDao.get(loginD.account);
        if (accountEntity == null || accountEntity.enabled == false) {
            //账号不存在
            throw new BizException("account_error", "账号不存在");
        } else if (accountEntity.password.equals(loginD.password) == false) {
            //密码错误
            throw new BizException("password_error", "密码错误");
        } else {
            //登录成功,生成token
            AuthV authV = new AuthV();
            authV.auth = this.jwtBean.createToken(accountEntity.userId, accountEntity.userName);
            //刷新token30有效
            long time = System.currentTimeMillis() + 3600000 * 24 * 30;
            Date refreshExpireTime = new Date(time);
            authV.refreshAuth = this.jwtBean.createToken(accountEntity.userId, accountEntity.userName, refreshExpireTime);
            return authV;
        }
    }

    public static class SessionV
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
    public SessionV get(Session session)
    {
        SessionV sessionDto = ToolUtil.copy(session, SessionV.class);
        return sessionDto;
    }

    public static class RefreshD
    {

        @RequestParam(desc = "刷新请求token")
        public String refreshToken;

    }

    @RequestMapping(value = "/refresh", desc = "通过refreshAuth重新获取auth")
    public String refresh(
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
            //登录成功,生成token
            return this.jwtBean.createToken(token.userId, token.userName);
        }
    }
}
