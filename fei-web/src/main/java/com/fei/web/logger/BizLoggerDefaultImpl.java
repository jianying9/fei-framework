package com.fei.web.logger;

import com.fei.web.router.BizContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jianying9
 */
public final class BizLoggerDefaultImpl implements BizLogger
{

    private final Logger logger = LoggerFactory.getLogger(BizLoggerDefaultImpl.class);

    @Override
    public void log(BizContext bizContext, String requestBody, String responseBody)
    {
        this.logger.info("route:{},processTime:{},subProcessTime:{},groupId:{},requestBody:{},responseBody:{}", bizContext.getRoute(), bizContext.getProcessTime(), bizContext.getSubProcessTime(), bizContext.getGroupId(), requestBody, responseBody);
    }

}
