package com.fei.web.request.validation;

import com.fei.app.utils.ToolUtil;
import java.util.Date;

/**
 * 正则类型处理类
 *
 * @author jianying9
 */
public class DateValidationImpl extends AbstractParamValidation implements ParamValidation
{

    private final String key;
    private final String name;
    private final String type;
    private final String description;
    private final String errorMsg;

    public DateValidationImpl(String key, String name, String description)
    {
        this.key = key;
        this.name = name;
        this.description = description;
        this.errorMsg = this.name + " must be date";
        this.type = "date[yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||millis]";
    }

    @Override
    public String validate(Object value)
    {
        String result = "";
        if (value != null) {
            Long num = null;
            if (value instanceof Long || value instanceof Integer) {
                num = ((Number) value).longValue();
            } else if (value instanceof String) {
                String v = (String) value;
                try {
                    Date date = ToolUtil.parse(v, "yyyy-MM-dd HH:mm:ss");
                    num = date.getTime();
                } catch (RuntimeException e) {
                }
                //
                if (num == null) {
                    try {
                        Date date = ToolUtil.parse(v, "yyyy-MM-dd");
                        num = date.getTime();
                    } catch (RuntimeException e) {
                    }
                }
            }
            if (num == null) {
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

    @Override
    public String getDescrption()
    {
        return this.description;
    }

}
