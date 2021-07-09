package com.fei.app.context;

import com.fei.app.bean.BeanContext;
import com.fei.app.utils.ClassUtil;
import java.util.Map;
import com.fei.annotations.module.Module;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.fei.app.module.ModuleContext;
import com.fei.app.utils.ToolUtil;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 全局上下文对象构造函数抽象类
 *
 * @author jianying9
 */
public class AppContextBuilder
{

    private final Logger logger = LoggerFactory.getLogger(AppContext.class);
    private final Set<Class<?>> initPackageClassSet = new HashSet();

    public AppContextBuilder(Map<String, String> parameterMap)
    {
        AppContext.INSTANCE.addAll(parameterMap);
    }

    public void addPackageClass(Class<?> clazz)
    {
        this.initPackageClassSet.add(clazz);
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
        if (AppContext.INSTANCE.isReady() == false) {
            //将运行参数保存至全局上下文对象
            String debug = AppContext.INSTANCE.getParameter("debug");
            if (debug != null && debug.equals("true")) {
                AppContext.INSTANCE.setDebug(true);
            }
            //增加扫描包
            Set<Class<?>> classSet = new HashSet();
            this.logger.info("scan package:{}", AppContext.INSTANCE.getPackageNameSet());
            AppContext.INSTANCE.addScanPackage("com.fei.module");
            Set<Class<?>> packageClassSet = ClassUtil.loadClass(AppContext.INSTANCE.getPackageNameSet());
            packageClassSet.addAll(this.initPackageClassSet);
            //确定包含annotation的类
            BeanContext beanContext = AppContext.INSTANCE.getBeanContext();
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
            for (Class<?> clazz : classSet) {
                if (clazz.isAnnotationPresent(Module.class)) {
                    this.logger.info("find Module class:{}.", clazz.getName());
                    moduleContext = ToolUtil.create(clazz);
                    //
                    moduleContextList.add(moduleContext);
                }
            }
            ClassUtil.removeClass(classSet, moduleContextList);
            //初始化module
            for (ModuleContext ctx : moduleContextList) {
                this.logger.info("init Module class:{},name:{}.", ctx.getClass().getName(), ctx.getName());
                ctx.init(classSet);
            }
            //初始化所有bean
            AppContext.INSTANCE.getBeanContext().build();
            //构件所有module
            for (ModuleContext ctx : moduleContextList) {
                this.logger.info("build Module class:{},name:{}.", ctx.getClass().getName(), ctx.getName());
                ctx.build();
            }
            //初始化完成
            AppContext.INSTANCE.ready();
        }
    }

}
