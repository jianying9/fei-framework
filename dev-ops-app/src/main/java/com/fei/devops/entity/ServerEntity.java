package com.fei.devops.entity;

import com.fei.annotations.elasticsearch.EsColumn;
import com.fei.annotations.elasticsearch.EsEntity;
import com.fei.annotations.elasticsearch.EsKey;
import java.util.Date;

/**
 * 服务器
 *
 * @author jianying9
 */
@EsEntity
public class ServerEntity
{

    @EsKey(desc = "id", auto = true)
    public String id;

    @EsColumn(description = "名称")
    public String name;

    @EsColumn(description = "ip")
    public String ip;

    @EsColumn(description = "描述")
    public String description;

    @EsColumn(description = "时间戳")
    public Date timestamp;

}
