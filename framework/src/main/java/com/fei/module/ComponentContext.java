package com.fei.module;

import com.fei.framework.context.AppContext;
import com.fei.framework.module.Module;
import com.fei.framework.module.ModuleContext;
import com.fei.framework.bean.BeanContext;
import com.fei.framework.util.ClassUtils;
import com.fei.framework.util.ToolUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @Bean 实现
 * @author jianying9
 */
@Module
public class ComponentContext implements ModuleContext
{

    private final String name = "component";

    private final Logger logger = LogManager.getLogger(ComponentContext.class);

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void init(Set<Class<?>> classSet)
    {
        List<Object> beanList = new ArrayList();
        Object bean;
        BeanContext beanContext = AppContext.INSTANCE.getBeanContext();
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(Component.class)) {
                this.logger.info("find Component class:{}.", clazz.getName());
                bean = ToolUtils.create(clazz);
                //
                beanList.add(bean);
                //
                beanContext.add(this.name, bean);
            }
        }
        ClassUtils.removeClass(classSet, beanList);
    }

    @Override
    public void build()
    {
    }

}
