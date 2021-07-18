package com.fei.devops.entity;

import com.fei.annotations.elasticsearch.EsColumn;
import com.fei.annotations.elasticsearch.EsEntity;
import com.fei.annotations.elasticsearch.EsKey;
import java.util.Date;

/**
 * 用户
 *
 * @author jianying9
 */
@EsEntity
public class UserEntity
{

    @EsKey(desc = "id", auto = true)
    public String userId;

    @EsColumn(desc = "姓名")
    public String userName;

    @EsColumn(desc = "是否管理员")
    public boolean admin;

    @EsColumn(desc = "时间戳")
    public Date timestamp;

}
