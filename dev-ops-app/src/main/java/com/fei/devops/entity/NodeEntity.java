package com.fei.devops.entity;

import com.fei.annotations.elasticsearch.EsColumn;
import com.fei.annotations.elasticsearch.EsEntity;
import com.fei.annotations.elasticsearch.EsKey;
import java.util.Date;

/**
 * 服务器节点
 *
 * @author jianying9
 */
@EsEntity
public class NodeEntity
{

    @EsKey(desc = "id", auto = true)
    public String id;

    @EsColumn(description = "名称")
    public String name;

    @EsColumn(description = "分支环境")
    public String branch;

    @EsColumn(description = "地址")
    public String hostname;

    @EsColumn(description = "描述")
    public String description;
    
    @EsColumn(analyzer = true, description = "关键字")
    public String keyword;

    @EsColumn(description = "时间戳")
    public Date timestamp;

}
