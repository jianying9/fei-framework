package com.fei.web.test;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fei.annotations.app.BootApp;
import com.fei.app.context.AppContext;
import com.fei.app.context.AppContextBuilder;
import com.fei.app.utils.ToolUtil;
import com.fei.web.response.Response;
import com.fei.web.router.Router;
import com.fei.web.router.RouterContext;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jianying9
 */
public class RouterMock
{

    private final Logger logger = LoggerFactory.getLogger(RouterMock.class);

    public RouterMock(Class<?> mainClass)
    {
        if (AppContext.INSTANCE.isReady() == false) {
            //appName
            if (mainClass.isAnnotationPresent(BootApp.class) == false) {
                throw new RuntimeException("mainClass must annotation BootApp.class");
            }
            BootApp bootApp = mainClass.getAnnotation(BootApp.class);
            String appName = bootApp.value();
            //框架初始化
            AppContext.INSTANCE.addScanPackage(mainClass);
            AppContext.INSTANCE.setAppName(appName);
            Map<String, String> parameterMap = ToolUtil.getAppParams(appName);
            parameterMap.put("debug", "true");
            AppContextBuilder appContextBuilder = new AppContextBuilder(parameterMap);
            appContextBuilder.build();
        }
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
