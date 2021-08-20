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
import com.fei.web.component.Session;
import com.fei.web.router.BizException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户接口
 *
 * @author jianying9
 */
@Controller(value = "/user", auth = true, name = "用户")
public class UserController
{

    @Resource
    private GitlabComponent gitlabComponent;

    @Resource
    private EsEntityDao<GitlabTokenEntity> gitlabTokenEntityDao;

    public static class UserView
    {

        @ResponseParam(description = "id")
        public String id;

        @ResponseParam(description = "名称")
        public String name;

        @ResponseParam(description = "账号")
        public String username;

        @ResponseParam(description = "邮箱")
        public String email;

        @ResponseParam(description = "是否管理员")
        public boolean isAdmin;

        @ResponseParam(description = "状态")
        public String state;

    }

    public static class UserSearchView
    {

        @ResponseParam(description = "用户集合")
        public List<UserView> userArray = new ArrayList();

    }

    /**
     * 用户搜索
     *
     * @param session
     * @return
     * @throws BizException
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/search", description = "用户列表查询")
    public UserSearchView search(Session session) throws BizException, IOException
    {
        GitlabTokenEntity gitlabTokenEntity = this.gitlabTokenEntityDao.get(session.id);
        GitlabToken gitlabToken = ToolUtil.copy(gitlabTokenEntity, GitlabToken.class);
        //
        List<GitlabUser> userList = this.gitlabComponent.searchUser(gitlabToken);
        UserSearchView userSearchView = new UserSearchView();
        UserView userView;
        for (GitlabUser gitlabUser : userList) {
            userView = ToolUtil.copy(gitlabUser, UserView.class);
            userSearchView.userArray.add(userView);
        }
        return userSearchView;
    }

    /**
     * 获取user的详细信息
     *
     * @param session
     * @param id
     * @return
     * @throws com.fei.web.router.BizException
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/get", description = "获取user的详细信息")
    public UserView get(
            Session session,
            @RequestParam(description = "id") String id
    ) throws BizException, IOException
    {
        GitlabTokenEntity gitlabTokenEntity = this.gitlabTokenEntityDao.get(session.id);
        GitlabToken gitlabToken = ToolUtil.copy(gitlabTokenEntity, GitlabToken.class);
        //
        GitlabUser gitlabUser = this.gitlabComponent.getUser(gitlabToken, id);
        if (gitlabUser == null) {
            throw new BizException("git_user_null", "用户不存在");
        }
        UserView userView = ToolUtil.copy(gitlabUser, UserView.class);
        return userView;
    }
    
    /**
     * 获取当前登录用户信息
     * @param session
     * @return
     * @throws BizException
     * @throws IOException 
     */
    @RequestMapping(value = "/current", description = "获取当前用户的详细信息")
    public UserView current(
            Session session
    ) throws BizException, IOException
    {
        GitlabTokenEntity gitlabTokenEntity = this.gitlabTokenEntityDao.get(session.id);
        GitlabToken gitlabToken = ToolUtil.copy(gitlabTokenEntity, GitlabToken.class);
        //
        GitlabUser gitlabUser = this.gitlabComponent.getCurrentUser(gitlabToken);
        if (gitlabUser == null) {
            throw new BizException("git_user_null", "用户不存在");
        }
        UserView userView = ToolUtil.copy(gitlabUser, UserView.class);
        return userView;
    }

    public static class UserAddView
    {

        @ResponseParam(description = "id")
        public String id;

        @ResponseParam(description = "初始密码")
        public String password;

    }

    /**
     * 新增用户
     *
     * @param session
     * @param name
     * @param username
     * @param email
     * @return
     * @throws BizException
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/add", description = "新增用户")
    public UserAddView add(
            Session session,
            @RequestParam(description = "用户名称") String name,
            @RequestParam(description = "账号") String username,
            @RequestParam(description = "邮箱") String email
    ) throws BizException, IOException
    {
        GitlabTokenEntity gitlabTokenEntity = this.gitlabTokenEntityDao.get(session.id);
        GitlabToken gitlabToken = ToolUtil.copy(gitlabTokenEntity, GitlabToken.class);
        //
        String password = ToolUtil.getAutomicId();
        GitlabUser gitlabUser = this.gitlabComponent.addUser(gitlabToken, email, name, username, password);
        UserAddView userAddView = new UserAddView();
        userAddView.id = gitlabUser.id;
        userAddView.password = password;
        return userAddView;
    }
}
