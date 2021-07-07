package com.fei.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.fei.elasticsearch.index.query.QueryBuilder;
import com.fei.elasticsearch.search.sort.SortBuilder;
import com.fei.app.utils.ToolUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class EsEntityDaoImpl<T> implements EsEntityDao<T>
{

    private final Logger logger = LoggerFactory.getLogger(EsContext.class);
    private final EsKeyHandler keyHandler;
    private final List<EsColumnHandler> columnHandlerList;
    private final Class<T> clazz;
    private final String index;

    public EsEntityDaoImpl(
            String index,
            EsKeyHandler keyHandler,
            List<EsColumnHandler> columnHandlerList,
            Class<T> clazz)
    {
        this.columnHandlerList = columnHandlerList;
        this.keyHandler = keyHandler;
        this.index = index;
        this.clazz = clazz;
    }

    @Override
    public String getIndex()
    {
        return this.index;
    }

    @Override
    public int total()
    {
        int result = 0;
        String path = index + "/_search";
        JSONObject requestJson = new JSONObject();
        requestJson.put("from", 0);
        requestJson.put("size", 1);
        Request request = new Request("POST", path);
        request.setJsonEntity(requestJson.toJSONString());
        try {
            Response response = EsContext.INSTANCE.getRestClient().performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject responseJson = JSON.parseObject(responseBody);
            JSONObject hitsJson = responseJson.getJSONObject("hits");
            JSONObject totalJson = hitsJson.getJSONObject("total");
            result = totalJson.getIntValue("value");
        } catch (IOException ex) {
            this.logger.error("es client: exec total error", ex);
            throw new RuntimeException("unknown es error");
        }
        return result;
    }

    @Override
    public boolean exist(Object keyValue)
    {
        boolean exist = false;
        String id = keyValue.toString();
        String path = index + "/_doc/" + id;
        try {
            Request request = new Request("GET", path);
            Response response = EsContext.INSTANCE.getRestClient().performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject responseJson = JSON.parseObject(responseBody);
            exist = responseJson.containsKey("_source");
        } catch (IOException ex) {
            this.logger.error("es client: exec exist error", ex);
            throw new RuntimeException("unknown es error");
        }
        return exist;
    }

    @Override
    public T get(Object keyValue)
    {
        T t = null;
        String id = keyValue.toString();
        String path = index + "/_doc/" + id;
        try {
            Request request = new Request("GET", path);
            Response response = EsContext.INSTANCE.getRestClient().performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject responseJson = JSON.parseObject(responseBody);
            JSONObject entityJson = responseJson.getJSONObject("_source");
            //如果属性不存在,则赋值默认值
            this.checkDefaultValue(entityJson);
            t = TypeUtils.castToJavaBean(entityJson, this.clazz);
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
        Object keyValue = tJson.get(this.keyHandler.getName());
        if (keyValue == null || keyValue.toString().isEmpty()) {
            if (this.keyHandler.isAuto()) {
                keyValue = ToolUtil.getAutomicId();
                tJson.put(this.keyHandler.getName(), keyValue);
                this.keyHandler.setValue(t, keyValue);
            }
        }
        if (keyValue == null || keyValue.toString().isEmpty()) {
            this.logger.error("{} insert miss keyValue:{}", clazz.getName(), this.keyHandler.getName());
            throw new RuntimeException("insert miss keyValue");
        }
        String id = keyValue.toString();
        String path = index + "/_doc/" + id + "?refresh";
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
        Object keyValue = tJson.get(this.keyHandler.getName());
        if (keyValue == null || keyValue.toString().isEmpty()) {
            this.logger.error("{} update miss keyValue:{}", clazz.getName(), this.keyHandler.getName());
            throw new RuntimeException("update miss keyValue");
        }
        tJson.remove(this.keyHandler.getName());
        JSONObject requestJson = new JSONObject();
        requestJson.put("doc", tJson);
        String id = keyValue.toString();
        String path = index + "/_update/" + id + "?refresh";
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
            String id = keyValue.toString();
            String path = index + "/_update/" + id + "?refresh";
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
        Object keyValue = tJson.get(this.keyHandler.getName());
        if (keyValue == null || keyValue.toString().isEmpty()) {
            if (this.keyHandler.isAuto()) {
                keyValue = ToolUtil.getAutomicId();
                tJson.put(this.keyHandler.getName(), keyValue);
                this.keyHandler.setValue(t, keyValue);
            }
        }
        if (keyValue == null || keyValue.toString().isEmpty()) {
            this.logger.error("{} upsert miss keyValue:{}", clazz.getName(), this.keyHandler.getName());
            throw new RuntimeException("upsert miss keyValue");
        }
        String id = keyValue.toString();
        JSONObject requestJson = new JSONObject();
        requestJson.put("doc", tJson);
        requestJson.put("doc_as_upsert", true);
        String path = index + "/_update/" + id + "?refresh";
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
    public void delete(Object keyValue)
    {
        String id = keyValue.toString();
        String path = index + "/_doc/" + id + "?refresh";
        try {
            Request request = new Request("DELETE", path);
            EsContext.INSTANCE.getRestClient().performRequest(request);
        } catch (IOException ex) {
            this.logger.error("es client: exec delete error", ex);
            throw new RuntimeException("unknown es error");
        }
    }

    private void checkDefaultValue(JSONObject sourceJson)
    {
        for (EsColumnHandler esColumnHandler : columnHandlerList) {
            if (sourceJson.containsKey(esColumnHandler.getName()) == false) {
                sourceJson.put(esColumnHandler.getName(), esColumnHandler.getDefaultValue());
            }
        }
    }

    /**
     *
     * @param queryBuilder
     * @param sort
     * @param from
     * @param size
     * @return
     */
    @Override
    public List<T> search(QueryBuilder queryBuilder, SortBuilder sort, int from, int size)
    {
        List<T> tList = Collections.EMPTY_LIST;
        String path = index + "/_search";
        JSONObject requestJson = new JSONObject();
        if (queryBuilder != null) {
            requestJson.put("query", queryBuilder.toJSONObject());
        }
        requestJson.put("from", from);
        requestJson.put("size", size);
        if (sort != null) {
            JSONArray sortJson = new JSONArray();
            sortJson.add(sort.toJSONObject());
            requestJson.put("sort", sortJson);
        }
        try {
            Request request = new Request("POST", path);
            request.setJsonEntity(requestJson.toJSONString());
            Response response = EsContext.INSTANCE.getRestClient().performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject responseJson = JSON.parseObject(responseBody);
            JSONObject hitsJson = responseJson.getJSONObject("hits");
            JSONArray hitArray = hitsJson.getJSONArray("hits");
            JSONObject sourceJson;
            JSONObject entityJson;
            T t;
            tList = new ArrayList(hitArray.size());
            for (int i = 0; i < hitArray.size(); i++) {
                sourceJson = hitArray.getJSONObject(i);
                entityJson = sourceJson.getJSONObject("_source");
                //如果属性不存在,则赋值默认值
                this.checkDefaultValue(entityJson);
                t = TypeUtils.castToJavaBean(entityJson, this.clazz);
                tList.add(t);
            }
        } catch (IOException ex) {
            this.logger.error("es client: exec search error", ex);
            throw new RuntimeException("unknown es error");
        }
        return tList;
    }

    @Override
    public List<T> search(QueryBuilder queryBuilder, List<SortBuilder> sortList, int from, int size)
    {
        List<T> tList = Collections.EMPTY_LIST;
        String path = index + "/_search";
        JSONObject requestJson = new JSONObject();
        if (queryBuilder != null) {
            requestJson.put("query", queryBuilder.toJSONObject());
        }
        requestJson.put("from", from);
        requestJson.put("size", size);
        if (sortList != null && sortList.isEmpty() == false) {
            JSONArray sortJson = new JSONArray();
            for (SortBuilder sortBuilder : sortList) {
                sortJson.add(sortBuilder.toJSONObject());
            }
            requestJson.put("sort", sortJson);
        }
        try {
            Request request = new Request("POST", path);
            request.setJsonEntity(requestJson.toJSONString());
            Response response = EsContext.INSTANCE.getRestClient().performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject responseJson = JSON.parseObject(responseBody);
            JSONObject hitsJson = responseJson.getJSONObject("hits");
            JSONArray hitArray = hitsJson.getJSONArray("hits");
            JSONObject sourceJson;
            JSONObject entityJson;
            T t;
            tList = new ArrayList(hitArray.size());
            for (int i = 0; i < hitArray.size(); i++) {
                sourceJson = hitArray.getJSONObject(i);
                entityJson = sourceJson.getJSONObject("_source");
                //如果属性不存在,则赋值默认值
                this.checkDefaultValue(entityJson);
                t = TypeUtils.castToJavaBean(entityJson, this.clazz);
                tList.add(t);
            }
        } catch (IOException ex) {
            this.logger.error("es client: exec search error", ex);
            throw new RuntimeException("unknown es error");
        }
        return tList;
    }

    @Override
    public List<T> search(QueryBuilder queryBuilder, int from, int size)
    {
        SortBuilder sort = null;
        return this.search(queryBuilder, sort, from, size);
    }

    @Override
    public List<T> search(SortBuilder sort, int from, int size)
    {
        return this.search(null, sort, from, size);
    }

    @Override
    public List<T> search(int from, int size)
    {
        SortBuilder sort = null;
        return this.search(null, sort, from, size);
    }

    @Override
    public List<T> search(QueryBuilder queryBuilder)
    {
        int size = 100;
        int from = 0;
        SortBuilder sort = null;
        return this.search(queryBuilder, sort, from, size);
    }

    private boolean existIndex()
    {
        boolean exist = false;
        String path = this.index;
        Request request = new Request("HEAD", path);
        Response response;
        try {
            response = EsContext.INSTANCE.getRestClient().performRequest(request);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                exist = true;
            }
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
        settingsJson.put("max_result_window", 60000);
        //mappings
        JSONObject propertiesJson = new JSONObject();
        propertiesJson.put(this.keyHandler.getName(), this.keyHandler.getProperty());
        for (EsColumnHandler esColumnHandler : columnHandlerList) {
            propertiesJson.put(esColumnHandler.getName(), esColumnHandler.getProperty());
        }
        //7.x后include_type_name默认为false,不需要指定type,type="_doc"
        JSONObject mappingsJson = new JSONObject();
        mappingsJson.put("properties", propertiesJson);
        //
        JSONObject requestJson = new JSONObject();
        requestJson.put("settings", settingsJson);
        requestJson.put("mappings", mappingsJson);

        String path = index;
        Request request = new Request("PUT", path);
        request.setJsonEntity(requestJson.toJSONString());
        try {
            EsContext.INSTANCE.getRestClient().performRequest(request);
        } catch (IOException ex) {
            this.logger.error("es client: exec create index error", ex);
            throw new RuntimeException("unknown es error");
        }
    }

    public void updateMapping()
    {
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
            String path = index + "/_mapping";
            Request request = new Request("PUT", path);
            request.setJsonEntity(requestJson.toJSONString());
            try {
                EsContext.INSTANCE.getRestClient().performRequest(request);
            } catch (IOException ex) {
                this.logger.error("es client: exec update mapping error", ex);
                throw new RuntimeException("unknown es error");
            }
        }
    }

}
