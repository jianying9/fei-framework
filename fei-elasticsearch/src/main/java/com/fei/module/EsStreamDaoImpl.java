package com.fei.module;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fei.app.utils.ToolUtil;
import static com.fei.module.EsContext.TIMESTAMP_COLUMN_NAME;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.ResponseException;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class EsStreamDaoImpl<T> extends AbstractEsDao<T> implements EsStreamDao<T>
{

    private final String lifecycle;

    public EsStreamDaoImpl(
            String index,
            String lifecycle,
            List<EsColumnHandler> columnHandlerList,
            Class<T> clazz)
    {
        super(index, columnHandlerList, clazz);
        this.lifecycle = lifecycle;
    }

    private String getInsertDocPath(String keyValue)
    {
        String path = "/" + index + "/_create/" + keyValue;
        return path;
    }

    @Override
    public void insert(T t)
    {
        JSONObject eJson = this.t2e(t);
        //时间戳
        Date timestamp = new Date();
        eJson.put(TIMESTAMP_COLUMN_NAME, ToolUtil.format(timestamp));
        //
        String keyValue = ToolUtil.getAutomicId();
        String path = this.getInsertDocPath(keyValue);
        Request request = new Request("PUT", path);
        request.setJsonEntity(eJson.toJSONString());
        try {
            EsContext.INSTANCE.getRestClient().performRequest(request);
        } catch (IOException ex) {
            this.logger.error("es client: exec insert error", ex);
            throw new RuntimeException("unknown es error");
        }
    }

    /**
     * 生命周期策略是否存在
     *
     * @return
     */
    private boolean existPolicy()
    {
        boolean exist;
        String path = "/_ilm/policy/" + this.lifecycle;
        try {
            Request request = new Request("GET", path);
            EsContext.INSTANCE.getRestClient().performRequest(request);
            exist = true;
        } catch (ResponseException ex) {
            exist = false;
        } catch (IOException ex) {
            this.logger.error("es client: exec get error", ex);
            throw new RuntimeException("unknown es error");
        }
        return exist;
    }

    /**
     * 创建生命周期
     */
    private void createPolicy()
    {
        //rollover
        JSONObject rolloverJson = new JSONObject();
        rolloverJson.put("max_primary_shard_size", "10gb");
        rolloverJson.put("max_age", "30d");
        JSONObject hotActionJson = new JSONObject();
        hotActionJson.put("rollover", rolloverJson);
        //hot phase
        JSONObject hotPhaseJson = new JSONObject();
        hotPhaseJson.put("min_age", "0ms");
        hotPhaseJson.put("actions", hotActionJson);
        //delete
        JSONObject deleteJson = new JSONObject();
        deleteJson.put("delete_searchable_snapshot", true);
        JSONObject deleteActionJson = new JSONObject();
        deleteActionJson.put("delete", deleteJson);
        //delete phase
        JSONObject deletePhaseJson = new JSONObject();
        deletePhaseJson.put("min_age", "90d");
        deletePhaseJson.put("actions", deleteActionJson);
        //phase
        JSONObject phasesJson = new JSONObject();
        phasesJson.put("hot", hotPhaseJson);
        phasesJson.put("delete", deletePhaseJson);
        //policy
        JSONObject policyJson = new JSONObject();
        policyJson.put("phases", phasesJson);
        JSONObject requestJson = new JSONObject();
        requestJson.put("policy", policyJson);
        //
        String path = "/_ilm/policy/" + this.lifecycle;
        try {
            Request request = new Request("PUT", path);
            request.setJsonEntity(requestJson.toJSONString());
            EsContext.INSTANCE.getRestClient().performRequest(request);
        } catch (IOException ex) {
            this.logger.error("es client: exec get error", ex);
            throw new RuntimeException("unknown es error");
        }
    }

    /**
     * 更新模板
     */
    @Override
    public void setUp()
    {
        //判断生命周期是否存在
        boolean exist = this.existPolicy();
        if (exist == false) {
            //不存在,则新增
            this.createPolicy();
        }
        //创建或更新索引模板
        //patterns
        JSONArray indexPatternsJson = new JSONArray();
        indexPatternsJson.add(this.index + "*");
        //data stream
        JSONObject dataStreamJson = new JSONObject();
        dataStreamJson.put("hidden", false);
        //settints
        JSONObject settingsJson = new JSONObject();
        settingsJson.put("refresh_interval", "5s");
        settingsJson.put("number_of_shards", "1");
        settingsJson.put("number_of_routing_shards", "30");
        settingsJson.put("codec", "best_compression");
        JSONObject lifecycleJson = new JSONObject();
        lifecycleJson.put("name", this.lifecycle);
        settingsJson.put("lifecycle", lifecycleJson);
        //mappings
        JSONObject propertiesJson = new JSONObject();
        for (EsColumnHandler esColumnHandler : columnHandlerList) {
            propertiesJson.put(esColumnHandler.getColumnName(), esColumnHandler.getProperty());
        }
        JSONObject mappingsJson = new JSONObject();
        mappingsJson.put("properties", propertiesJson);
        //关闭自动日期检测
        mappingsJson.put("date_detection", false);
        //
        JSONObject templateJson = new JSONObject();
        templateJson.put("settings", settingsJson);
        templateJson.put("mappings", mappingsJson);
        //
        JSONObject requestJson = new JSONObject();
        requestJson.put("index_patterns", indexPatternsJson);
        requestJson.put("priority", 500);
        requestJson.put("data_stream", dataStreamJson);
        requestJson.put("template", templateJson);
        //
        String path = "/_index_template/" + index;
        Request request = new Request("PUT", path);
        request.setJsonEntity(requestJson.toJSONString());
        try {
            EsContext.INSTANCE.getRestClient().performRequest(request);
        } catch (IOException ex) {
            this.logger.error("es client: exec create index error", ex);
            throw new RuntimeException("unknown es error");
        }
    }

}
