package com.fei.devops.entity;

import com.fei.annotations.elasticsearch.EsColumn;
import com.fei.annotations.elasticsearch.EsEntity;
import com.fei.annotations.elasticsearch.EsKey;
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

    @EsColumn(desc = "时间戳")
    public Date timestamp;

}
