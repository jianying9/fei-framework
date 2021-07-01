package com.fei.web.test;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fei.app.context.AppContextBuilder;
import com.fei.web.response.Response;
import com.fei.web.router.Router;
import com.fei.web.router.RouterContext;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author jianying9
 */
public class RouterMock
{

    private final Logger logger = LogManager.getLogger(RouterMock.class);

    public RouterMock(Class<?> controller)
    {
        Map<String, String> parameterMap = new HashMap();
        parameterMap.put("debug", "true");
        AppContextBuilder appContextBuilder = new AppContextBuilder(parameterMap);
        appContextBuilder.addPackageClass(controller);
        appContextBuilder.build();
    }

    public JSONObject perform(String route, JSONObject input, String auth)
    {
        Router router = RouterContext.INSTANCE.get(route);
        JSONObject output;
        if (router == null) {
            output = Response.createNotfound(route);
        } else {
            output = router.processRequest(input, auth);
        }
        this.logger.info(output.toString(SerializerFeature.PrettyFormat));
        return output;
    }

}
