package com.fei.web.request.validation;

/**
 * double类型处理类
 *
 * @author jianying9
 */
public class NumberValidationImpl extends AbstractParamValidation implements ParamValidation
{

    private final String key;
    private final String name;
    private final String type;
    private final String description;
    private final long max;
    private final long min;
    private final String errorMsg;
    private final boolean limit;

    public NumberValidationImpl(String key, String name, long max, long min, String description)
    {
        this.key = key;
        this.name = name;
        this.description = description;
        if (max == Long.MAX_VALUE && min == 0) {
            //无大小限制
            this.limit = false;
            this.max = max;
            this.min = min;
            this.errorMsg = this.name + " must be number";
            this.type = "number";
        } else {
            //有大小限制
            this.limit = true;
            this.max = max > min ? max : min;
            this.min = max > min ? min : max;
            this.errorMsg = this.name + " must be number["
                    + Long.toString(this.min) + ","
                    + Long.toString(this.max) + "]";
            this.type = "number[" + Long.toString(this.min) + "," + Long.toString(this.max) + "]";
        }
    }

    @Override
    public String getKey()
    {
        return this.key;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getType()
    {
        return type;
    }

    @Override
    public String validate(Object value)
    {
        String result = "";
        if (value != null) {
            Double num = null;
            if (value instanceof Number) {
                num = ((Number) value).doubleValue();
            } else if (value instanceof String) {
                String v = value.toString();
                try {
                    Double.parseDouble(v);
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
    public String getDescrption()
    {
        return this.description;
    }

}
