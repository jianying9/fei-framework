package com.fei.app.context;

import com.fei.app.bean.BeanContext;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jianying9
 */
public final class AppContext
{

    public final static AppContext INSTANCE = new AppContext();

    private final Set<String> packageNameSet = new HashSet();

    private final BeanContext beanContext = new BeanContext();

    private final Map<String, String> parameterMap = new HashMap();

    private boolean ready = false;

    private boolean debug = false;

    private String appName = "app";
    private String host = "";
    private int port = 8080;

    public String getAppName()
    {
        return appName;
    }

    public void setAppName(String appName)
    {
        this.appName = appName;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public boolean isReady()
    {
        return ready;
    }

    void ready()
    {
        this.ready = true;
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

    public String getParameter(String name)
    {
        return this.parameterMap.get(name);
    }

    public void addAll(Map<String, String> parameterMap)
    {
        this.parameterMap.putAll(parameterMap);
    }

}
