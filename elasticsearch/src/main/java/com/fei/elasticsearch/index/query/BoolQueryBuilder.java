package com.fei.elasticsearch.index.query;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jianying9
 */
public class BoolQueryBuilder implements QueryBuilder
{

    private final List<QueryBuilder> mustClauses = new ArrayList();

    private final List<QueryBuilder> mustNotClauses = new ArrayList();

    private final List<QueryBuilder> shouldClauses = new ArrayList();

    private final List<QueryBuilder> filterClauses = new ArrayList();

    public BoolQueryBuilder must(QueryBuilder queryBuilder)
    {
        mustClauses.add(queryBuilder);
        return this;
    }

    @Override
    public boolean canFilter()
    {
        return false;
    }

    public BoolQueryBuilder mustNot(QueryBuilder queryBuilder)
    {
        mustNotClauses.add(queryBuilder);
        return this;
    }

    public BoolQueryBuilder should(QueryBuilder queryBuilder)
    {
        shouldClauses.add(queryBuilder);
        return this;
    }

    public BoolQueryBuilder filter(QueryBuilder queryBuilder)
    {
        if (queryBuilder.canFilter()) {
            filterClauses.add(queryBuilder);
        } else {
            mustClauses.add(queryBuilder);
        }
        return this;
    }

    @Override
    public JSONObject toJSONObject()
    {
        JSONObject boolJson = new JSONObject();
        if (this.mustClauses.isEmpty() == false) {
            JSONArray mustArray = new JSONArray();
            for (QueryBuilder mustClause : this.mustClauses) {
                mustArray.add(mustClause.toJSONObject());
            }
            boolJson.put("must", mustArray);
        }
        if (this.mustNotClauses.isEmpty() == false) {
            JSONArray mustNotArray = new JSONArray();
            for (QueryBuilder mustNotClause : this.mustNotClauses) {
                mustNotArray.add(mustNotClause.toJSONObject());
            }
            boolJson.put("must_not", mustNotArray);
        }
        if (this.shouldClauses.isEmpty() == false) {
            JSONArray shouldArray = new JSONArray();
            for (QueryBuilder shouldClause : this.shouldClauses) {
                shouldArray.add(shouldClause.toJSONObject());
            }
            boolJson.put("should", shouldArray);
        }
        if (this.filterClauses.isEmpty() == false) {
            JSONArray filterArray = new JSONArray();
            for (QueryBuilder filterClause : this.filterClauses) {
                filterArray.add(filterClause.toJSONObject());
            }
            boolJson.put("filter", filterArray);
        }
        //
        JSONObject queryJson = new JSONObject();
        queryJson.put("bool", boolJson);
        return queryJson;
    }
}
