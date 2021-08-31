package com.fei.web.request.validation;

/**
 * 字符类型处理类
 *
 * @author jianying9
 */
public final class StringValidationImpl extends AbstractParamValidation implements ParamValidation
{

    private final long max;
    private final long min;
    private final String key;
    private final String name;
    private final String type;
    private final String errorMsg;
    private final String description;

    public StringValidationImpl(String key, String name, long max, long min, String description)
    {
        this.key = key;
        this.name = name;
        this.description = description;
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

    @Override
    public String getDescrption()
    {
        return this.description;
    }

}
