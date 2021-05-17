package com.fei.framework.test;

import com.fei.framework.bean.BeanContext;
import com.fei.framework.context.AppContext;
import com.fei.framework.context.AppContextBuilder;
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

    public void resource(Object obj)
    {
        BeanContext beanContext = AppContext.CONTEXT.getBeanContext();
        beanContext.resource(obj);
    }
}
