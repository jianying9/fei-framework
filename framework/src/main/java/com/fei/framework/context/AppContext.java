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

    private final BeanContext beanContext = new BeanContext();

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

    public void addScanPackage(String packageName)
    {
        this.packageNameSet.add(packageName);
    }

    public void addScanPackage(Class<?> clazz)
    {

        this.packageNameSet.add(clazz.getPackageName());
    }

    public BeanContext getBeanContext()
    {
        return beanContext;
    }

}