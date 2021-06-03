package com.fei.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.fei.elasticsearch.index.query.QueryBuilder;
import com.fei.elasticsearch.search.sort.SortBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;

/**
 *
 * @author jianying9
 * @param <T>
 */
public class EsEntityDaoImpl<T> implements EsEntityDao<T>
{

    private final Logger logger = LogManager.getLogger(EsContext.class);
    protected final EsColumnHandler keyHandler;
    protected final List<EsColumnHandler> columnHandlerList;
    protected final Class<T> clazz;
    protected final String index;
    protected final String type;

    public EsEntityDaoImpl(
            String index,
            String type,
            EsColumnHandler keyHandler,
            List<EsColumnHandler> columnHandlerList,
            Class<T> clazz)
    {
        this.columnHandlerList = columnHandlerList;
        this.keyHandler = keyHandler;
        this.index = index;
        this.type = type;
        this.clazz = clazz;
    }

    @Override
    public String getIndex()
    {
        return this.index;
    }

    @Override
    public String getType()
    {
        return this.type;
    }

    @Override
    public int total()
    {
        int result = 0;
        String path = "/" + index + "/" + type + "/_search";
        JSONObject requestJson = new JSONObject();
        requestJson.put("from", 0);
        requestJson.put("size", 1);
        Request request = new Request("POST", path);
        request.setJsonEntity(requestJson.toJSONString());
        try {
            Response response = EsContext.CONTEXT.getRestClient().performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject responseJson = JSON.parseObject(responseBody);
            JSONObject hitsJson = responseJson.getJSONObject("hits");
            result = hitsJson.getIntValue("total");
        } catch (IOException ex) {
            this.logger.error(ex);
        }
        return result;
    }

    @Override
    public boolean exist(Object keyValue)
    {
        boolean exist = false;
        String id = keyValue.toString();
        String path = "/" + index + "/" + type + "/" + id;
        try {
            Request request = new Request("GET", path);
            Response response = EsContext.CONTEXT.getRestClient().performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject responseJson = JSON.parseObject(responseBody);
            exist = responseJson.containsKey("_source");
        } catch (IOException ex) {
            this.logger.error(ex);
        }
        return exist;
    }

    @Override
    public T get(Object keyValue)
    {
        T t = null;
        String id = keyValue.toString();
        String path = "/" + index + "/" + type + "/" + id;
        try {
            Request request = new Request("GET", path);
            Response response = EsContext.CONTEXT.getRestClient().performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject responseJson = JSON.parseObject(responseBody);
            JSONObject entityJson = responseJson.getJSONObject("_source");
            //如果属性不存在,则赋值默认值
            this.checkDefaultValue(entityJson);
            t = TypeUtils.castToJavaBean(entityJson, this.clazz);
        } catch (IOException ex) {
            this.logger.error(ex);
        }
        return t;
    }

    @Override
    public void insert(T t)
    {
        JSONObject tJson = JSON.parseObject(JSON.toJSONString(t));
        Object keyValue = tJson.get(this.keyHandler.getName());
        String id = keyValue.toString();
        String path = "/" + index + "/" + type + "/" + id + "?refresh=true";
        Request request = new Request("PUT", path);
        request.setJsonEntity(tJson.toJSONString());
        try {
            EsContext.CONTEXT.getRestClient().performRequest(request);
        } catch (IOException ex) {
            this.logger.error(ex);
        }
    }

    @Override
    public void update(T t)
    {
        JSONObject tJson = JSON.parseObject(JSON.toJSONString(t));
        Object keyValue = tJson.get(this.keyHandler.getName());
        JSONObject requestJson = new JSONObject();
        requestJson.put("doc", tJson);
        String id = keyValue.toString();
        String path = "/" + index + "/" + type + "/" + id + "/_update?refresh=true";
        try {
            Request request = new Request("POST", path);
            request.setJsonEntity(requestJson.toJSONString());
            EsContext.CONTEXT.getRestClient().performRequest(request);
        } catch (IOException ex) {
            this.logger.error(ex);
        }
    }

    @Override
    public void update(Object keyValue, Map<String, Object> updateMap)
    {
        JSONObject tJson = new JSONObject();
        //只更新有定义的字段
        Object value;
        for (EsColumnHandler esColumnHandler : this.columnHandlerList) {
            value = updateMap.get(esColumnHandler.getName());
            if (value != null) {
                tJson.put(esColumnHandler.getName(), value);
            }
        }
        //
        if (tJson.isEmpty() == false) {
            JSONObject requestJson = new JSONObject();
            requestJson.put("doc", tJson);
            String id = keyValue.toString();
            String path = "/" + index + "/" + type + "/" + id + "/_update?refresh=true";
            try {
                Request request = new Request("POST", path);
                request.setJsonEntity(requestJson.toJSONString());
                EsContext.CONTEXT.getRestClient().performRequest(request);
            } catch (IOException ex) {
                this.logger.error(ex);
            }
        }
    }

    @Override
    public void upsert(T t)
    {
        JSONObject tJson = JSON.parseObject(JSON.toJSONString(t));
        Object keyValue = tJson.get(this.keyHandler.getName());
        String id = keyValue.toString();
        JSONObject requestJson = new JSONObject();
        requestJson.put("doc", tJson);
        requestJson.put("doc_as_upsert", true);
        String path = "/" + index + "/" + type + "/" + id + "/_update?refresh=true";
        try {
            Request request = new Request("POST", path);
            request.setJsonEntity(requestJson.toJSONString());
            EsContext.CONTEXT.getRestClient().performRequest(request);
        } catch (IOException ex) {
            this.logger.error(ex);
        }
    }

    @Override
    public void delete(Object keyValue)
    {
        String id = keyValue.toString();
        String path = "/" + index + "/" + type + "/" + id + "?refresh=true";
        try {
            Request request = new Request("DELETE", path);
            EsContext.CONTEXT.getRestClient().performRequest(request);
        } catch (IOException ex) {
            this.logger.error(ex);
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
        String path = "/" + index + "/" + type + "/_search";
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
            Response response = EsContext.CONTEXT.getRestClient().performRequest(request);
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
            this.logger.error(ex);
        }
        return tList;
    }

    @Override
    public List<T> search(QueryBuilder queryBuilder, List<SortBuilder> sortList, int from, int size)
    {
        List<T> tList = Collections.EMPTY_LIST;
        String path = "/" + index + "/" + type + "/_search";
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
            Response response = EsContext.CONTEXT.getRestClient().performRequest(request);
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
            this.logger.error(ex);
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

}
