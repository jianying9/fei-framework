package com.fei.demo.es;

import com.fei.annotations.elasticsearch.EsColumn;
import com.fei.annotations.elasticsearch.EsEntity;
import com.fei.annotations.elasticsearch.EsKey;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *
 * @author jianying9
 */
@EsEntity()
public class CardEntity
{

    @EsKey(desc = "id", auto = true)
    public String cardId;

    @EsColumn(description = "卡片集合")
    public List<Long> longList;

    @EsColumn(description = "卡片集合")
    public Set<Boolean> boolSet;

    @EsColumn(description = "卡片集合")
    public String[] stringArray;

    @EsColumn(description = "创建时间")
    public Collection<Date> dateCollection;

}
