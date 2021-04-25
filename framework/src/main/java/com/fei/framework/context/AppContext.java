package com.fei.framework.context;

import com.fei.framework.bean.BeanContext;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author jianying9
 */
public final class AppContext
{

    public final static AppContext CONTEXT = new AppContext();

    private final Set<String> packageNameSet = new HashSet();

    private final BeanContext resourceContext = new BeanContext();

    private boolean ready = false;

    private boolean debug = false;

    public boolean isReady()
    {
        return ready;
    }

    void setReady(boolean ready)
    {
        this.ready = ready;
    }

    public boolean isDebug()
    {
        return debug;
    }

    void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    public Set<String> getPackageNameSet()
    {
        return packageNameSet;
    }

    public void addPackage(String packageName)
    {
        this.packageNameSet.add(packageName);
    }

    public BeanContext getBeanContext()
    {
        return resourceContext;
    }

}
