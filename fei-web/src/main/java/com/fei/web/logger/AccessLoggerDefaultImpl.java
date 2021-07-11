package com.fei.web.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jianying9
 */
public final class AccessLoggerDefaultImpl implements AccessLogger
{

    private final Logger logger = LoggerFactory.getLogger(AccessLoggerDefaultImpl.class);

    @Override
    public void log(String route, String groupId, String request, String response, long time)
    {
        this.logger.info("route:{},time:{},groupId:{},request:{},response:{}", route, time, groupId, request, response);
    }

}
