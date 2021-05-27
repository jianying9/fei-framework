package com.fei.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fei.demo.controller.UserController;
import com.fei.demo.controller.UserController.UserAddDto;
import com.fei.demo.controller.UserController.UserDto;
import com.fei.demo.controller.UserController.UserGetDto;
import com.fei.framework.bean.Resource;
import com.fei.framework.test.ResourceMock;
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
        routerMock = new RouterMock(UserController.class);
        resourceMock = new ResourceMock(UserController.class);
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
    public void get()
    {
        UserGetDto userGetDto = new UserGetDto();
        userGetDto.userId = "2";
        JSONObject output = routerMock.perform("/user/get", userGetDto);
        System.out.println(output.toString(SerializerFeature.PrettyFormat));
    }

//    @Test
    public void get2()
    {
        UserGetDto userGetDto = new UserGetDto();
        userGetDto.userId = "1";
        UserDto userDto = this.userController.get(userGetDto);
        System.out.println(JSON.toJSONString(userDto, SerializerFeature.PrettyFormat));
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
        JSONObject output = routerMock.perform("/user/batchGet", input);
        System.out.println(output.toString(SerializerFeature.PrettyFormat));
    }

    @Test
    public void add()
    {
        UserAddDto userAddDto = new UserAddDto();
        userAddDto.userName = "4399";
        userAddDto.desc = "xxx";
        userAddDto.sex = "男";
        JSONObject output = routerMock.perform("/user/add", userAddDto);
        System.out.println(output.toString(SerializerFeature.PrettyFormat));
    }

}
