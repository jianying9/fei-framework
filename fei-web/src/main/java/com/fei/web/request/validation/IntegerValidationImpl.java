package com.fei.web.request.validation;

/**
 * 数字类型处理类
 *
 * @author jianying9
 */
public final class IntegerValidationImpl implements ParamValidation
{

    private final String key;
    private final String name;
    private final String type;
    private final long max;
    private final long min;
    private final String errorMsg;
    private final boolean limit;

    public IntegerValidationImpl(String key, String name, long max, long min)
    {
        this.key = key;
        this.name = name;
        if (max == Long.MAX_VALUE && min == 0) {
            //无大小限制
            this.limit = false;
            this.max = max;
            this.min = min;
            this.errorMsg = this.name + " must be integer";
            this.type = "integer";
        } else {
            //有大小限制
            this.limit = true;
            this.max = max > min ? max : min;
            this.min = max > min ? min : max;
            this.errorMsg = this.name + " must be integer["
                    + Long.toString(this.min) + ","
                    + Long.toString(this.max) + "]";
            type = "integer[" + Long.toString(this.min) + "," + Long.toString(this.max) + "]";
        }
    }

    @Override
    public String validate(final Object value)
    {
        String result = "";
        if (value != null) {
            Long num = null;
            if (value instanceof Long || value instanceof Integer) {
                num = ((Number) value).longValue();
            } else if (value instanceof String) {
                String v = (String) value;
                try {
                    num = Long.valueOf(v);
                } catch (NumberFormatException e) {
                }
            }
            if (num == null) {
                result = this.errorMsg;
            } else {
                if (this.limit) {
                    if (num > this.max || num < this.min) {
                        result = this.errorMsg;
                    }
                }
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
