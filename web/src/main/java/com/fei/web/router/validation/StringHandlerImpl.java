package com.fei.web.router.validation;

/**
 * 字符类型处理类
 *
 * @author aladdin
 */
public final class StringHandlerImpl implements ValidationHandler
{

    private final long max;
    private final long min;
    private final String key;
    private final String name;
    private final String type;
    private final String errorMsg;

    public StringHandlerImpl(String key, String name, long max, long min)
    {
        this.key = key;
        this.name = name;
        if (max == Long.MAX_VALUE) {
            max = 512;
        }
        if (min < 0) {
            min = 0;
        }
        //有大小限制
        this.max = max > min ? max : min;
        this.min = max > min ? min : max;
        this.errorMsg = this.name + " must be string["
                + Long.toString(this.min) + ","
                + Long.toString(this.max) + "]";
        this.type = "string["
                + Long.toString(this.min) + ","
                + Long.toString(this.max) + "]";
    }

    @Override
    public String validate(Object value)
    {
        String result = "";
        if (value != null) {
            String v = value.toString();
            if (v.length() > this.max || v.length() < this.min) {
                result = this.errorMsg;
            }
        }
        return result;
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
