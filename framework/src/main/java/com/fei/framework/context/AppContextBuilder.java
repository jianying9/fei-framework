package com.fei.framework.context;

import com.fei.framework.util.ClassUtil;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fei.framework.module.Module;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.fei.framework.module.ModuleContext;
import java.util.HashSet;

/**
 * 全局上下文对象构造函数抽象类
 *
 * @author jianying9
 */
public class AppContextBuilder
{

    private final Logger logger = LogManager.getLogger(AppContext.class);
    private final Map<String, String> parameterMap;

    public AppContextBuilder(Map<String, String> parameterMap)
    {
        this.parameterMap = parameterMap;
    }

    public final String getParameter(String name)
    {
        return this.parameterMap.get(name);
    }

    private Set<Class<?>> loadClass(ClassLoader classloader, Set<String> classNameSet)
    {
        Set<Class<?>> classSet = new HashSet(classNameSet.size());
        Class<?> clazz;
        try {
            for (String className : classNameSet) {
                this.logger.debug("locadClass:{}", className);
                clazz = classloader.loadClass(className);
                classSet.add(clazz);
            }
        } catch (ClassNotFoundException | ClassFormatError | NoClassDefFoundError ex) {
            boolean stop = true;
            String error = ex.getMessage();
            if (error.contains("javax/servlet/")) {
                stop = false;
            } else if (error.contains("com/sun/grizzly/websockets/")) {
                stop = false;
            }
            if (stop) {
                this.logger.error(ex);
                throw new RuntimeException(ex);
            }
        }

        return classSet;
    }

    public final void build()
    {
        //将运行参数保存至全局上下文对象
        String debug = this.getParameter("debug");
        if (debug != null && debug.equals("true")) {
            AppContext.CONTEXT.setDebug(true);
        }
        //增加基础包
        AppContext.CONTEXT.addPackage("com.fei.module");
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        Set<String> classNameSet = ClassUtil.findClass(classloader, AppContext.CONTEXT.getPackageNameSet());
        Set<Class<?>> classSet = this.loadClass(classloader, classNameSet);
        //查找module
        ModuleContext moduleContext;
        List<ModuleContext> moduleContextList = new ArrayList();
        try {
            for (Class<?> clazz : classSet) {
                if (clazz.isAnnotationPresent(Module.class)) {
                    this.logger.info("find Module class:{}.", clazz.getName());
                    moduleContext = (ModuleContext) clazz.getDeclaredConstructor().newInstance();
                    //
                    moduleContextList.add(moduleContext);
                }
            }
        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
        ClassUtil.removeClass(classSet, moduleContextList);
        //初始化module
        for (ModuleContext ctx : moduleContextList) {
            this.logger.info("init Module class:{},name:{}.", ctx.getClass().getName(), ctx.getName());
            ctx.init(classSet);
        }
        //初始化所有bean
        AppContext.CONTEXT.getBeanContext().build();
    }

}
