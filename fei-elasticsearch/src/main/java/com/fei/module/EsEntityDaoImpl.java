package com.fei.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.fei.app.utils.ToolUtil;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class EsEntityDaoImpl<T> extends AbstractEsDao<T> implements EsEntityDao<T>
{

    private final EsKeyHandler keyHandler;

    public EsEntityDaoImpl(
            String index,
            EsKeyHandler keyHandler,
            List<EsColumnHandler> columnHandlerList,
            Class<T> clazz)
    {
        super(index, columnHandlerList, clazz);
        this.keyHandler = keyHandler;
    }

    private String getDocPath(String keyValue)
    {
        String path = "/" + index + "/_doc/" + keyValue;
        return path;
    }

    private String getInsertDocPath(String keyValue)
    {
        String path = "/" + index + "/_create/" + keyValue + "?refresh";
        return path;
    }

    private String getDeleteDocPath(String keyValue)
    {
        String path = "/" + index + "/_doc/" + keyValue + "?refresh";
        return path;
    }

    private String getUpdateDocPath(String keyValue)
    {
        String path = "/" + index + "/_update/" + keyValue + "?refresh";
        return path;
    }

    @Override
    public boolean exist(String keyValue)
    {
        boolean exist;
        String path = this.getDocPath(keyValue);
        try {
            Request request = new Request("GET", path);
            Response response = EsContext.INSTANCE.getRestClient().performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject responseJson = JSON.parseObject(responseBody);
            exist = responseJson.containsKey("_source");
        } catch (ResponseException ex) {
            exist = false;
        } catch (IOException ex) {
            this.logger.error("es client: exec exist error", ex);
            throw new RuntimeException("unknown es error");
        }
        return exist;
    }

    @Override
    public T get(String keyValue)
    {
        T t;
        String path = this.getDocPath(keyValue);
        try {
            Request request = new Request("GET", path);
            Response response = EsContext.INSTANCE.getRestClient().performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject responseJson = JSON.parseObject(responseBody);
            JSONObject entityJson = responseJson.getJSONObject("_source");
            //如果属性不存在,则赋值默认值
            this.checkDefaultValue(entityJson);
            t = TypeUtils.castToJavaBean(entityJson, this.clazz);
        } catch (ResponseException ex) {
            t = null;
        } catch (IOException ex) {
            this.logger.error("es client: exec get error", ex);
            throw new RuntimeException("unknown es error");
        }
        return t;
    }

    @Override
    public void insert(T t)
    {
        JSONObject tJson = JSON.parseObject(JSON.toJSONStringWithDateFormat(t, ToolUtil.DATE_FORMAT));
        String keyValue = tJson.getString(this.keyHandler.getName());
        if (keyValue == null || keyValue.isEmpty()) {
            if (this.keyHandler.isAuto()) {
                keyValue = ToolUtil.getAutomicId();
                tJson.put(this.keyHandler.getName(), keyValue);
                this.keyHandler.setValue(t, keyValue);
            }
        }
        if (keyValue == null || keyValue.isEmpty()) {
            this.logger.error("{} insert miss keyValue:{}", clazz.getName(), this.keyHandler.getName());
            throw new RuntimeException("insert miss keyValue");
        }
        //时间戳
        Date timestamp = new Date();
        tJson.put(this.timestampName, ToolUtil.format(timestamp));
        //
        String path = this.getInsertDocPath(keyValue);
        Request request = new Request("PUT", path);
        request.setJsonEntity(tJson.toJSONString());
        try {
            EsContext.INSTANCE.getRestClient().performRequest(request);
        } catch (IOException ex) {
            this.logger.error("es client: exec insert error", ex);
            throw new RuntimeException("unknown es error");
        }
    }

    @Override
    public void update(T t)
    {
        JSONObject tJson = JSON.parseObject(JSON.toJSONStringWithDateFormat(t, ToolUtil.DATE_FORMAT));
        String keyValue = tJson.getString(this.keyHandler.getName());
        if (keyValue == null || keyValue.isEmpty()) {
            this.logger.error("{} update miss keyValue:{}", clazz.getName(), this.keyHandler.getName());
            throw new RuntimeException("update miss keyValue");
        }
        tJson.remove(this.keyHandler.getName());
        JSONObject requestJson = new JSONObject();
        requestJson.put("doc", tJson);
        String path = this.getUpdateDocPath(keyValue);
        try {
            Request request = new Request("POST", path);
            request.setJsonEntity(requestJson.toJSONString());
            EsContext.INSTANCE.getRestClient().performRequest(request);
        } catch (IOException ex) {
            this.logger.error("es client: exec update error", ex);
            throw new RuntimeException("unknown es error");
        }
    }

    @Override
    public void update(String keyValue, Map<String, Object> updateMap)
    {
        if (keyValue == null || keyValue.isEmpty()) {
            this.logger.error("{} update miss keyValue:{}", clazz.getName(), this.keyHandler.getName());
            throw new RuntimeException("update miss keyValue");
        }
        JSONObject tJson = new JSONObject();
        //只更新有定义的字段
        Object value;
        for (EsColumnHandler esColumnHandler : this.columnHandlerList) {
            value = updateMap.get(esColumnHandler.getName());
            if (value != null) {
                if (esColumnHandler.getColumnType().equals(EsColumnType.DATE)) {
                    value = ToolUtil.format((Date) value);
                }
                tJson.put(esColumnHandler.getName(), value);
            }
        }
        //
        if (tJson.isEmpty() == false) {
            JSONObject requestJson = new JSONObject();
            requestJson.put("doc", tJson);
            String path = this.getUpdateDocPath(keyValue);
            try {
                Request request = new Request("POST", path);
                request.setJsonEntity(requestJson.toJSONString());
                EsContext.INSTANCE.getRestClient().performRequest(request);
            } catch (IOException ex) {
                this.logger.error("es client: exec update error", ex);
                throw new RuntimeException("unknown es error");
            }
        }
    }

    @Override
    public void upsert(T t)
    {
        JSONObject tJson = JSON.parseObject(JSON.toJSONStringWithDateFormat(t, ToolUtil.DATE_FORMAT));
        String keyValue = tJson.getString(this.keyHandler.getName());
        if (keyValue == null || keyValue.isEmpty()) {
            if (this.keyHandler.isAuto()) {
                keyValue = ToolUtil.getAutomicId();
                tJson.put(this.keyHandler.getName(), keyValue);
                this.keyHandler.setValue(t, keyValue);
            }
        }
        if (keyValue == null || keyValue.isEmpty()) {
            this.logger.error("{} upsert miss keyValue:{}", clazz.getName(), this.keyHandler.getName());
            throw new RuntimeException("upsert miss keyValue");
        }
        //时间戳
        Date timestamp = new Date();
        tJson.put(this.timestampName, ToolUtil.format(timestamp));
        //
        JSONObject requestJson = new JSONObject();
        requestJson.put("doc", tJson);
        requestJson.put("doc_as_upsert", true);
        String path = this.getUpdateDocPath(keyValue);
        try {
            Request request = new Request("POST", path);
            request.setJsonEntity(requestJson.toJSONString());
            EsContext.INSTANCE.getRestClient().performRequest(request);
        } catch (IOException ex) {
            this.logger.error("es client: exec upsert error", ex);
            throw new RuntimeException("unknown es error");
        }
    }

    @Override
    public void delete(String keyValue)
    {
        String path = this.getDeleteDocPath(keyValue);
        try {
            Request request = new Request("DELETE", path);
            EsContext.INSTANCE.getRestClient().performRequest(request);
        } catch (IOException ex) {
            this.logger.error("es client: exec delete error", ex);
            throw new RuntimeException("unknown es error");
        }
    }

    private boolean existIndex()
    {
        boolean exist;
        String path = "/" + this.index;
        Request request = new Request("HEAD", path);
        Response response;
        try {
            response = EsContext.INSTANCE.getRestClient().performRequest(request);
            int code = response.getStatusLine().getStatusCode();
            exist = code == 200;
        } catch (ResponseException ex) {
            exist = false;
        } catch (IOException ex) {
            this.logger.error("es client: exec exist index error", ex);
            throw new RuntimeException("unknown es error");
        }
        return exist;
    }

    private void createIndex()
    {
        //settints
        JSONObject settingsJson = new JSONObject();
        //最大返回查询记录数量
        settingsJson.put("max_result_window", 60000);
        //mappings
        JSONObject propertiesJson = new JSONObject();
        propertiesJson.put(this.keyHandler.getName(), this.keyHandler.getProperty());
        for (EsColumnHandler esColumnHandler : columnHandlerList) {
            propertiesJson.put(esColumnHandler.getName(), esColumnHandler.getProperty());
        }
        //增加时间戳
        propertiesJson.put(this.timestampName, EsColumnHandler.getTimestampProperty());
        //7.x后include_type_name默认为false,不需要指定type,type="_doc"
        JSONObject mappingsJson = new JSONObject();
        mappingsJson.put("properties", propertiesJson);
        //关闭自动日期检测
        mappingsJson.put("date_detection", false);
        //
        JSONObject requestJson = new JSONObject();
        requestJson.put("settings", settingsJson);
        requestJson.put("mappings", mappingsJson);

        String path = "/" + index;
        Request request = new Request("PUT", path);
        request.setJsonEntity(requestJson.toJSONString());
        try {
            EsContext.INSTANCE.getRestClient().performRequest(request);
        } catch (IOException ex) {
            this.logger.error("es client: exec create index error", ex);
            throw new RuntimeException("unknown es error");
        }
    }

    /**
     * 更新索引配置,如果生命周期不为空,则使用date stream模式
     */
    @Override
    public void setUp()
    {
        //index模式
        boolean exist = this.existIndex();
        if (exist == false) {
            this.createIndex();
        } else {
            //properties
            JSONObject propertiesJson = new JSONObject();
            propertiesJson.put(this.keyHandler.getName(), this.keyHandler.getProperty());
            for (EsColumnHandler esColumnHandler : columnHandlerList) {
                propertiesJson.put(esColumnHandler.getName(), esColumnHandler.getProperty());
            }
            JSONObject requestJson = new JSONObject();
            requestJson.put("properties", propertiesJson);
            //7.x后include_type_name默认为false,不需要指定type,type="_doc"
            String path = "/" + index + "/_mapping";
            Request request = new Request("PUT", path);
            request.setJsonEntity(requestJson.toJSONString());
            try {
                Response response = EsContext.INSTANCE.getRestClient().performRequest(request);
                EntityUtils.toString(response.getEntity());
            } catch (IOException ex) {
                this.logger.error("es client: exec update mapping error", ex);
                throw new RuntimeException("unknown es error");
            }
        }
    }

}
