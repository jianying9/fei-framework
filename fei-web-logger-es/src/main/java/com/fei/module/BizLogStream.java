package com.fei.module;

import com.fei.annotations.elasticsearch.EsColumn;
import com.fei.annotations.elasticsearch.EsKey;
import com.fei.annotations.elasticsearch.EsStream;
import java.util.Date;

/**
 *
 * @author jianying9
 */
@EsStream
public class BizLogStream
{

    @EsKey(desc = "id", auto = true)
    public String logId;

    @EsColumn(desc = "路由")
    public String route;

    @EsColumn(desc = "执行时间")
    public long processTime;

    @EsColumn(desc = "controller执行时间")
    public long subProcessTime;

    @EsColumn(desc = "业务追踪id(多个微服务调用时追踪用)")
    public String groupId;

    @EsColumn(desc = "请求内容", analyzer = true)
    public String requestBody;

    @EsColumn(desc = "响应内容", analyzer = true)
    public String responseBody;

    @EsColumn(desc = "记录时间")
    public Date createTime;

}
