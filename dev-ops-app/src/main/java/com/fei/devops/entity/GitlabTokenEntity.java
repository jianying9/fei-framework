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

    @EsColumn(description = "访问token")
    public String accessToken;

    @EsColumn(description = "token类型")
    public String tokenType;

    @EsColumn(description = "到期时间")
    public long expires;

    @EsColumn(description = "刷新token")
    public String refreshToken;

    @EsColumn(description = "创建时间")
    public long createdAt;

    @EsColumn(description = "时间戳")
    public Date timestamp;
}
