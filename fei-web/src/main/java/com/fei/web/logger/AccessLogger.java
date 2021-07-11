package com.fei.web.logger;

/**
 *
 * @author jianying9
 */
public interface AccessLogger
{

    public void log(String route, String groupId, String request, String response, long time);

}
