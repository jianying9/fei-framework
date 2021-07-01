package com.fei.demo.controller;

import com.fei.annotations.component.Resource;
import com.fei.annotations.web.Controller;
import com.fei.annotations.web.RequestMapping;
import com.fei.annotations.web.RequestParam;
import com.fei.demo.component.UserComponent;
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

    public static class UserV
    {

        public String userId;

        public String userName;

        public String sex;

        public String desc;
    }

    public static class UserArrayV
    {

        public List<UserV> userArray;
    }

    @RequestParam(desc = "人")
    public static class UserAddD
    {

        @RequestParam(desc = "用户名称", max = 32)
        public String userName;

        @RequestParam(desc = "用户名称", regexp = "[男|女]")
        public String sex;

        @RequestParam(desc = "年龄", min = 1, max = 150, required = false)
        public int age;

        @RequestParam(desc = "描述", max = 512)
        public String desc;

        @RequestParam(desc = "标签", required = false)
        public List<String> tagList;

        @RequestParam(desc = "小孩", required = false)
        public List<UserV> childList;

    }

//    @RequestMapping(value = "/get", desc = "用户信息获取")
    public UserV get(@RequestParam(desc = "用户id") String userId)
    {
        UserV userDto = this.userComponent.getUser(userId);
        return userDto;
    }

    @RequestMapping(value = "/batchGet", auth = false, desc = "用户信息获取")
    public UserArrayV batchGet(@RequestParam(desc = "用户id集合") List<String> userIdArray)
    {
        UserArrayV userArrayV = new UserArrayV();
        userArrayV.userArray = new ArrayList();
        for (String userId : userIdArray) {
            UserV userV = this.userComponent.getUser(userId);
            userArrayV.userArray.add(userV);
        }
        return userArrayV;
    }

//    @RequestMapping(value = "/add", desc = "用户信息获取")
    public UserV add(UserAddD userAddD)
    {
        UserV userDto = this.userComponent.addUser(userAddD);
        return userDto;
    }

}
