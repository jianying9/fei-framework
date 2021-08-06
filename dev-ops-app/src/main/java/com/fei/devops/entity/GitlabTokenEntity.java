package com.fei.devops.entity;

import com.fei.annotations.elasticsearch.EsColumn;
import com.fei.annotations.elasticsearch.EsEntity;
import com.fei.annotations.elasticsearch.EsKey;
import java.util.Date;

/**
 * gitlabToken存储
 *
 * @author jianying9
 */
@EsEntity
public class GitlabTokenEntity
{

    @EsKey(desc = "id")
    public String id;

    @EsColumn(desc = "访问token")
    public String accessToken;

    @EsColumn(desc = "token类型")
    public String tokenType;

    @EsColumn(desc = "到期时间")
    public long expires;

    @EsColumn(desc = "刷新token")
    public String refreshToken;

    @EsColumn(desc = "创建时间")
    public long createdAt;

    @EsColumn(desc = "时间戳")
    public Date timestamp;
}
