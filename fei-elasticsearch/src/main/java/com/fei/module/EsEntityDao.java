package com.fei.module;

import com.fei.elasticsearch.index.query.QueryBuilder;
import com.fei.elasticsearch.search.sort.SortBuilder;
import java.util.List;
import java.util.Map;

/**
 * elasticsearch entity dao
 *
 * @author jianying9
 * @param <T>
 */
public interface EsEntityDao<T>
{

    public String getIndex();

    /**
     * 判断主键是否存在
     *
     * @param keyValue
     * @return
     */
    public boolean exist(Object keyValue);

    /**
     * 总记录数
     *
     * @return
     */
    public int total();

    /**
     * 根据主键查询
     *
     * @param keyValue
     * @return
     */
    public T get(Object keyValue);

    /**
     * 删除
     *
     * @param keyValue
     */
    public void delete(Object keyValue);

    /**
     * 插入
     *
     * @param t
     */
    public void insert(T t);

    /**
     * 更新
     *
     * @param t
     */
    public void update(T t);

    /**
     * 更新
     *
     * @param keyValue
     * @param updateMap
     */
    public void update(String keyValue, Map<String, Object> updateMap);

    /**
     * 更新或插入
     *
     * @param t
     */
    public void upsert(T t);

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
