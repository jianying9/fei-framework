package com.fei.demo.controller;

import com.fei.demo.component.UserComponent;
import com.fei.framework.bean.Resource;
import com.fei.module.Controller;
import com.fei.module.RequestMapping;
import java.util.ArrayList;
import java.util.List;
import com.fei.module.RequestParam;

/**
 *
 * @author jianying9
 */
@Controller(value = "/user", name = "用户", auth = true)
public class UserController
{

    @Resource
    private UserComponent userComponent;

    public static class UserDto
    {

        public String userId;

        public String userName;

        public String sex;

        public String desc;
    }

    public static class UserArrayDto
    {

        public List<UserDto> userArray;
    }

    public static class UserGetDto
    {

        @RequestParam(desc = "用户id", max = 32, min = 32)
        public String userId;
    }

    @RequestParam(desc = "人")
    public static class UserAddDto
    {

        @RequestParam(desc = "用户名称", max = 32)
        public String userName;

        @RequestParam(desc = "用户名称", regexp = "[男|女]")
        public String sex;

        @RequestParam(desc = "年龄", min = 1, max = 150, notNull = false)
        public int age;

        @RequestParam(desc = "描述", max = 512)
        public String desc;

        @RequestParam(desc = "标签", notNull = false)
        public List<String> tagList;

        @RequestParam(desc = "小孩", notNull = false)
        public List<UserDto> childList;

    }

    public static class UserBatchGetDto
    {

        @RequestParam(desc = "用户id集合")
        public List<String> userIdArray;
    }

//    @RequestMapping(value = "/get", desc = "用户信息获取")
    public UserDto get(UserGetDto userGetDto)
    {
        UserDto userDto = this.userComponent.getUser(userGetDto.userId);
        return userDto;
    }

//    @RequestMapping(value = "/batchGet", desc = "用户信息获取")
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

    @RequestMapping(value = "/add", desc = "用户信息获取")
    public UserDto add(UserAddDto userAddDto)
    {
        UserDto userDto = this.userComponent.addUser(userAddDto);
        return userDto;
    }

}
