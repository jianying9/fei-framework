package com.fei.demo.es;

import com.fei.annotations.elasticsearch.EsColumn;
import com.fei.annotations.elasticsearch.EsEntity;
import com.fei.annotations.elasticsearch.EsKey;
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
