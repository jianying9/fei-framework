package com.fei.demo.component;

import com.fei.demo.controller.UserController.UserDto;
import com.fei.module.Component;

/**
 *
 * @author jianying9
 */
@Component
public class UserComponent
{
    public UserDto getUser(String userId) {
        UserDto userDto = new UserDto();
        userDto.userId = userId;
        userDto.userName = "name1";
        if (userId.equals("1") == false) {
            userDto.userName = "otherName";
        }
        return userDto;
    }
}
