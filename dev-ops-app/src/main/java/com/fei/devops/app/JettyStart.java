package com.fei.devops.app;

import com.fei.annotations.app.BootApp;
import com.fei.framework.context.AppContext;
import com.fei.framework.context.AppContextBuilder;
import com.fei.framework.utils.ToolUtil;
import com.fei.jetty.embed.ServerBuilder;
import com.fei.web.servlet.AppServlet;
import java.util.Map;

/**
 * 内置jetty启动
 *
 * @author jianying9
 */
@BootApp
public class JettyStart
{

    //cd /data/app/devops-app
    //nohup java -jar -server -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=128m -Xms256m -Xmx256m -Xss256k -XX:+UseG1GC -XX:ParallelGCThreads=2 devops-app.jar >/dev/null 2>&1 &
    public static void main(String[] args) throws Exception
    {
        //应用名
        final String appName = "devops";
        int port = 8080;
        //优先初始化jetty server,(日志服务)
        ServerBuilder serverBuilder = new ServerBuilder(appName);
        //1、从命令行中读取自定义端口
        if (ServerBuilder.getPort(args) > -1) {
            port = ServerBuilder.getPort(args);
        }
        //框架初始化
        AppContext.INSTANCE.addScanPackage(JettyStart.class);
        Map<String, String> parameterMap = ToolUtil.getAppParams(appName);
        AppContextBuilder appContextBuilder = new AppContextBuilder(parameterMap);
        appContextBuilder.build();
        //启动
        serverBuilder.setPort(port).addServlet(AppServlet.class).build();
    }

}
