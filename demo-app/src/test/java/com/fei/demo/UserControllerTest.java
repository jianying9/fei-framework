package com.fei.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fei.annotations.component.Resource;
import com.fei.demo.controller.UserController;
import com.fei.demo.controller.UserController.UserView;
import com.fei.app.test.ResourceMock;
import com.fei.web.test.RouterMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jianying9
 */
public class UserControllerTest
{

    public UserControllerTest()
    {
    }

    @Resource
    private UserController userController;

    private static RouterMock routerMock;

    private static ResourceMock resourceMock;

    @BeforeClass
    public static void setUpClass()
    {
        routerMock = new RouterMock(AppMain.class);
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

    private String auth = "";

//    @Test
    public void get2()
    {
        String userId = "1";
        UserView userView = this.userController.get(userId);
        System.out.println(JSON.toJSONString(userView, SerializerFeature.PrettyFormat));
    }

//    @Test
    public void batchGet()
    {
        JSONObject input = new JSONObject();
        input.put("userId", "1");
        input.put("userId", "1");
        JSONArray array = new JSONArray();
        array.add("2");
        array.add("1");
        input.put("userIdArray", array);
        input.put("userId", 999);
        routerMock.perform("/user/batchGet", input, this.auth);
    }
    
//    @Test
    public void add()
    {
        JSONObject input = new JSONObject();
        input.put("userName", "xxxx");
        input.put("sex", "男");
        input.put("age", 12);
        input.put("desc", "坏人一个");
        //
        JSONArray tagList = new JSONArray();
        tagList.add("爱钱");
        tagList.add("无情");
        input.put("tagList", tagList);
        //
        JSONObject child = new JSONObject();
        child.put("userName", "yyyy");
        child.put("sex", "男");
        JSONArray childList = new JSONArray();
        childList.add(child);
        input.put("childList", childList);
        routerMock.perform("/user/add", input, this.auth);
    }

}
