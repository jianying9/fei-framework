package com.fei.devops.entity;

import com.fei.annotations.component.Resource;
import com.fei.app.test.ResourceMock;
import com.fei.app.utils.ToolUtil;
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
public class AdminInitTest
{

    public AdminInitTest()
    {
    }

    @Resource
    private EsEntityDao<UserEntity> userEntityDao;

    @Resource
    private EsEntityDao<AccountEntity> accountEntityDao;

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
    public void initAdmin()
    {
        //新增管理员
        UserEntity userEntity = new UserEntity();
        userEntity.userName = "admin";
        userEntity.admin = true;
        this.userEntityDao.insert(userEntity);
        //新增账号
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.userId = userEntity.userId;
        accountEntity.userName = userEntity.userName;
        accountEntity.account = "admin";
        accountEntity.password = ToolUtil.encryptByMd5(ToolUtil.getAutomicId());
        accountEntity.enabled = true;
        this.accountEntityDao.insert(accountEntity);
    }

}
