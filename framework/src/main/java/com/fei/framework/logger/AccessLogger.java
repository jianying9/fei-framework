package com.fei.framework.logger;

/**
 *
 * @author jianying9
 */
public interface AccessLogger {

    public void log(String route, String sid, String request, String response, long time);
    
    public void error(String route, String sid, String request, String response, long time);

    public void log(String sid, String type, String operate);

}
