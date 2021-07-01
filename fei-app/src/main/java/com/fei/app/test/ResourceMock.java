package com.fei.app.test;

import com.fei.app.bean.BeanContext;
import com.fei.app.context.AppContext;
import com.fei.app.context.AppContextBuilder;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jianying9
 */
public class ResourceMock
{

    public ResourceMock(Class<?> component)
    {
        Map<String, String> parameterMap = new HashMap();
        parameterMap.put("debug", "true");
        AppContextBuilder appContextBuilder = new AppContextBuilder(parameterMap);
        appContextBuilder.addPackageClass(component);
        appContextBuilder.build();
    }

    public ResourceMock(Class<?> component, Map<String, String> parameterMap)
    {
        parameterMap.put("debug", "true");
        AppContextBuilder appContextBuilder = new AppContextBuilder(parameterMap);
        appContextBuilder.addPackageClass(component);
        appContextBuilder.build();
    }

    public void resource(Object obj)
    {
        BeanContext beanContext = AppContext.INSTANCE.getBeanContext();
        beanContext.resource(obj);
    }
}
