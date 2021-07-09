package com.fei.devops;

import com.fei.annotations.app.BootApp;
import com.fei.jetty.embed.AppServer;

/**
 * 内置jetty启动
 *
 * @author jianying9
 */
@BootApp("dev-ops")
public class AppMain
{

    //cd /data/app/devops-app
    //nohup java -jar -server -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=128m -Xms256m -Xmx256m -Xss256k -XX:+UseG1GC -XX:ParallelGCThreads=2 devops-app.jar >/dev/null 2>&1 &
    public static void main(String[] args) throws Exception
    {
        AppServer appServer = new AppServer(AppMain.class, args);
        appServer.start();
    }

}
