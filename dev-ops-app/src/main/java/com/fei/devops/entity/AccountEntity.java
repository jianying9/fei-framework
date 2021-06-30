package com.fei.devops.entity;

import com.fei.module.EsColumn;
import com.fei.module.EsEntity;
import com.fei.module.EsKey;
import java.util.Date;

/**
 * 账号
 *
 * @author jianying9
 */
@EsEntity
public class AccountEntity
{

    @EsKey(desc = "id")
    public String account;

    @EsColumn(desc = "密码")
    public String password;

    @EsColumn(desc = "id")
    public String userId;

    @EsColumn(desc = "姓名")
    public String userName;

    @EsColumn(desc = "是否启用")
    public boolean enabled;

    @EsColumn(desc = "创建时间")
    public Date createTime;

}
