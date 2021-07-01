package com.fei.web.router.validation;

/**
 * boolean类型处理类
 *
 * @author jianying9
 */
public class BooleanHandlerImpl implements ValidationHandler
{

    private final String key;
    private final String name;
    private final String type;
    private final String errorMsg;

    public BooleanHandlerImpl(String key, String name)
    {
        this.key = key;
        this.name = name;
        this.type = "boolean";
        this.errorMsg = this.name + " must be boolean";
    }

    @Override
    public String validate(Object value)
    {
        String result = "";
        if (value != null) {
            if (value instanceof Boolean) {
                result = "";
            } else if (value instanceof String) {
                String v = (String) value;
                v = v.toLowerCase();
                if (v.equals("true") || v.equals("false")) {
                    result = "";
                } else {
                    result = this.errorMsg;
                }
            } else {
                result = this.errorMsg;
            }
        }
        return result;
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
        return this.type;
    }

}
