package com.fei.demo.component;

import com.fei.annotations.component.Component;
import com.fei.demo.controller.UserController.UserAddRequest;
import com.fei.demo.controller.UserController.UserView;
import com.fei.app.utils.ToolUtil;

/**
 *
 * @author jianying9
 */
@Component
public class UserComponent
{

    public UserView getUser(String userId)
    {
        UserView userDto = new UserView();
        userDto.userId = userId;
        userDto.userName = "name1";
        if (userId.equals("1") == false) {
            userDto.userName = "otherName";
        }
        userDto.sex = "男";
        userDto.desc = "呵呵呵呵呵呵呵呵呵呵呵呵";
        return userDto;
    }

    public UserView addUser(UserAddRequest userAddDto)
    {
        UserView userDto = ToolUtil.copy(userAddDto, UserView.class);
        userDto.userId = "3444";
        return userDto;
    }
}
