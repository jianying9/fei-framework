package com.fei.framework.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author jianying9
 */
public final class ClassUtil
{

    /**
     * 获取包下所有的class
     *
     * @param classloader
     * @param packageNameList
     * @return
     */
    public static Set<String> findClass(final ClassLoader classloader, final Set<String> packageNameList)
    {
        Logger logger = LogManager.getLogger(ClassUtil.class);
        final Set<String> classNameSet = new HashSet(200);
        Enumeration<URL> eUrl;
        try {
            for (String packageName : packageNameList) {
                //获取有效的url
                eUrl = classloader.getResources(getPackagePath(packageName));
                if (eUrl != null) {
                    while (eUrl.hasMoreElements()) {
                        //获取class路径
                        findClass(classNameSet, classloader, eUrl.nextElement(), packageName);
                    }
                } else {
                    logger.error("can not find package:".concat(packageName));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return classNameSet;
    }

    /**
     * 包名与路径转换
     *
     * @param packageName
     * @return
     */
    private static String getPackagePath(String packageName)
    {
        return packageName == null ? null : packageName.replace('.', '/');
    }

    /**
     * 类路径与类名转换
     *
     * @param packagePath
     * @return
     */
    private static String getPackageName(String packagePath)
    {
        return packagePath == null ? null : packagePath.replace('/', '.');
    }

    /**
     * 根据url获取包含的class文件
     *
     * @param url
     * @return
     */
    private static void findClass(final Set<String> classNameSet, final ClassLoader classloader, final URL url, final String packageName) throws IOException, ClassNotFoundException
    {
        //判断是否是jar包
        String urlName = url.getFile();
        if (!urlName.contains("/src/test/") && !urlName.contains("/src/main/")) {
            int index = urlName.lastIndexOf(".jar");
            if (index > -1) {
                String jarUrlName = urlName.substring(0, index + 4);
                URL jarUrl = new URL(jarUrlName);
                findClassInJar(classNameSet, jarUrl, packageName);
            } else {
                findClassInDirectory(classNameSet, classloader, url, packageName);
            }
        }
    }

    /**
     * 获取文件目录中的class
     *
     * @param classloader
     * @param url
     * @param packageName
     * @throws IOException
     */
    private static void findClassInDirectory(final Set<String> classNameSet, final ClassLoader classloader, final URL url, final String packageName) throws IOException, ClassNotFoundException
    {
        InputStream is = null;
        Enumeration<URL> eUrl;
        String className;
        String childPackageName;
        StringBuilder classBuilder = new StringBuilder();
        StringBuilder childPackageNameBuilder = new StringBuilder();
        try {
            is = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            for (String line; (line = reader.readLine()) != null;) {
                if (line.endsWith(".class")) {
                    classBuilder.setLength(0);
                    className = line.substring(0, line.length() - 6);
                    classBuilder.append(packageName).append('.').append(className);
                    className = classBuilder.toString();
                    classNameSet.add(className);
                } else {
                    childPackageNameBuilder.setLength(0);
                    childPackageNameBuilder.append(packageName).append('.').append(line);
                    childPackageName = childPackageNameBuilder.toString();
                    eUrl = classloader.getResources(getPackagePath(childPackageName));
                    if (eUrl != null) {
                        while (eUrl.hasMoreElements()) {
                            //获取class路径
                            findClass(classNameSet, classloader, eUrl.nextElement(), childPackageName);
                        }
                    }
                }
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 获取jar包的class
     *
     * @param jarUrlName
     * @param resourceList
     */
    private static void findClassInJar(final Set<String> classNameSet, final URL url, final String packageName) throws IOException, ClassNotFoundException
    {
        InputStream is = null;
        try {
            is = url.openStream();
            JarInputStream jarInput = new JarInputStream(is);
            String entryName;
            String className;
            String classPath;
            for (JarEntry entry; (entry = jarInput.getNextJarEntry()) != null;) {
                if (!entry.isDirectory()) {
                    entryName = entry.getName();
                    if (entryName.endsWith(".class")) {
                        classPath = entryName.substring(0, entryName.length() - 6);
                        className = getPackageName(classPath);
                        if (className.startsWith(packageName)) {
                            classNameSet.add(className);
                        }
                    }
                }
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 移除对象实例对应的class
     *
     * @param classSet
     * @param objectList
     */
    public static void removeClass(Set<Class<?>> classSet, List<?> objectList)
    {
        for (Object obj : objectList) {
            classSet.remove(obj.getClass());
        }
    }

    /**
     * 根据方法名获取方法
     *
     * @param clazz
     * @param name
     * @return
     */
    public static Method getMethodByName(Class<?> clazz, String name)
    {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(name);
        } catch (NoSuchMethodException | SecurityException ex) {
        }
        return method;
    }
}
