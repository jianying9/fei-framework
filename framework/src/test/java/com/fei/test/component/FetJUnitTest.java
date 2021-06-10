package com.fei.test.component;

import com.fei.framework.bean.BeanContext;
import com.fei.framework.context.AppContext;
import com.fei.framework.context.AppContextBuilder;
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
public class FetJUnitTest
{

    public FetJUnitTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void init()
    {
        AppContext.INSTANCE.addScanPackage("com.fei.test");
        Map<String, String> parameterMap = new HashMap();
        AppContextBuilder appContextBuilder = new AppContextBuilder(parameterMap);
        appContextBuilder.build();
        //
        BeanContext beanContext = AppContext.INSTANCE.getBeanContext();
        TestResource testAutowired = beanContext.get("component", TestResource.class);
        testAutowired.info();
    }
}
