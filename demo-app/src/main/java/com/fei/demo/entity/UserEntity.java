package com.fei.demo.entity;

import com.fei.module.EsColumn;
import com.fei.module.EsEntity;
import com.fei.module.EsKey;
import java.util.Date;

/**
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

    @EsColumn(desc = "钱")
    public double money;

    @EsColumn(desc = "是否是小孩")
    public boolean kid;

    @EsColumn(desc = "创建时间")
    public Date createTime;

}
