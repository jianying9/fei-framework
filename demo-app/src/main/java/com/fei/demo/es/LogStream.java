package com.fei.demo.es;

import com.fei.annotations.elasticsearch.EsColumn;
import com.fei.annotations.elasticsearch.EsStream;
import java.util.Date;

/**
 *
 * @author jianying9
 */
@EsStream
public class LogStream
{

    @EsColumn(desc = "id")
    public String userId;

    @EsColumn(desc = "姓名")
    public String userName;

    @EsColumn(desc = "创建时间")
    public Date createTime;

}
