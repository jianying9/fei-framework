package com.fei.demo;

import com.fei.annotations.component.Resource;
import com.fei.elasticsearch.index.query.BoolQueryBuilder;
import com.fei.elasticsearch.index.query.QueryBuilders;
import com.fei.elasticsearch.search.sort.SortBuilder;
import com.fei.elasticsearch.search.sort.SortBuilders;
import com.fei.elasticsearch.search.sort.SortOrder;
import com.fei.app.test.ResourceMock;
import com.fei.demo.es.LogStream;
import com.fei.module.EsStreamDao;
import java.util.Date;
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
public class LogStreamTest
{

    public LogStreamTest()
    {
    }

    @Resource
    private EsStreamDao<LogStream> logStreamDao;

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
    public void add()
    {
        LogStream logStream = new LogStream();
        logStream.userId = "fei0192";
        logStream.userName = "fei0192";
        logStream.createTime = new Date();
        this.logStreamDao.insert(logStream);
    }

    @Test
    public void search()
    {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("userName", "fei0192"));
        SortBuilder sortBuilder = SortBuilders.fieldSort("createTime").order(SortOrder.DESC);
        List<LogStream> logList = this.logStreamDao.search(boolQueryBuilder, sortBuilder, 0, 100);
        for (LogStream logStream : logList) {
            System.out.println(logStream.userId);
        }
    }

}
