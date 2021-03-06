package com.fei.web.router;

/**
 *
 * @author jianying9
 */
public class BizContext
{

    //接口
    private final String route;

    //用户id
    private String userId;

    //用户
    private String userName;

    //业务分组追踪id(微服务垮服务器执行追踪)
    private String groupId = "";

    //接口执行开始时间
    private long startTime = 0;

    //接口执行结束时间
    private long endTime = 0;

    //control业务执行开始时间
    private long subStartTime = 0;

    //control业务执行结束时间
    private long subEndTime = 0;

    public BizContext(String route)
    {
        this.route = route;
    }

    public String getRoute()
    {
        return route;
    }

    public long getStartTime()
    {
        return startTime;
    }

    void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }

    public long getEndTime()
    {
        return endTime;
    }

    void setEndTime(long endTime)
    {
        this.endTime = endTime;
    }

    public String getGroupId()
    {
        return groupId;
    }

    void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public long getProcessTime()
    {
        return this.endTime - this.startTime;
    }

    public long getSubStartTime()
    {
        return subStartTime;
    }

    void setSubStartTime(long subStartTime)
    {
        this.subStartTime = subStartTime;
    }

    public long getSubEndTime()
    {
        return subEndTime;
    }

    void setSubEndTime(long subEndTime)
    {
        this.subEndTime = subEndTime;
    }

    public long getSubProcessTime()
    {
        return this.subEndTime - this.subStartTime;
    }

    public String getUserId()
    {
        return userId;
    }

    void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getUserName()
    {
        return userName;
    }

    void setUserName(String userName)
    {
        this.userName = userName;
    }

}
