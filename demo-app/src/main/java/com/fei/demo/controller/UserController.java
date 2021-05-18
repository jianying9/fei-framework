package com.fei.demo.controller;

import com.fei.demo.component.UserComponent;
import com.fei.framework.bean.Resource;
import com.fei.module.Controller;
import com.fei.module.RequestMapping;
import java.util.ArrayList;
import java.util.List;

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

    public static class UserArrayDto
    {

        public List<UserDto> userArray;
    }

    public static class UserGetDto
    {

        public String userId;

    }

    public static class UserBatchGetDto
    {

        public List<String> userIdArray;

    }

    @RequestMapping(value = "/get", desc = "用户信息获取")
    public UserDto get(UserGetDto userGetDto)
    {
        UserDto userDto = this.userComponent.getUser(userGetDto.userId);
        return userDto;
    }

    @RequestMapping(value = "/batchGet", desc = "用户信息获取")
    public UserArrayDto batchGet(UserGetDto userGetDto, UserBatchGetDto userBatchGetDto)
    {
        UserArrayDto userArrayDto = new UserArrayDto();
        userArrayDto.userArray = new ArrayList();
        for (String userId : userBatchGetDto.userIdArray) {
            UserDto userDto = this.userComponent.getUser(userId);
            userArrayDto.userArray.add(userDto);
        }
        return userArrayDto;
    }

}
