package com.fei.demo.controller;

import com.fei.demo.component.UserComponent;
import com.fei.framework.bean.Resource;
import com.fei.module.Controller;
import com.fei.module.RequestMapping;

/**
 *
 * @author jianying9
 */
@Controller(value = "/user", name = "用户")
public class UserController
{

    @Resource
    private UserComponent userComponent;

    public static class UserDto
    {

        public String userId;

        public String userName;
    }

    public static class UserGetDto
    {

        public String userId;

    }

    @RequestMapping(value = "/get", desc = "用户信息获取")
    public UserDto get(UserGetDto userGetDto)
    {
        UserDto userDto = this.userComponent.getUser(userGetDto.userId);
        return userDto;
    }
}
