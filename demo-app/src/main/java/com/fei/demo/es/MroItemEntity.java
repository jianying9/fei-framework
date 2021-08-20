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
public class MroItemEntity
{

    @EsKey(desc = "单品id")
    public String itemId;

    @EsColumn(description = "组织id")
    public String orgId;

    @EsColumn(description = "组织")
    public String orgName;

    @EsColumn(description = "名称")
    public String name;

    @EsColumn(description = "品牌")
    public String brand;

    @EsColumn(description = "分类")
    public String categoryCode;

    @EsColumn(description = "商品图片集合")
    public List<String> imageUrlList;

    @EsColumn(description = "描述图片集合")
    public List<String> descriptionUrlList;

}
