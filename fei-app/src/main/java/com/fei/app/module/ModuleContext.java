package com.fei.app.module;

import java.util.Set;

/**
 *
 * @author jianying9
 */
public interface ModuleContext
{

    public String getName();

    public void init(Set<Class<?>> classSet);
    
    public void build();

}
