package com.fei.devops.entity;

import com.fei.annotations.component.Resource;
import com.fei.app.test.ResourceMock;
import com.fei.devops.AppMain;
import com.fei.module.EsEntityDao;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jianying9
 */
public class AppEntityInitTest
{

    public AppEntityInitTest()
    {
        //admin
        //aZyav83Ne74KwKFGma2PxW
    }

    @Resource
    private EsEntityDao<AppEntity> appEntityDao;

    private static ResourceMock resourceMock;

    @BeforeClass
    public static void setUpClass()
    {
        resourceMock = new ResourceMock(AppMain.class);
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
        //注入
        resourceMock.resource(this);
    }

    @After
    public void tearDown()
    {
    }

//    @Test
    public void init()
    {
        AppEntity appEntity = new AppEntity();
        appEntity.appName = "gitlab";
        appEntity.appDesc = "版本控制";
        this.appEntityDao.insert(appEntity);
        //
        appEntity = new AppEntity();
        appEntity.appName = "jenkins";
        appEntity.appDesc = "持续集成";
        this.appEntityDao.insert(appEntity);
        //
        appEntity = new AppEntity();
        appEntity.appName = "nginx";
        appEntity.appDesc = "反向代理";
        this.appEntityDao.insert(appEntity);
        
    }

}
