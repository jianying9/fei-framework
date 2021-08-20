package com.fei.devops.controller;

import com.fei.annotations.component.Resource;
import com.fei.annotations.web.Controller;
import com.fei.annotations.web.RequestMapping;
import com.fei.annotations.web.ResponseParam;
import com.fei.app.utils.ToolUtil;
import com.fei.devops.component.GitlabComponent;
import com.fei.devops.component.GitlabComponent.GitlabProject;
import com.fei.devops.component.GitlabComponent.GitlabToken;
import com.fei.devops.entity.GitlabTokenEntity;
import com.fei.module.EsEntityDao;
import com.fei.web.component.Session;
import com.fei.web.router.BizException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目接口
 *
 * @author jianying9
 */
@Controller(value = "/project", auth = true, name = "项目")
public class ProjectController
{

    @Resource
    private GitlabComponent gitlabComponent;

    @Resource
    private EsEntityDao<GitlabTokenEntity> gitlabTokenEntityDao;

    public static class TemplateSearchView
    {

        @ResponseParam(description = "项目模板集合")
        public List<TemplateView> templateArray = new ArrayList();

    }

    public static class TemplateView
    {

        @ResponseParam(description = "id")
        public String id;

        @ResponseParam(description = "名称")
        public String name;

        @ResponseParam(description = "路径")
        public String path;

        @ResponseParam(description = "可见性")
        public String visibility;

        @ResponseParam(description = "图标")
        public String avatarUrl;

        @ResponseParam(description = "描述")
        public String description;

    }

    @RequestMapping(value = "/template/search", description = "可用项目模板")
    public TemplateSearchView searchTemplate(
            Session session
    ) throws BizException, IOException
    {
        GitlabTokenEntity gitlabTokenEntity = this.gitlabTokenEntityDao.get(session.id);
        GitlabToken gitlabToken = ToolUtil.copy(gitlabTokenEntity, GitlabToken.class);
        //
        TemplateSearchView templateSearchView = new TemplateSearchView();
        templateSearchView.templateArray = new ArrayList();
        final String id = "22";
        List<GitlabProject> gitlabProjectList = this.gitlabComponent.searchGroupProject(gitlabToken, id);
        TemplateView templateView;
        for (GitlabProject gitlabProject : gitlabProjectList) {
            templateView = ToolUtil.copy(gitlabProject, TemplateView.class);
            templateSearchView.templateArray.add(templateView);
        }
        return templateSearchView;
    }
}
