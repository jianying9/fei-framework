package com.fei.demo;

import com.fei.annotations.component.Resource;
import com.fei.app.test.ResourceMock;
import com.fei.module.BizLogStream;
import com.fei.module.EsStreamDao;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jianying9
 */
public class BizLogStreamTest
{

    public BizLogStreamTest()
    {
    }

    @Resource
    private EsStreamDao<BizLogStream> logStreamDao;

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
    public void search()
    {
        List<BizLogStream> logList = this.logStreamDao.search(0, 100);
        for (BizLogStream logStream : logList) {
            System.out.println(logStream.userId);
        }
    }

}
