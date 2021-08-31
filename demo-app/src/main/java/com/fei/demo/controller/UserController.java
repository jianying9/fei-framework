package com.fei.demo.controller;

import com.fei.annotations.component.Resource;
import com.fei.annotations.web.Controller;
import com.fei.annotations.web.RequestMapping;
import com.fei.annotations.web.RequestParam;
import com.fei.annotations.web.ResponseParam;
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

    public static class UserView
    {

        @ResponseParam(description = "id")
        public String userId;

        @ResponseParam(description = "名称")
        public String userName;

        @ResponseParam(description = "性别")
        public String sex;

        @ResponseParam(description = "描述")
        public String desc;

        @ResponseParam(description = "小孩")
        public List<Child> childList;
    }

    public static class UserArrayView
    {

        public List<UserView> userArray;
    }

    public static class UserAddRequest
    {

        @RequestParam(description = "用户名称", max = 32)
        public String userName;

        @RequestParam(description = "用户名称", regexp = "[男|女]")
        public String sex;

        @RequestParam(description = "年龄", min = 1, max = 120)
        public int age;

        @RequestParam(description = "描述", max = 512)
        public String desc;

        @RequestParam(description = "标签", required = false)
        public List<String> tagList;

        @RequestParam(description = "小孩")
        public List<Child> childList;

    }

    public static class Child
    {

        @RequestParam(description = "名称", max = 4)
        @ResponseParam(description = "名称")
        public String userName;

        @RequestParam(description = "性别")
//        @ResponseParam(description = "性别")
        public String sex;
    }

//    @RequestMapping(value = "/get", description = "用户信息获取")
    public UserView get(@RequestParam(description = "用户id") String userId)
    {
        UserView userDto = this.userComponent.getUser(userId);
        return userDto;
    }

//    @RequestMapping(value = "/batchGet", auth = false, description = "用户信息获取")
    public UserArrayView batchGet(@RequestParam(description = "用户id集合") List<String> userIdArray)
    {
        UserArrayView userArrayView = new UserArrayView();
        userArrayView.userArray = new ArrayList();
        for (String userId : userIdArray) {
            UserView userV = this.userComponent.getUser(userId);
            userArrayView.userArray.add(userV);
        }
        return userArrayView;
    }

    @RequestMapping(value = "/add", description = "用户信息获取")
    public UserView add(UserAddRequest userAddRequest)
    {
        UserView userView = this.userComponent.addUser(userAddRequest);
        return userView;
    }

}
