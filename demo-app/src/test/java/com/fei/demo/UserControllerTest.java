package com.fei.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fei.demo.controller.UserController;
import com.fei.demo.controller.UserController.UserDto;
import com.fei.demo.controller.UserController.UserGetDto;
import com.fei.framework.bean.Resource;
import com.fei.framework.test.ResourceMock;
import com.fei.web.test.RouterMock;
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

    @BeforeClass
    public static void setUpClass()
    {
        routerMock = new RouterMock(UserController.class);
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
        ResourceMock resourceMock = new ResourceMock(UserController.class);
        resourceMock.resource(this);
    }

    @Test
    public void get()
    {
        UserGetDto userGetDto = new UserGetDto();
        userGetDto.userId = "2";
        routerMock.perform("/user/get", userGetDto);
    }

    @Test
    public void get2()
    {
        UserGetDto userGetDto = new UserGetDto();
        userGetDto.userId = "1";
        UserDto userDto = this.userController.get(userGetDto);
        System.out.println(JSON.toJSONString(userDto, SerializerFeature.PrettyFormat));
    }

}
