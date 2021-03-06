package com.fei.elasticsearch.index.query;

/**
 *
 * @author jianying9
 */
public class QueryBuilders
{

    public static BoolQueryBuilder boolQuery()
    {
        return new BoolQueryBuilder();
    }

    public static TermQueryBuilder termQuery(String name, Object value)
    {
        return new TermQueryBuilder(name, value);
    }

    public static RangeQueryBuilder rangeQuery(String name)
    {
        return new RangeQueryBuilder(name);
    }

    public static TermsQueryBuilder termsQuery(String name, Object... values)
    {
        return new TermsQueryBuilder(name, values);
    }

    public static MatchQueryBuilder matchQuery(String name, String text)
    {
        return new MatchQueryBuilder(name, text);
    }

    public static ExistsQueryBuilder existsQuery(String name)
    {
        return new ExistsQueryBuilder(name);
    }

    public static MultiMatchQueryBuilder multiMatchQuery(String text, String... fieldNames)
    {
        return new MultiMatchQueryBuilder(text, fieldNames);
    }

    public static NestedQueryBuilder nestedQuery(String path, QueryBuilder query)
    {
        return new NestedQueryBuilder(path, query);
    }

}
