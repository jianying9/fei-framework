package com.fei.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.fei.app.utils.ToolUtil;
import com.fei.elasticsearch.index.query.QueryBuilder;
import com.fei.elasticsearch.search.sort.SortBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jianying9
 * @param <T>
 */
public abstract class AbstractEsDao<T>
{

    protected final Logger logger = LoggerFactory.getLogger(EsContext.class);
    protected final EsKeyHandler keyHandler;
    protected final List<EsColumnHandler> columnHandlerList;
    protected final Class<T> clazz;
    protected final String index;

    public AbstractEsDao(
            String index,
            EsKeyHandler keyHandler,
            List<EsColumnHandler> columnHandlerList,
            Class<T> clazz)
    {
        this.keyHandler = keyHandler;
        this.columnHandlerList = columnHandlerList;
        this.index = index;
        this.clazz = clazz;
    }

    public AbstractEsDao(
            String index,
            List<EsColumnHandler> columnHandlerList,
            Class<T> clazz)
    {
        this.keyHandler = null;
        this.columnHandlerList = columnHandlerList;
        this.index = index;
        this.clazz = clazz;
    }

    /**
     * 创建更新索引定义或数据流
     */
    public abstract void setUp();

    public final String getIndex()
    {
        return this.index;
    }

    private String getSearchDocPath()
    {
        String path = "/" + index + "/_search";
        return path;
    }

    /**
     * 实体对象转es表对象
     *
     * @param t
     * @return
     */
    protected final JSONObject t2e(T t)
    {
        JSONObject tJson = JSON.parseObject(JSON.toJSONStringWithDateFormat(t, ToolUtil.DATE_FORMAT));
        JSONObject eJson = new JSONObject();
        Object value;
        for (EsColumnHandler esColumnHandler : this.columnHandlerList) {
            value = tJson.get(esColumnHandler.getFieldName());
            eJson.put(esColumnHandler.getColumnName(), value);
        }
        if (this.keyHandler != null) {
            value = tJson.get(this.keyHandler.getName());
            eJson.put(this.keyHandler.getName(), value);
        }
        return eJson;
    }

    /**
     * es表对象转实体
     *
     * @param eJson
     * @return
     */
    protected final T e2t(JSONObject eJson)
    {
        JSONObject tJson = new JSONObject();
        Object value;
        for (EsColumnHandler esColumnHandler : this.columnHandlerList) {
            value = eJson.get(esColumnHandler.getColumnName());
            if (value == null) {
                value = esColumnHandler.getDefaultValue();
            }
            tJson.put(esColumnHandler.getFieldName(), value);
        }
        if (this.keyHandler != null) {
            value = eJson.get(this.keyHandler.getName());
            tJson.put(this.keyHandler.getName(), value);
        }
        T t = TypeUtils.castToJavaBean(tJson, this.clazz);
        return t;
    }

    public final int total()
    {
        int result;
        String path = this.getSearchDocPath();
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
        } catch (ResponseException ex) {
            result = 0;
        } catch (IOException ex) {
            this.logger.error("es client: exec total error", ex);
            throw new RuntimeException("unknown es error");
        }
        return result;
    }

    /**
     *
     * @param queryBuilder
     * @param sort
     * @param from
     * @param size
     * @return
     */
    public final List<T> search(QueryBuilder queryBuilder, SortBuilder sort, int from, int size)
    {
        List<T> tList;
        String path = this.getSearchDocPath();
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
            JSONObject eJson;
            T t;
            tList = new ArrayList(hitArray.size());
            for (int i = 0; i < hitArray.size(); i++) {
                sourceJson = hitArray.getJSONObject(i);
                eJson = sourceJson.getJSONObject("_source");
                t = this.e2t(eJson);
                tList.add(t);
            }
        } catch (ResponseException ex) {
            tList = Collections.EMPTY_LIST;
        } catch (IOException ex) {
            this.logger.error("es client: exec search error", ex);
            throw new RuntimeException("unknown es error");
        }
        return tList;
    }

    public final List<T> search(QueryBuilder queryBuilder, List<SortBuilder> sortList, int from, int size)
    {
        List<T> tList;
        String path = this.getSearchDocPath();
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
            JSONObject eJson;
            T t;
            tList = new ArrayList(hitArray.size());
            for (int i = 0; i < hitArray.size(); i++) {
                sourceJson = hitArray.getJSONObject(i);
                eJson = sourceJson.getJSONObject("_source");
                t = this.e2t(eJson);
                tList.add(t);
            }
        } catch (ResponseException ex) {
            tList = Collections.EMPTY_LIST;
        } catch (IOException ex) {
            this.logger.error("es client: exec search error", ex);
            throw new RuntimeException("unknown es error");
        }
        return tList;
    }

    public final List<T> search(QueryBuilder queryBuilder, int from, int size)
    {
        SortBuilder sort = null;
        return this.search(queryBuilder, sort, from, size);
    }

    public final List<T> search(SortBuilder sort, int from, int size)
    {
        return this.search(null, sort, from, size);
    }

    public final List<T> search(int from, int size)
    {
        SortBuilder sort = null;
        return this.search(null, sort, from, size);
    }

    public final List<T> search(QueryBuilder queryBuilder)
    {
        int size = 100;
        int from = 0;
        SortBuilder sort = null;
        return this.search(queryBuilder, sort, from, size);
    }

}
