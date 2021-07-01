package com.fei.web.component;

import java.util.Date;

/**
 *
 * @author jianying9
 */
public class Token
{
    public String auth;

    public String userId;

    public String userName;

    public Date expireTime;

    public boolean expired = false;

}
