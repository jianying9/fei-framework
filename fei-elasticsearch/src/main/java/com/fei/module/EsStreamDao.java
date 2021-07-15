package com.fei.module;

import com.fei.elasticsearch.index.query.QueryBuilder;
import com.fei.elasticsearch.search.sort.SortBuilder;
import java.util.List;

/**
 * elasticsearch entity dao
 *
 * @author jianying9
 * @param <T>
 */
public interface EsStreamDao<T>
{

    public String getIndex();

    /**
     * 总记录数
     *
     * @return
     */
    public int total();

    /**
     * 插入
     *
     * @param t
     */
    public void insert(T t);

    /**
     *
     * @param queryBuilder
     * @param from
     * @param size
     * @return
     */
    public List<T> search(QueryBuilder queryBuilder, int from, int size);

    /**
     *
     * @param queryBuilder
     * @param sort
     * @param from
     * @param size
     * @return
     */
    public List<T> search(QueryBuilder queryBuilder, SortBuilder sort, int from, int size);

    /**
     *
     * @param queryBuilder
     * @param sortList
     * @param from
     * @param size
     * @return
     */
    public List<T> search(QueryBuilder queryBuilder, List<SortBuilder> sortList, int from, int size);

    /**
     *
     * @param sort
     * @param from
     * @param size
     * @return
     */
    public List<T> search(SortBuilder sort, int from, int size);

    /**
     *
     * @param from
     * @param size
     * @return
     */
    public List<T> search(int from, int size);

    /**
     *
     * @param queryBuilder
     * @return
     */
    public List<T> search(QueryBuilder queryBuilder);

}
