package com.fei.jetty.embed;

import com.fei.annotations.app.BootApp;
import com.fei.app.context.AppContext;
import com.fei.app.context.AppContextBuilder;
import com.fei.app.utils.ToolUtil;
import com.fei.web.servlet.AppServlet;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Servlet;
import javax.servlet.annotation.WebServlet;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * jetty server实例化
 *
 * @author jianying9
 */
public class AppServer
{

    private String defaultWebappPath = "";

    private String webappsPath = "";

    private String logsWebappPath = "";

    private int port = 8080;

    private final Logger logger;

    private final List<EventListener> eventListenerList = new ArrayList();

    private final List<Class<? extends Servlet>> servletList = new ArrayList();

    /**
     * 服务实例化
     *
     * @param mainClass
     * @param args
     */
    public AppServer(Class<?> mainClass, String[] args)
    {
        //端口自定义
        port = AppServer.getPort();
        //appName
        if (mainClass.isAnnotationPresent(BootApp.class) == false) {
            throw new RuntimeException("mainClass must annotation BootApp.class");
        }
        BootApp bootApp = mainClass.getAnnotation(BootApp.class);
        String appName = bootApp.value();
        //appPath
        String appPath = new File("").getAbsolutePath();
        //如果是maven运行环境则根目录定位到target
        String targetPath = appPath + "/target";
        File targetDir = new File(targetPath);
        if (targetDir.exists()) {
            String buildName = appPath.substring(appPath.lastIndexOf("/") + 1);
            appPath = targetPath + "/" + buildName;
        }
        //设置环境变量,用于日志对象初始化配置,logback.xml使用
        System.setProperty("app.path", appPath);
        System.setProperty("app.name", appName);
        //初始化目录
        //webapps   web服务目录
        webappsPath = appPath + "/webapps";
        this.checkDir(webappsPath);
        //webapps/logs  日志web应用目录
        logsWebappPath = webappsPath + "/logs";
        this.checkDir(logsWebappPath);
        //webapps/appName
        defaultWebappPath = webappsPath + "/" + appName;
        this.checkDir(defaultWebappPath);
        //日志初始化
        this.logger = LoggerFactory.getLogger(AppServer.class);
        logger.info("app目录:{}", appPath);
        logger.info("web应用目录:{}", webappsPath);
        logger.info("日志web应用目录:{}", logsWebappPath);
        logger.info("默认web应用目录:{}", defaultWebappPath);
        //框架初始化
        AppContext.INSTANCE.setAppName(appName);
        AppContext.INSTANCE.addScanPackage(mainClass);
        Map<String, String> parameterMap = ToolUtil.getAppParams(appName);
        AppContextBuilder appContextBuilder = new AppContextBuilder(parameterMap);
        appContextBuilder.build();
        //
        this.servletList.add(AppServlet.class);
    }

    private void checkDir(String path)
    {
        File dir = new File(path);
        if (dir.exists() == false) {
            dir.mkdir();
        }
    }

    /**
     * 添加监听
     *
     * @param listener
     * @return
     */
    public AppServer addEventListener(EventListener listener)
    {
        this.eventListenerList.add(listener);
        return this;
    }

    public AppServer setPort(int port)
    {
        this.port = port;
        return this;
    }

    /**
     * 添加servlet
     *
     * @param servlet 类
     * @return
     */
    public AppServer addServlet(Class<? extends Servlet> servlet)
    {
        this.servletList.add(servlet);
        return this;
    }

    private void checkAndKillPort()
    {
        //获取系统版本
        String osName = System.getProperty("os.name").toLowerCase();
        logger.warn("当前系统:{}", osName);
        if (osName.startsWith("linux")) {
            //linux
            checkAndKillPortInLinux();
        } else if (osName.startsWith("mac")) {
            //mac
            checkAndKillPortInMac();
        }
    }

    private String getMacPid(String text)
    {
        String pid = null;
        Pattern pidPattern = Pattern.compile("(?:java    )([\\d]+)");
        Matcher matcher = pidPattern.matcher(text);
        if (matcher.find()) {
            pid = matcher.group(1);
        }
        return pid;
    }

    private void checkAndKillPortInMac()
    {
        try {
            //检查端口
            String cmd = "lsof -i:" + Integer.toString(port);
            logger.info("检测端口:{}", cmd);
            Process lsofProcess = Runtime.getRuntime().exec(cmd);
            lsofProcess.waitFor();
            InputStream in = lsofProcess.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in));
            String temp = read.readLine();
            String last = "";
            while (temp != null) {
                last = temp;
                temp = read.readLine();
            }
            String pid = this.getMacPid(last);
            if (pid != null) {
                logger.warn("端口已被占用:{}", last);
                //杀死该进程
                cmd = "kill -9 " + pid;
                Process killProcess = Runtime.getRuntime().exec(cmd);
                killProcess.waitFor();
                logger.warn("杀死进程:{}", cmd);
            }
        } catch (IOException | InterruptedException ex) {
            this.logger.error("mac端口检测异常", ex);
        }
    }

    private String getLinuxPid(String text)
    {
        String pid = null;
        Pattern pidPattern = Pattern.compile("([\\d]+)(?:/java)");
        Matcher matcher = pidPattern.matcher(text);
        if (matcher.find()) {
            pid = matcher.group(1);
        }
        return pid;
    }

    private void checkAndKillPortInLinux()
    {
        try {
            //检查端口
            String cmd = "netstat -ntlp | grep " + Integer.toString(port);
            logger.info("检测端口:{}", cmd);
            String[] cmdArray = {"/bin/sh", "-c", cmd};
            Process netstatProcess = Runtime.getRuntime().exec(cmdArray);
            netstatProcess.waitFor();
            InputStream in = netstatProcess.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in));
            String temp = read.readLine();
            String last = "";
            while (temp != null) {
                last = temp;
                temp = read.readLine();
            }
            String pid = this.getLinuxPid(last);
            if (pid != null) {
                logger.warn("端口已被占用:{}", last);
                //杀死该进程
                cmd = "kill -9 " + pid;
                Process killProcess = Runtime.getRuntime().exec(cmd);
                killProcess.waitFor();
                logger.warn("杀死进程:{}", cmd);
            }
        } catch (IOException | InterruptedException ex) {
            this.logger.error("linux端口检测异常", ex);
        }
    }

    public void start()
    {
        //创建server
        final Server server = new Server();
        //监听端口
        HttpConfiguration config = new HttpConfiguration();
        //http不返回版本信息
        config.setSendServerVersion(false);
        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(config);
        ServerConnector connector = new ServerConnector(server, null, null, null, -1, -1, httpConnectionFactory);
        connector.setPort(this.port);
        server.setConnectors(new Connector[]{connector});
        //初始化默认webapp服务
        WebAppContext defaultAppContext = new WebAppContext(this.defaultWebappPath, "/" + AppContext.INSTANCE.getAppName());
        //禁止http目录遍历
        defaultAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        //添加监听
        for (EventListener eventListener : eventListenerList) {
            defaultAppContext.addEventListener(eventListener);
        }
        for (Class<? extends Servlet> servlet : this.servletList) {
            String pathSpec = "/" + servlet.getName();
            if (servlet.isAnnotationPresent(WebServlet.class)) {
                WebServlet webServlet = servlet.getAnnotation(WebServlet.class);
                if (webServlet.urlPatterns().length > 0) {
                    pathSpec = webServlet.urlPatterns()[0];
                }
            }
            defaultAppContext.addServlet(servlet, pathSpec);
        }
        server.setHandler(defaultAppContext);
        //初始化日志文件web服务
        ResourceHandler logResourceHandler = new ResourceHandler();
        logResourceHandler.setEtags(true);
        //允许遍历子目录
        logResourceHandler.setDirectoriesListed(true);
        //设置文件头
        MimeTypes mimeTypes = new MimeTypes();
        logResourceHandler.setMimeTypes(mimeTypes);
        mimeTypes.addMimeMapping("log", "text/plain;charset=utf-8");
        //设置路径
        ContextHandler logContext = new ContextHandler();
        logContext.setContextPath("/logs");
        logContext.setBaseResource(Resource.newResource(new File(this.logsWebappPath)));
        logContext.setHandler(logResourceHandler);
        //设置安全验证
        ClassLoader classLoader = AppServer.class.getClassLoader();
        URL realmProps = classLoader.getResource("realm.properties");
        LoginService loginService = new HashLoginService("logRealm",
                realmProps.toExternalForm());
        Constraint constraint = new Constraint();
        constraint.setName("auth");
        constraint.setAuthenticate(true);
        constraint.setRoles(new String[]{"admin"});
        ConstraintMapping mapping = new ConstraintMapping();
        mapping.setPathSpec("/*");
        mapping.setConstraint(constraint);
        ConstraintSecurityHandler security = new ConstraintSecurityHandler();
        security.setConstraintMappings(Collections.singletonList(mapping));
        security.setAuthenticator(new BasicAuthenticator());
        security.setLoginService(loginService);
        security.setHandler(logContext);
        //加载web服务
        HandlerList handlers = new HandlerList();
        handlers.addHandler(defaultAppContext);
        handlers.addHandler(security);
        server.setHandler(handlers);
        //准备启动服务
        this.logger.info("端口:{},应用名称:{},准备启动服务...", port, AppContext.INSTANCE.getAppName());
        //检测端口是否被占用,如果已经被使用则杀死已有服务
        this.checkAndKillPort();
        try {
            //启动服务
            server.start();
        } catch (Exception ex) {
            this.logger.error("server启动异常", ex);
        }
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        this.logger.info("启动成功:{}", pid);
        //修改http响应头Server信息
//        HttpGenerator.setJettyVersion("Zlw(3.3.3)");
    }

    public static int getPort()
    {
        int port = 8080;
        String jettyPort = System.getProperty("jetty.port");
        if (jettyPort != null) {
            port = Integer.parseInt(jettyPort);
        }
        return port;
    }

}
