package com.fei.demo.es;

import com.fei.annotations.elasticsearch.EsColumn;
import com.fei.annotations.elasticsearch.EsEntity;
import com.fei.annotations.elasticsearch.EsKey;
import java.util.Date;
import java.util.List;

/**
 *
 * @author jianying9
 */
@EsEntity
public class UserEntity
{

    @EsKey(desc = "id", auto = true)
    public String userId;

    @EsColumn(description = "姓名")
    public String userName;

    @EsColumn(description = "钱")
    public double money;

    @EsColumn(description = "是否是小孩")
    public boolean kid;

    @EsColumn(description = "时间戳")
    public Date timestamp;

}
