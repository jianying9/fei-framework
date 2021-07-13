package com.fei.web.logger;

import com.fei.web.router.BizContext;

/**
 * 业务日志
 *
 * @author jianying9
 */
public interface BizLogger
{

    /**
     * 接口处理日志
     *
     * @param requestBody 请求文本
     * @param responseBody 响应文本
     * @param bizContext 请求上下文
     */
    public void log(BizContext bizContext, String requestBody, String responseBody);

}
