package com.fei.framework.util;

import com.alibaba.fastjson.JSON;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.UUID;

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
        if (index >= 0) {
            tableName = tableName.substring(index + 1);
        }
        //大小写转换
        StringBuilder sb = new StringBuilder();
        char[] charArray = tableName.toCharArray();
        for (char c : charArray) {
            if (c >= 'A' && c <= 'Z') {
                c = (char) (c + 32);
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

    /**
     * 获取class的示例
     *
     * @param clazz
     * @return
     */
    public static <T> T create(Class<?> clazz)
    {
        T t = null;
        try {
            Field field = clazz.getField("INSTANCE");
            if (Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                t = (T) field.get(clazz);
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
        }
        if (t == null) {
            try {
                t = (T) clazz.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return t;
    }

    //base64编码去除数字0,1,大写字母I,O,小写字母l,符号+,/
    private final static char[] base57 = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();

    /**
     * 生成唯一id
     *
     * @return
     */
    public final static String getAutomicId()
    {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        BigInteger uuidNumber = new BigInteger(uuid, 16);
        BigInteger baseSize = BigInteger.valueOf(base57.length);
        StringBuilder automicId = new StringBuilder();
        BigInteger[] fracAndRemainder;
        while (uuidNumber.compareTo(BigInteger.ZERO) > 0) {
            fracAndRemainder = uuidNumber.divideAndRemainder(baseSize);
            automicId.append(base57[fracAndRemainder[1].intValue()]);
            uuidNumber = fracAndRemainder[0];
        }
        if (automicId.length() < 22) {
            //小于22位,补充base57[0]
            int padding = 22 - automicId.length();
            for (int i = 0; i < padding; i++) {
                automicId.append(base57[0]);
            }
        }
        return automicId.toString();
    }

    public final static String decodeAutomicId(String id)
    {
        char[] idArray = id.toCharArray();
        BigInteger sum = BigInteger.ZERO;
        BigInteger baseSize = BigInteger.valueOf(base57.length);
        for (int i = 0; i < idArray.length; i++) {
            BigInteger n = baseSize.pow(i).multiply(BigInteger.valueOf(Arrays.binarySearch(base57, idArray[i])));
            sum = sum.add(n);
        }
        String str = sum.toString(16);
        return str;
    }

}
