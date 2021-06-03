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

    /**
     * 对象复制
     *
     * @param <T>
     * @param from
     * @param toClazz
     * @return
     */
    public static <T> T copy(Object from, Class<T> toClazz)
    {
        String text = JSON.toJSONString(from);
        return JSON.parseObject(text, toClazz);
    }

    /**
     * 根据类获取数据库表明,大写转小写，并且家下划线.UserEntity->user_entity
     *
     * @param clazz
     * @return
     */
    public static String getTableName(Class<?> clazz)
    {
        String tableName = clazz.getName();
        int index = tableName.lastIndexOf(".");
        if (index > 0) {
            tableName = tableName.substring(index);
        }
        //大小写转换
        StringBuilder sb = new StringBuilder();
        char[] charArray = tableName.toCharArray();
        for (char c : charArray) {
            if (c >= 'A' && c <= 'Z') {
                if (sb.length() == 0) {
                    sb.append(c);
                } else {
                    sb.append('_').append(c);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
