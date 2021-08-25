package com.fei.devops.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fei.annotations.component.Resource;
import com.fei.annotations.web.Controller;
import com.fei.annotations.web.RequestMapping;
import com.fei.annotations.web.RequestParam;
import com.fei.annotations.web.ResponseParam;
import com.fei.app.utils.ToolUtil;
import com.fei.devops.component.JenkinsComponent;
import com.fei.devops.entity.NodeEntity;
import com.fei.elasticsearch.index.query.BoolQueryBuilder;
import com.fei.elasticsearch.index.query.QueryBuilder;
import com.fei.elasticsearch.index.query.QueryBuilders;
import com.fei.module.EsEntityDao;
import com.fei.web.router.BizException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * node接口
 *
 * @author jianying9
 */
@Controller(value = "/node", auth = true, name = "节点")
public class NodeController
{

    private final String emptyPasswordEncrypted = "{AQAAABAAAAAQrahPjlORtvSSCRo4ZGb22FUHhja6empySRf4DbBccI=}";

    @Resource
    private EsEntityDao<NodeEntity> nodeEntityDao;

    @Resource
    private JenkinsComponent jenkinsComponent;

    public static class NodeView
    {

        @ResponseParam(description = "id")
        public String id;

        @ResponseParam(description = "名称")
        public String name;

        @ResponseParam(description = "地址")
        public String hostname;

        @ResponseParam(description = "分支")
        public String branch;

        @ResponseParam(description = "描述")
        public String description;

        @ResponseParam(description = "创建时间")
        public Date timestamp;

    }

    public static class NodeSearchView
    {

        @ResponseParam(description = "节点集合")
        public List<NodeView> nodeArray = new ArrayList();

    }

    /**
     * 节点搜索
     *
     * @param keyword
     * @return
     * @throws BizException
     */
    @RequestMapping(value = "/search", description = "节点列表查询")
    public NodeSearchView search(
            @RequestParam(required = false, description = "关键字") String keyword
    ) throws BizException
    {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (keyword != null && keyword.isEmpty() != false) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("keyword", keyword));
        }
        List<NodeEntity> nodeEntityList = this.nodeEntityDao.search(boolQueryBuilder);
        NodeSearchView nodeSearchView = new NodeSearchView();
        NodeView nodeView;
        for (NodeEntity nodeEntity : nodeEntityList) {
            nodeView = ToolUtil.copy(nodeEntity, NodeView.class);
            nodeSearchView.nodeArray.add(nodeView);
        }
        return nodeSearchView;
    }

    /**
     * 获取节点的详细信息
     *
     * @param id
     * @return
     * @throws com.fei.web.router.BizException
     */
    @RequestMapping(value = "/get", description = "获取node详情")
    public NodeView get(
            @RequestParam(description = "id") String id
    ) throws BizException
    {
        NodeEntity nodeEntity = this.nodeEntityDao.get(id);
        if (nodeEntity == null) {
            throw new BizException("node_null", "节点不存在");
        }
        NodeView nodeView = ToolUtil.copy(nodeEntity, NodeView.class);
        return nodeView;
    }

    /**
     * 新增节点
     *
     * @param branch
     * @param name
     * @param hostname
     * @param description
     * @return
     * @throws BizException
     */
    @RequestMapping(value = "/add", description = "新增节点")
    public NodeView add(
            @RequestParam(description = "分支环境") String branch,
            @RequestParam(description = "名称") String name,
            @RequestParam(description = "地址") String hostname,
            @RequestParam(description = "描述") String description
    ) throws BizException
    {
        QueryBuilder queryBuilder = QueryBuilders.termQuery("name", name);
        List<NodeEntity> nodeEntityList = this.nodeEntityDao.search(queryBuilder);
        if (nodeEntityList.isEmpty() == false) {
            throw new BizException("node_name_exist", "该名称已经存在");
        }
        //
        queryBuilder = QueryBuilders.termQuery("hostname", hostname);
        nodeEntityList = this.nodeEntityDao.search(queryBuilder);
        if (nodeEntityList.isEmpty() == false) {
            throw new BizException("node_ip_exist", "该IP已经存在");
        }
        //新增
        NodeEntity nodeEntity = new NodeEntity();
        nodeEntity.name = name;
        nodeEntity.hostname = hostname;
        nodeEntity.branch = branch;
        nodeEntity.description = description;
        nodeEntity.keyword = name + " " + hostname + " " + branch + " " + description;
        this.nodeEntityDao.insert(nodeEntity);
        NodeView nodeView = ToolUtil.copy(nodeEntity, NodeView.class);
        return nodeView;
    }

    @RequestMapping(value = "/updatePublishOverSSH", description = "发布")
    public void updatePublishOverSSH() throws BizException, IOException
    {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        List<NodeEntity> nodeEntityList = this.nodeEntityDao.search(boolQueryBuilder);
        //构造节点实例配置集合
        JSONArray instanceJsonArray = new JSONArray();
        JSONObject instanceJson;
        for (NodeEntity nodeEntity : nodeEntityList) {
            instanceJson = this.createNodeInstanceConfig(nodeEntity);
            instanceJsonArray.add(instanceJson);
        }
        //构造通用配置
        JSONObject commonConfigJson = new JSONObject();
        commonConfigJson.put("encryptedPassphrase", this.emptyPasswordEncrypted);
        commonConfigJson.put("$redact", "encryptedPassphrase");
        commonConfigJson.put("keyPath", "");
        commonConfigJson.put("disableAllExec", false);
        String key = "-----BEGIN RSA PRIVATE KEY-----\nMIIEpAIBAAKCAQEA04TlFwsOrYhrq7WSTP17Jxzl2V0EpWaX9LODr3ZJNWGjUkx1\naT+UUDw6rKYwsO0JdoDPliqAmZVSu9nC1aiT9FhMnsYaYkXTaOedhVdEUKJWc6ZP\nR2uzdVV2Wwbk9wjBMFf+Lc1A2XqkEAV9OQXN203sZeNJ/8TptU4SWbsqxsLg0enG\nNKAfZ9qL2bgdEwSLtDA+9Qx1Kl95ktlEXu5ne3AUoKlYM4y/LmsWJ1+exNm1J0R/\niCPjvq6/F0ZenLL63cQ8tGdghc4D39iRK6bWEIedJb4Avg2vmzzkIGFHC87VgCi7\n/k2yAh0zaIVGFqmanMtSg0ihSVMYWrpK2/l+HQIDAQABAoIBAQCOe8AipKUvim0V\nTwuNZ1c1Qscmg/1kOdb01JRJdwHbvrjY8H5K9rQ+1EOmF5FHLXWpaR3tBxZ33tnL\nhuYzLQr1lyGN9t7BAk5mJVe8AcwYETLxVr+i2c8apAZFTUChlDknCq9DRTeBCJdJ\n35i598nzB+vNuq0XaRsRsdS0s1seZeCAer2qcJANQPrtr9ZuwAM0IVF4qlGi4c9M\n3/xGxU/+f8F2Mr1RCcLadJaPJ9kmyM9C3zxfb/+uGu5BM0JUgGtMiTg9W2puJe3k\n/iiaCgiVGyinTaELonyu5wtFrPe/M5mSLReSEXHRTAZazomg2CqvJj5/IqvWUcPk\nApnUakshAoGBAPDlZYARui3WoeawfnaaeKhkegeGgo9lJ0F+Y3tEbBZC6jsySHTG\nBPjGdnzZ7ZN4LoCGxqCdH6mElvhk4dlMECLyLzwGlRMxaWRuHwgmUHMk4JsUFYKZ\n5DPjM+s2RVkjLVskIxZQgCGBp5+QKfCL9qbf1ZkP1Z3fMLtpPR2horzlAoGBAODH\n+KBQAvm0PXaBeQrK6/Z+NZtovbbFFuXmqk3zKqEJZD7XS46w723qPZOmcXfg5eyO\nLRkGJCS2dlFeaU08cLg92j9d1yyfARYGRz+2DumY11HA5v7+u8MeCyDjJi3eW+wu\nBaYODqyUxINO07n7EczWf4v3/d2wBhY75ACH0+DZAoGAcpCmdQH5SVOKK+xEOKO+\nPleKsYmHDitNQBibt0QTI4MvYnfHfcGvG6FHOJlsI3ZEp1txm3EeXcBxDDDuOCm0\nguorDKEUxMv7E4hLudR+7kPbjeU/VZ1aqlKjnnlxAbN5Hp9REIu4ZDcFQR2O1Je7\n9iD9tI2TSkGbK4YhSrZUbwECgYEAv3My3QAo+Js+ek/eU4XwIhru97XnV+NYFGo5\nhXjmRxCjtC5VaWxkEX8gAUETheIkky2pVZX4dNh/v+Ak2ibvbs0ntS7tFVE8cJDa\nYHZlDyshwb0GQgueypotKk6t9wVbz7aHEx8H+pAfLCOYi+A/EfNj4UhQjl/dX1SV\nZqpJmUkCgYBz+Y5Ch9/m2j/JPNTR07YCO0Aff5nna/JrfFbP5aKwTSTf+kqZDSDw\neYzp4qmK5aj5LhWLmftV/f4jdnlgFIEFUz8lgMpAE1tB7XBh6jCWIxvXsVae2IyV\nZ1NX58rtzLBBQZohTDyCMGxuYxvwaBEcfuMe017Byd8FLQwgo8RyyA==\n-----END RSA PRIVATE KEY-----";
        String plugEncode = URLEncoder.encode("+", "UTF-8");
        key = key.replaceAll("\\+", plugEncode);
        commonConfigJson.put("key", key);
        //构造默认配置
        JSONObject defaultJson = new JSONObject();
        defaultJson.put("stapler-class", "jenkins.plugins.publish_over_ssh.options.SshPluginDefaults");
        defaultJson.put("$class", "jenkins.plugins.publish_over_ssh.options.SshPluginDefaults");
        //插件sjon
        JSONObject posshjson = new JSONObject();
        posshjson.put("commonConfig", commonConfigJson);
        posshjson.put("instance", instanceJsonArray);
        posshjson.put("defaults", defaultJson);
        //整体配置json
        JSONObject json = new JSONObject();
        json.put("jenkins-plugins-publish_over_ssh-BapSshPublisherPlugin", posshjson);
        //其他基本空配置
        //1
        JSONObject baseJson = new JSONObject();
        baseJson.put("numExecutors", "2");
        baseJson.put("labelString", "");
        json.put("jenkins-model-MasterBuildConfiguration", baseJson);
        //2
        baseJson = new JSONObject();
        baseJson.put("quietPeriod", "5");
        json.put("jenkins-model-GlobalQuietPeriodConfiguration", baseJson);
        //3
        baseJson = new JSONObject();
        baseJson.put("scmCheckoutRetryCount", "0");
        json.put("jenkins-model-GlobalSCMRetryCountConfiguration", baseJson);
        //formData
        String formData = "core:apply=true&json=" + json.toJSONString();
        this.jenkinsComponent.updateConfig(formData);
    }

    private JSONObject createNodeInstanceConfig(NodeEntity nodeEntity)
    {
        JSONObject instanceJson = new JSONObject();
        instanceJson.put("name", nodeEntity.name);
        instanceJson.put("hostname", nodeEntity.hostname);
        instanceJson.put("username", "root");
        instanceJson.put("remoteRootDir", "/");
        instanceJson.put("overrideKey", false);
        instanceJson.put("encryptedPassword", emptyPasswordEncrypted);
        instanceJson.put("keyPath", "");
        instanceJson.put("key", "");
        instanceJson.put("jumpHost", "");
        instanceJson.put("port", "22");
        instanceJson.put("timeout", "60000");
        instanceJson.put("disableExec", false);
        instanceJson.put("proxyType", "");
        instanceJson.put("proxyHost", "");
        instanceJson.put("proxyPort", "0");
        instanceJson.put("proxyUser", "");
        instanceJson.put("proxyPassword", emptyPasswordEncrypted);
        //
        JSONArray jsonArray = new JSONArray();
        jsonArray.add("encryptedPassword");
        jsonArray.add("proxyPassword");
        instanceJson.put("$redact", jsonArray);
        return instanceJson;
    }
}
