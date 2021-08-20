package com.fei.devops.entity;

import com.fei.annotations.elasticsearch.EsColumn;
import com.fei.annotations.elasticsearch.EsEntity;
import com.fei.annotations.elasticsearch.EsKey;
import java.util.Date;

/**
 * 应用
 *
 * @author jianying9
 */
@EsEntity
public class AppEntity
{

    @EsKey(desc = "id", auto = true)
    public String appId;

    @EsColumn(description = "名称")
    public String appName;

    @EsColumn(description = "描述")
    public String appDesc;
    
    @EsColumn(analyzer = true, description = "关键字")
    public String keyword;

    @EsColumn(description = "时间戳")
    public Date timestamp;

}
