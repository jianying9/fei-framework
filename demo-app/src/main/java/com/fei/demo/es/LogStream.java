package com.fei.demo.es;

import com.fei.annotations.elasticsearch.EsColumn;
import com.fei.annotations.elasticsearch.EsStream;

/**
 *
 * @author jianying9
 */
@EsStream
public class LogStream
{

    @EsColumn(description = "id")
    public String userId;

    @EsColumn(description = "姓名")
    public String userName;

}
