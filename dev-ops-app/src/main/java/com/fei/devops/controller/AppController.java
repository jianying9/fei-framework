package com.fei.devops.controller;

import com.fei.annotations.component.Resource;
import com.fei.annotations.web.Controller;
import com.fei.annotations.web.RequestMapping;
import com.fei.annotations.web.RequestParam;
import com.fei.annotations.web.ResponseParam;
import com.fei.app.utils.ToolUtil;
import com.fei.devops.entity.AppEntity;
import com.fei.elasticsearch.index.query.BoolQueryBuilder;
import com.fei.elasticsearch.index.query.QueryBuilders;
import com.fei.module.EsEntityDao;
import com.fei.web.router.BizException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 应用接口
 *
 * @author jianying9
 */
@Controller(value = "/app", auth = true, name = "应用")
public class AppController
{

    @Resource
    private EsEntityDao<AppEntity> appEntityDao;

    public static class AppView
    {

        @ResponseParam(description = "appId")
        public String appId;

        @ResponseParam(description = "名称")
        public String appName;

        @ResponseParam(description = "描述")
        public String appDesc;

        @ResponseParam(description = "创建时间")
        public Date timestamp;

    }

    public static class AppSearchView
    {

        @ResponseParam(description = "app集合")
        public List<AppView> appArray = new ArrayList();

    }

    /**
     * app搜索
     *
     * @param keyword
     * @return
     * @throws BizException
     */
    @RequestMapping(value = "/search", description = "应用列表查询")
    public AppSearchView search(
            @RequestParam(required = false, description = "关键字") String keyword
    ) throws BizException
    {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (keyword != null && keyword.isEmpty() != false) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("keyword", keyword));
        }
        List<AppEntity> appEntityList = this.appEntityDao.search(boolQueryBuilder);
        AppSearchView appSearchView = new AppSearchView();
        AppView appView;
        for (AppEntity appEntity : appEntityList) {
            appView = ToolUtil.copy(appEntity, AppView.class);
            appSearchView.appArray.add(appView);
        }
        return appSearchView;
    }

    /**
     * 获取app的详细信息
     *
     * @param appId
     * @return
     * @throws com.fei.web.router.BizException
     */
    @RequestMapping(value = "/get", description = "获取app的详细信息")
    public AppView get(
            @RequestParam(description = "appId") String appId
    ) throws BizException
    {
        AppEntity appEntity = this.appEntityDao.get(appId);
        if (appEntity == null) {
            throw new BizException("app_null", "app不存在");
        }
        AppView appView = ToolUtil.copy(appEntity, AppView.class);
        return appView;
    }

    /**
     * 新增app
     *
     * @param appName
     * @param appDesc
     * @return
     * @throws BizException
     */
    @RequestMapping(value = "/add", description = "新增app")
    public AppView add(
            @RequestParam(description = "应用名称") String appName,
            @RequestParam(description = "应用描述") String appDesc
    ) throws BizException
    {
        AppEntity appEntity = new AppEntity();
        appEntity.appName = appName;
        appEntity.appDesc = appDesc;
        appEntity.keyword = appName + " " + appDesc;
        this.appEntityDao.insert(appEntity);
        AppView appView = ToolUtil.copy(appEntity, AppView.class);
        return appView;
    }
}
