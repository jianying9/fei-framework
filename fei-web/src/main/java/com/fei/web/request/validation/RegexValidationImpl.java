package com.fei.web.request.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则类型处理类
 *
 * @author jianying9
 */
public class RegexValidationImpl implements ParamValidation
{

    private final String key;
    private final String name;
    private final String type;
    private final Pattern pattern;
    private final String errorMsg;

    public RegexValidationImpl(String key, String name, String regex)
    {
        this.key = key;
        this.name = name;
        this.pattern = Pattern.compile(regex);
        this.errorMsg = this.name + " must be regex(" + regex + ")";
        this.type = "regex(" + regex + ")";
    }

    @Override
    public String validate(Object value)
    {
        String result = "";
        if (value != null) {
            String v = value.toString();
            Matcher matcher = this.pattern.matcher(v);
            if (matcher.matches()) {
                result = "";
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
