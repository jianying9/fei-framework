package com.fei.demo;

import com.fei.demo.entity.UserEntity;
import com.fei.framework.bean.Resource;
import com.fei.framework.test.ResourceMock;
import com.fei.module.EsConfig;
import com.fei.module.EsContext;
import com.fei.module.EsEntityDao;
import java.util.HashMap;
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
        Map<String, String> parameterMap = new HashMap();
        parameterMap.put(EsConfig.HOST, "106.14.237.124");
        parameterMap.put(EsConfig.DATABASE, "fei");
        parameterMap.put(EsConfig.USER, "elastic");
        parameterMap.put(EsConfig.PASSWORD, "Cghi34EulPRN0ksOP8um");
        parameterMap.put(EsConfig.HTTP_CERTIFICATE, "/Users/jianying9/data/es-zlw/elastic-certificates.p12");
        resourceMock = new ResourceMock(UserEntity.class, parameterMap);
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
        //更新表定义
        EsContext.INSTANCE.updateMapping();
    }

    @After
    public void tearDown()
    {
    }

//    @Test
    public void add()
    {
        //Y2SaHmtcAzUB7wyToV8MhY
        UserEntity userEntity = new UserEntity();
        userEntity.userName = "fei002";
        userEntity.kid = false;
        userEntity.money = 3100;
        userEntity.createTime = System.currentTimeMillis();
        this.userEntityDao.insert(userEntity);
        System.out.println(userEntity.userId);
    }

//    @Test
    public void update1()
    {
        //Y2SaHmtcAzUB7wyToV8MhY
        UserEntity userEntity = new UserEntity();
        userEntity.userId = "Y2SaHmtcAzUB7wyToV8MhY";
        userEntity.userName = "fei003";
        userEntity.kid = true;
        userEntity.money = 3300;
        userEntity.createTime = System.currentTimeMillis();
        this.userEntityDao.update(userEntity);
        System.out.println(userEntity.userId);
    }

//    @Test
    public void update2()
    {
        //Y2SaHmtcAzUB7wyToV8MhY
        Map<String, Object> updateMap = new HashMap();
        updateMap.put("kid", false);
        updateMap.put("money", 2200);
        this.userEntityDao.update("Y2SaHmtcAzUB7wyToV8MhY", updateMap);
    }

//    @Test
    public void get()
    {
        //Y2SaHmtcAzUB7wyToV8MhY
        UserEntity userEntity = this.userEntityDao.get("Y2SaHmtcAzUB7wyToV8MhY");
        System.out.println(userEntity.userName);
    }

//    @Test
    public void upsert()
    {
        //Y2SaHmtcAzUB7wyToV8MhY
        UserEntity userEntity = new UserEntity();
        userEntity.userId = "Y2SaHmtcAzUB7wyToV8MhY";
        userEntity.userName = "fei005";
        userEntity.kid = false;
        userEntity.money = 99;
        userEntity.createTime = System.currentTimeMillis();
        this.userEntityDao.upsert(userEntity);
        System.out.println(userEntity.userId);
    }

//    @Test
    public void delete()
    {
        this.userEntityDao.delete("xRagf5tWV527fmEaXxt9UU");
    }

}
