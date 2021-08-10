package com.fei.devops.controller;

import com.fei.annotations.component.Resource;
import com.fei.annotations.web.Controller;
import com.fei.annotations.web.RequestMapping;
import com.fei.annotations.web.RequestParam;
import com.fei.annotations.web.ResponseParam;
import com.fei.app.utils.ToolUtil;
import com.fei.devops.component.GitlabComponent;
import com.fei.devops.component.GitlabComponent.GitlabGroup;
import com.fei.devops.component.GitlabComponent.GitlabMember;
import com.fei.devops.component.GitlabComponent.GitlabToken;
import com.fei.devops.entity.GitlabTokenEntity;
import com.fei.module.EsEntityDao;
import com.fei.web.component.Session;
import com.fei.web.router.BizException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 群组接口
 *
 * @author jianying9
 */
@Controller(value = "/group", auth = true, name = "群组")
public class GroupController
{

    @Resource
    private GitlabComponent gitlabComponent;

    @Resource
    private EsEntityDao<GitlabTokenEntity> gitlabTokenEntityDao;

    public static class GroupView
    {

        @ResponseParam(desc = "id")
        public String id;

        @ResponseParam(desc = "名称")
        public String name;

        @ResponseParam(desc = "路径")
        public String path;

        @ResponseParam(desc = "可见级别[private,internal,public]")
        public String visibility;

        @ResponseParam(desc = "图标")
        public String avatarUrl;

        @ResponseParam(desc = "描述")
        public String description;

    }

    public static class GroupSearchView
    {

        @ResponseParam(desc = "群组集合")
        public List<GroupView> groupArray = new ArrayList();

    }

    /**
     * 搜索
     *
     * @param session
     * @return
     * @throws BizException
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/search", desc = "用户列表查询")
    public GroupSearchView search(Session session) throws BizException, IOException
    {
        GitlabTokenEntity gitlabTokenEntity = this.gitlabTokenEntityDao.get(session.id);
        GitlabToken gitlabToken = ToolUtil.copy(gitlabTokenEntity, GitlabToken.class);
        //
        List<GitlabGroup> groupList = this.gitlabComponent.searchGroup(gitlabToken);
        GroupSearchView groupSearchView = new GroupSearchView();
        GroupView groupView;
        for (GitlabGroup gitlabGroup : groupList) {
            groupView = ToolUtil.copy(gitlabGroup, GroupView.class);
            groupSearchView.groupArray.add(groupView);
        }
        return groupSearchView;
    }

    public static class GroupDetailView
    {

        @ResponseParam(desc = "id")
        public String id;

        @ResponseParam(desc = "名称")
        public String name;

        @ResponseParam(desc = "路径")
        public String path;

        @ResponseParam(desc = "可见级别[private,internal,public]")
        public String visibility;

        @ResponseParam(desc = "图标")
        public String avatarUrl;

        @ResponseParam(desc = "描述")
        public String description;

        @ResponseParam(desc = "成员集合")
        public List<MemberView> memberArray;

    }

    /**
     * 获取群组的详细信息
     *
     * @param session
     * @param id
     * @return
     * @throws com.fei.web.router.BizException
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/get", desc = "获取群组的详细信息")
    public GroupDetailView get(
            Session session,
            @RequestParam(desc = "id") String id
    ) throws BizException, IOException
    {
        GitlabTokenEntity gitlabTokenEntity = this.gitlabTokenEntityDao.get(session.id);
        GitlabToken gitlabToken = ToolUtil.copy(gitlabTokenEntity, GitlabToken.class);
        //
        GitlabGroup gitlabGroup = this.gitlabComponent.getGroup(gitlabToken, id);
        if (gitlabGroup == null) {
            throw new BizException("git_group_null", "群组不存在");
        }
        GroupDetailView groupDetailView = ToolUtil.copy(gitlabGroup, GroupDetailView.class);
        //
        groupDetailView.memberArray = new ArrayList();
        List<GitlabMember> gitlabMemberList = this.gitlabComponent.searchGroupMember(gitlabToken, id);
        MemberView memberView;
        for (GitlabMember gitlabMember : gitlabMemberList) {
            memberView = ToolUtil.copy(gitlabMember, MemberView.class);
            groupDetailView.memberArray.add(memberView);
        }
        return groupDetailView;
    }

    /**
     * 新增群组
     *
     * @param session
     * @param name
     * @param description
     * @return
     * @throws BizException
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/add", desc = "新增群组")
    public GroupView add(
            Session session,
            @RequestParam(desc = "名称") String name,
            @RequestParam(desc = "描述") String description
    ) throws BizException, IOException
    {
        GitlabTokenEntity gitlabTokenEntity = this.gitlabTokenEntityDao.get(session.id);
        GitlabToken gitlabToken = ToolUtil.copy(gitlabTokenEntity, GitlabToken.class);
        //
        GitlabGroup gitlabGroup = this.gitlabComponent.addGroup(gitlabToken, name, description);
        GroupView groupView = ToolUtil.copy(gitlabGroup, GroupView.class);
        return groupView;
    }

    public static class MemberView
    {

        @ResponseParam(desc = "id")
        public String id;

        @ResponseParam(desc = "名称")
        public String name;

        @ResponseParam(desc = "账号")
        public String username;

        @ResponseParam(desc = "状态")
        public String state;

        @ResponseParam(desc = "权限")
        public int accessLevel;

    }

    public static class MemberSearchView
    {

        @ResponseParam(desc = "成员集合")
        public List<MemberView> memberArray = new ArrayList();

    }

    @RequestMapping(value = "/searchMember", desc = "群组成员查询")
    public MemberSearchView searchMember(
            Session session,
            @RequestParam(desc = "群组id") String id
    ) throws BizException, IOException
    {
        GitlabTokenEntity gitlabTokenEntity = this.gitlabTokenEntityDao.get(session.id);
        GitlabToken gitlabToken = ToolUtil.copy(gitlabTokenEntity, GitlabToken.class);
        //
        List<GitlabMember> gitlabMemberList = this.gitlabComponent.searchGroupMember(gitlabToken, id);
        MemberSearchView memberSearchView = new MemberSearchView();
        MemberView memberView;
        for (GitlabMember gitlabMember : gitlabMemberList) {
            memberView = ToolUtil.copy(gitlabMember, MemberView.class);
            memberSearchView.memberArray.add(memberView);
        }
        return memberSearchView;
    }

    @RequestMapping(value = "/member/add", desc = "群组新增成员")
    public void addMember(
            Session session,
            @RequestParam(desc = "群组id") String id,
            @RequestParam(desc = "用户id") List<String> userIdArray,
            @RequestParam(desc = "权限") int accessLevel
    ) throws BizException, IOException
    {
        GitlabTokenEntity gitlabTokenEntity = this.gitlabTokenEntityDao.get(session.id);
        GitlabToken gitlabToken = ToolUtil.copy(gitlabTokenEntity, GitlabToken.class);
        //获取该群组所有成员信息
        Set<String> existUserIdSet = new HashSet();
        List<GitlabMember> gitlabMemberList = this.gitlabComponent.searchGroupMember(gitlabToken, id);
        for (GitlabMember gitlabMember : gitlabMemberList) {
            existUserIdSet.add(gitlabMember.id);
        }
        for (String userId : userIdArray) {
            if (existUserIdSet.contains(userId)) {
                //更新
                this.gitlabComponent.updateGroupMember(gitlabToken, id, userId, accessLevel);
            } else {
                //新增
                this.gitlabComponent.addGroupMember(gitlabToken, id, userId, accessLevel);
            }

        }
    }

    @RequestMapping(value = "/member/delete", desc = "群组删除成员")
    public MemberView deleteMember(
            Session session,
            @RequestParam(desc = "群组id") String id,
            @RequestParam(desc = "用户id") String userId
    ) throws BizException, IOException
    {
        GitlabTokenEntity gitlabTokenEntity = this.gitlabTokenEntityDao.get(session.id);
        GitlabToken gitlabToken = ToolUtil.copy(gitlabTokenEntity, GitlabToken.class);
        //
        GitlabMember gitlabMember = this.gitlabComponent.deleteGroupMember(gitlabToken, id, userId);
        MemberView memberView = ToolUtil.copy(gitlabMember, MemberView.class);
        return memberView;
    }

}
