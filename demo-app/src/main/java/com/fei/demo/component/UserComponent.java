package com.fei.demo.component;

import com.fei.demo.controller.UserController.UserAddDto;
import com.fei.demo.controller.UserController.UserDto;
import com.fei.framework.utils.ToolUtil;
import com.fei.module.Component;

/**
 *
 * @author jianying9
 */
@Component
public class UserComponent
{

    public UserDto getUser(String userId)
    {
        UserDto userDto = new UserDto();
        userDto.userId = userId;
        userDto.userName = "name1";
        if (userId.equals("1") == false) {
            userDto.userName = "otherName";
        }
        userDto.sex = "男";
        userDto.desc = "呵呵呵呵呵呵呵呵呵呵呵呵";
        return userDto;
    }

    public UserDto addUser(UserAddDto userAddDto)
    {
        UserDto userDto = ToolUtil.copy(userAddDto, UserDto.class);
        userDto.userId = "3444";
        return userDto;
    }
}
