package com.fei.web.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jianying9
 */
public final class AccessLoggerDefaultImpl implements AccessLogger
{

    private final Logger logger = LoggerFactory.getLogger(AccessLog.class);

    @Override
    public void log(String route, String sid, String request, String response, long time)
    {
        this.logger.info("time:{},sid:{} route:{} time:{} request:{} response:{}", time, sid, route, time, request, response);
    }

    @Override
    public void error(String route, String sid, String request, String response, long time)
    {
        this.logger.error("time:{},sid:{} route:{} time:{} request:{} response:{}", time, sid, route, time, request, response);
    }

    @Override
    public void log(String sid, String type, String operate)
    {
        this.logger.info("sid[" + sid + "]:" + type + " " + operate);
    }

}
