package com.fei.module;

import com.fei.annotations.component.Component;
import com.fei.app.context.AppContext;
import com.fei.annotations.module.Module;
import com.fei.app.module.ModuleContext;
import com.fei.app.bean.BeanContext;
import com.fei.app.utils.ClassUtil;
import com.fei.app.utils.ToolUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Bean 实现
 * @author jianying9
 */
@Module
public class ComponentContext implements ModuleContext
{

    private final String name = "component";

    private final Logger logger = LoggerFactory.getLogger(ComponentContext.class);

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
                bean = ToolUtil.create(clazz);
                //
                beanList.add(bean);
                //
                beanContext.add(this.name, bean);
            }
        }
        ClassUtil.removeClass(classSet, beanList);
    }

    @Override
    public void build()
    {
    }

}
