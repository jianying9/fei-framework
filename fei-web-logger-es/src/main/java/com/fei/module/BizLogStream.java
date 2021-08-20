package com.fei.module;

import com.fei.annotations.elasticsearch.EsColumn;
import com.fei.annotations.elasticsearch.EsStream;

/**
 *
 * @author jianying9
 */
@EsStream
public class BizLogStream
{
    @EsColumn(description = "应用名称")
    public String appName;

    @EsColumn(description = "地址")
    public String host;

    @EsColumn(description = "端口")
    public int port;

    @EsColumn(description = "用户id")
    public String userId;

    @EsColumn(description = "用户")
    public String userName;

    @EsColumn(description = "业务追踪id(多个微服务调用时追踪用)")
    public String groupId;

    @EsColumn(description = "路由")
    public String route;

    @EsColumn(description = "执行时间")
    public long processTime;

    @EsColumn(description = "controller执行时间")
    public long subProcessTime;

    @EsColumn(description = "请求内容", analyzer = true)
    public String requestBody;

    @EsColumn(description = "响应内容", analyzer = true)
    public String responseBody;

}
