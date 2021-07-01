package com.fei.demo.component;

import com.fei.annotations.component.Component;
import com.fei.demo.controller.UserController.UserAddD;
import com.fei.demo.controller.UserController.UserV;
import com.fei.app.utils.ToolUtil;

/**
 *
 * @author jianying9
 */
@Component
public class UserComponent
{

    public UserV getUser(String userId)
    {
        UserV userDto = new UserV();
        userDto.userId = userId;
        userDto.userName = "name1";
        if (userId.equals("1") == false) {
            userDto.userName = "otherName";
        }
        userDto.sex = "男";
        userDto.desc = "呵呵呵呵呵呵呵呵呵呵呵呵";
        return userDto;
    }

    public UserV addUser(UserAddD userAddDto)
    {
        UserV userDto = ToolUtil.copy(userAddDto, UserV.class);
        userDto.userId = "3444";
        return userDto;
    }
}
