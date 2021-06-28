package com.fei.demo.entity;

import com.fei.module.EsColumn;
import com.fei.module.EsEntity;
import com.fei.module.EsKey;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *
 * @author jianying9
 */
@EsEntity
public class CardEntity
{

    @EsKey(desc = "id", auto = true)
    public String cardId;

    @EsColumn(desc = "卡片集合")
    public List<Long> longList;

    @EsColumn(desc = "卡片集合")
    public Set<Boolean> boolSet;

    @EsColumn(desc = "卡片集合")
    public String[] stringArray;

    @EsColumn(desc = "创建时间")
    public Collection<Date> dateCollection;

}
