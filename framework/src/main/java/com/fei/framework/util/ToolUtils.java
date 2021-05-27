package com.fei.framework.util;

import com.alibaba.fastjson.JSON;

/**
 * @author aladdin
 *
 */
public final class ToolUtils
{

    /**
     * 过滤前后全角半角空格
     *
     * @param value
     * @return
     */
    public static String trim(String value)
    {
        value = value.replaceAll("&nbsp;", "");
        String result = "";
        int len = value.length();
        if (len > 0) {
            int st = 0;
            int end = len;
            char[] val = value.toCharArray();
            while ((st < end) && (val[st] == ' ' || val[st] == '　')) {
                st++;
            }
            while ((st < end) && (val[end - 1] == ' ' || val[end - 1] == '　')) {
                end--;
            }
            result = ((st > 0) || (end < len)) ? value.substring(st, end) : value;
        }
        return result;
    }

    public static <O> O copy(Object from, Class<O> toClazz)
    {
        String text = JSON.toJSONString(from);
        return JSON.parseObject(text, toClazz);
    }

}
