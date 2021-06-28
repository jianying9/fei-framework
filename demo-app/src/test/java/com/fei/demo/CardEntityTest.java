package com.fei.demo;

import com.alibaba.fastjson.JSON;
import com.fei.demo.entity.CardEntity;
import com.fei.elasticsearch.index.query.BoolQueryBuilder;
import com.fei.elasticsearch.index.query.QueryBuilders;
import com.fei.framework.bean.Resource;
import com.fei.framework.test.ResourceMock;
import com.fei.framework.utils.ToolUtil;
import com.fei.module.EsConfig;
import com.fei.module.EsEntityDao;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author jianying9
 */
public class CardEntityTest
{

    public CardEntityTest()
    {
    }

    @Resource
    private EsEntityDao<CardEntity> cardEntityDao;

    private static ResourceMock resourceMock;

    @BeforeClass
    public static void setUpClass()
    {
        Map<String, String> parameterMap = new HashMap();
        parameterMap.put(EsConfig.URL, "http://a.zlw333.com/es");
        parameterMap.put(EsConfig.DATABASE, "demo");
        parameterMap.put(EsConfig.USER, "elastic");
        parameterMap.put(EsConfig.PASSWORD, "");
//        parameterMap.put(EsConfig.HTTP_CERTIFICATE, "/Users/jianying9/data/es-zlw/elastic-certificates.p12");
        resourceMock = new ResourceMock(CardEntity.class, parameterMap);
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
        CardEntity cardEntity = new CardEntity();
        cardEntity.longList = new ArrayList();
        cardEntity.longList.add(1l);
        cardEntity.longList.add(2l);
        //
        cardEntity.boolSet = new HashSet();
        cardEntity.boolSet.add(true);
        //
        cardEntity.stringArray = new String[]{"4", "5", "6"};
        //
        cardEntity.dateCollection = new LinkedList();
        long time = System.currentTimeMillis();
        cardEntity.dateCollection.add(new Date(time));
        time += 128000;
        cardEntity.dateCollection.add(new Date(time));
        time += 128000;
        cardEntity.dateCollection.add(new Date(time));
        //
        this.cardEntityDao.insert(cardEntity);
    }

//    @Test
    public void get()
    {
        CardEntity cardEntity = this.cardEntityDao.get("iA7WXLHAAtjCqPdgMer8Bm");
        System.out.println(JSON.toJSONStringWithDateFormat(cardEntity, ToolUtil.DATE_FORMAT));
    }

//    @Test
    public void search()
    {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        boolQueryBuilder.must(QueryBuilders.termQuery("userName", "fei092"));
        boolQueryBuilder.filter(QueryBuilders.termsQuery("longList", 1l, 9223372036854775807l));
        List<CardEntity> cardList = this.cardEntityDao.search(boolQueryBuilder, 0, 100);
        for (CardEntity cardEntity : cardList) {
            System.out.println(JSON.toJSONStringWithDateFormat(cardEntity, ToolUtil.DATE_FORMAT));
        }
    }

}
