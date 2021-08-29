package com.fei.web.response.filter;

/**
 * 基础数据类型
 *
 * @author jianying9
 */
public final class BasicFilterImpl implements ParamFilter
{

    private final String key;
    private final String name;
    private final String type;

    public BasicFilterImpl(String key, String name, String type)
    {
        this.key = key;
        this.name = name;
        this.type = type;
    }

    @Override
    public void filter(Object value)
    {
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getKey()
    {
        return this.key;
    }

    @Override
    public String getType()
    {
        return type;
    }

}
