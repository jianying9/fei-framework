package com.fei.demo.es;

import com.fei.annotations.elasticsearch.EsColumn;
import com.fei.annotations.elasticsearch.EsEntity;
import com.fei.annotations.elasticsearch.EsKey;
import java.util.List;

/**
 *
 * @author jianying9
 */
@EsEntity(database = "zlw")
public class MroProductEntity
{

    @EsKey(desc = "商品id")
    public String productId;

    @EsColumn(desc = "单品id")
    public String itemId;

    @EsColumn(desc = "组织id")
    public String orgId;

    @EsColumn(desc = "组织")
    public String orgName;

    @EsColumn(desc = "名称")
    public String name;

    @EsColumn(desc = "规格")
    public String attribute;

    @EsColumn(desc = "单位")
    public String unit;

    @EsColumn(desc = "单价")
    public double taxPrice;

    @EsColumn(desc = "标签")
    public String tagName;

    @EsColumn(desc = "分类")
    public String categoryCode;

    @EsColumn(desc = "商品图片集合")
    public List<String> imageUrlList;

    @EsColumn(desc = "搜索关键字", analyzer = true)
    public String keyword;

}
