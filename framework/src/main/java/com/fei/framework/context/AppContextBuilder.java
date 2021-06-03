package com.fei.framework.context;

import com.fei.framework.bean.BeanContext;
import com.fei.framework.util.ClassUtils;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fei.framework.module.Module;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.fei.framework.module.ModuleContext;
import java.lang.annotation.Annotation;
import java.util.HashSet;

/**
 * 全局上下文对象构造函数抽象类
 *
 * @author jianying9
 */
public class AppContextBuilder
{

    private final Logger logger = LogManager.getLogger(AppContext.class);
    private final Set<Class<?>> initPackageClassSet = new HashSet();

    public AppContextBuilder(Map<String, String> parameterMap)
    {
        AppContext.CONTEXT.addAll(parameterMap);
    }

    public void addPackageClass(Class<?> clazz)
    {
        this.initPackageClassSet.add(clazz);
    }

    private Set<Class<?>> loadClass(ClassLoader classloader, Set<String> classNameSet)
    {
        Set<Class<?>> packageClassSet = new HashSet(classNameSet.size());
        Class<?> clazz;
        try {
            for (String className : classNameSet) {
                this.logger.debug("locadClass:{}", className);
                clazz = classloader.loadClass(className);
                packageClassSet.add(clazz);
            }
        } catch (ClassNotFoundException | ClassFormatError | NoClassDefFoundError ex) {
            boolean stop = true;
            String error = ex.getMessage();
            if (error.contains("javax/servlet/")) {
                stop = false;
            }
            if (stop) {
                this.logger.error(ex);
                throw new RuntimeException(ex);
            }
        }

        return packageClassSet;
    }

    private void analyzeDependency(BeanContext beanContext, Class<?> depClass, Set<Class<?>> allClassSet)
    {
        if (allClassSet.contains(depClass) == false) {
            allClassSet.add(depClass);
            Set<Class<?>> depClassSet = beanContext.getDependency(depClass);
            for (Class<?> childDepClass : depClassSet) {
                this.analyzeDependency(beanContext, childDepClass, allClassSet);
            }
        }
    }

    public final void build()
    {
        if (AppContext.CONTEXT.isReady() == false) {
            //将运行参数保存至全局上下文对象
            String debug = AppContext.CONTEXT.getParameter("debug");
            if (debug != null && debug.equals("true")) {
                AppContext.CONTEXT.setDebug(true);
            }
            //增加扫描包
            Set<Class<?>> classSet = new HashSet();
            AppContext.CONTEXT.addScanPackage("com.fei.module");
            final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            Set<String> classNameSet = ClassUtils.findClass(classloader, AppContext.CONTEXT.getPackageNameSet());
            Set<Class<?>> packageClassSet = this.loadClass(classloader, classNameSet);
            packageClassSet.addAll(this.initPackageClassSet);
            //确定包含annotation的类
            BeanContext beanContext = AppContext.CONTEXT.getBeanContext();
            Annotation[] annotationArray;
            for (Class<?> clazz : packageClassSet) {
                if (clazz.isAnnotation() == false) {
                    annotationArray = clazz.getAnnotations();
                    if (annotationArray.length > 0) {
                        classSet.add(clazz);
                        //分析依赖关系并追加
                        Set<Class<?>> depClassSet = beanContext.getDependency(clazz);
                        for (Class<?> depClass : depClassSet) {
                            this.analyzeDependency(beanContext, depClass, classSet);
                        }
                    }
                }
            }
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
            ClassUtils.removeClass(classSet, moduleContextList);
            //初始化module
            for (ModuleContext ctx : moduleContextList) {
                this.logger.info("init Module class:{},name:{}.", ctx.getClass().getName(), ctx.getName());
                ctx.init(classSet);
            }
            //初始化所有bean
            AppContext.CONTEXT.getBeanContext().build();
            //构件所有module
            for (ModuleContext ctx : moduleContextList) {
                this.logger.info("build Module class:{},name:{}.", ctx.getClass().getName(), ctx.getName());
                ctx.build();
            }
            //初始化完成
            AppContext.CONTEXT.setReady(true);
        }
    }

}
