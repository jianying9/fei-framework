package com.fei.demo;

import com.alibaba.fastjson.JSON;
import com.fei.annotations.component.Resource;
import com.fei.demo.es.UserEntity;
import com.fei.elasticsearch.index.query.BoolQueryBuilder;
import com.fei.elasticsearch.index.query.QueryBuilders;
import com.fei.elasticsearch.search.sort.SortBuilder;
import com.fei.elasticsearch.search.sort.SortBuilders;
import com.fei.elasticsearch.search.sort.SortOrder;
import com.fei.app.test.ResourceMock;
import com.fei.app.utils.ToolUtil;
import com.fei.module.EsEntityDao;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jianying9
 */
public class UserEntityTest
{

    public UserEntityTest()
    {
    }

    @Resource
    private EsEntityDao<UserEntity> userEntityDao;

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
        UserEntity userEntity = new UserEntity();
        userEntity.userName = "fei0192";
        userEntity.kid = false;
        userEntity.money = 4300;
        this.userEntityDao.insert(userEntity);
        System.out.println(userEntity.userId);
    }

//    @Test
    public void update1()
    {
        UserEntity userEntity = new UserEntity();
        userEntity.userId = "4SkA377EeN6PNinTtLckpY";
        userEntity.userName = "fei0192u";
        userEntity.kid = true;
        userEntity.money = 9;
        this.userEntityDao.update(userEntity);
    }

//    @Test
    public void update2()
    {
        Map<String, Object> updateMap = new HashMap();
        updateMap.put("kid", false);
        updateMap.put("createTime", new Date());
        this.userEntityDao.update("qMkNVWNRZxwLBHVx4GU7Gf", updateMap);
    }

//    @Test
    public void get()
    {
        UserEntity userEntity = this.userEntityDao.get("qMkNVWNRZxwLBHVx4GU7Gf");
        System.out.println(userEntity.userName);
    }

//    @Test
    public void upsert()
    {
        UserEntity userEntity = new UserEntity();
        userEntity.userName = "fei0192s";
        userEntity.kid = false;
        userEntity.money = 99;
        this.userEntityDao.upsert(userEntity);
        System.out.println(userEntity.userId);
    }

//    @Test
    public void upsert2()
    {
        UserEntity userEntity = new UserEntity();
        userEntity.kid = true;
        userEntity.userId = "AUR7pFrG8zmdpCjqFUhkfn";
        this.userEntityDao.upsert(userEntity);
        System.out.println(userEntity.userId);
    }

//    @Test
    public void delete()
    {
        this.userEntityDao.delete("TWoa6X3y5mcveSDghqRXxW");
    }

//    @Test
    public void total()
    {
        int total = this.userEntityDao.total();
        System.out.println(total);
    }

    @Test
    public void search()
    {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        boolQueryBuilder.must(QueryBuilders.termQuery("userName", "fei092"));
        boolQueryBuilder.filter(QueryBuilders.existsQuery("money"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("createTime").gt("2021-06-27 22:00:00").lt("2021-06-27 23:00:00"));
        SortBuilder sortBuilder = SortBuilders.fieldSort("createTime").order(SortOrder.DESC);
        List<UserEntity> userList = this.userEntityDao.search(boolQueryBuilder, sortBuilder, 0, 100);
        for (UserEntity userEntity : userList) {
            System.out.println(JSON.toJSONStringWithDateFormat(userEntity, ToolUtil.DATE_FORMAT));
        }
    }

}
