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

    public String getAppName()
    {
        return appName;
    }

    public void setAppName(String appName)
    {
        this.appName = appName;
    }

    public String getAbsolutePath()
    {
        //初始化当前应用目录
        String appPath = new File("").getAbsolutePath();
        //如果是maven运行环境则根目录定位到target
        String targetPath = appPath + "/target";
        File targetDir = new File(targetPath);
        if (targetDir.exists()) {
            String buildName = appPath.substring(appPath.lastIndexOf("/") + 1);
            appPath = targetPath + "/" + buildName;
        }
        return appPath;
    }

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

    public String getParameter(String name)
    {
        return this.parameterMap.get(name);
    }

    public void addAll(Map<String, String> parameterMap)
    {
        this.parameterMap.putAll(parameterMap);
    }

}
