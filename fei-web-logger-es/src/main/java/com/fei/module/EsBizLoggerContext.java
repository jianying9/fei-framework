package com.fei.module;

import com.fei.app.module.ModuleContext;
import java.util.Set;

/**
 *
 * @author jianying9
 */
public class EsBizLoggerContext implements ModuleContext
{

    private final String name = "es-biz-logger";

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void init(Set<Class<?>> classSet)
    {
    }

    @Override
    public void build()
    {
        
    }

}
