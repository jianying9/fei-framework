package com.fei.app.test;

import com.fei.annotations.app.BootApp;
import com.fei.app.bean.BeanContext;
import com.fei.app.context.AppContext;
import com.fei.app.context.AppContextBuilder;
import com.fei.app.utils.ToolUtil;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class ResourceMock
{

    public ResourceMock(Class<?> mainClass)
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
            //
            parameterMap.put("debug", "true");
            AppContextBuilder appContextBuilder = new AppContextBuilder(parameterMap);
            appContextBuilder.build();
        }
    }

    public void resource(Object obj)
    {
        BeanContext beanContext = AppContext.INSTANCE.getBeanContext();
        beanContext.resource(obj);
    }
}
