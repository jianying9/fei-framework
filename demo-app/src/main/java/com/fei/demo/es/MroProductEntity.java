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

    @EsColumn(description = "单品id")
    public String itemId;

    @EsColumn(description = "组织id")
    public String orgId;

    @EsColumn(description = "组织")
    public String orgName;

    @EsColumn(description = "名称")
    public String name;

    @EsColumn(description = "规格")
    public String attribute;

    @EsColumn(description = "单位")
    public String unit;

    @EsColumn(description = "单价")
    public double taxPrice;

    @EsColumn(description = "标签")
    public String tagName;

    @EsColumn(description = "分类")
    public String categoryCode;

    @EsColumn(description = "商品图片集合")
    public List<String> imageUrlList;

    @EsColumn(description = "搜索关键字", analyzer = true)
    public String keyword;

}
