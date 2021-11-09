package com.fei.app.utils;

import com.alibaba.fastjson.JSON;
import com.fei.app.context.AppContext;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jianying9
 *
 */
public final class ToolUtil
{

    public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

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
     * @param <T>
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
        return encodeAutomicId(uuid);
    }
    
    public final static String encodeAutomicId(String id)
    {
        BigInteger uuidNumber = new BigInteger(id, 16);
        BigInteger baseSize = BigInteger.valueOf(base57.length);
        StringBuilder automicId = new StringBuilder();
        BigInteger[] fracAndRemainder;
        while (uuidNumber.compareTo(BigInteger.ZERO) > 0) {
            fracAndRemainder = uuidNumber.divideAndRemainder(baseSize);
            automicId.append(base57[fracAndRemainder[1].intValue()]);
            uuidNumber = fracAndRemainder[0];
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

    /**
     * byte转16进制字符
     *
     * @param textByte
     * @return
     */
    public static String byteToHexString(byte[] textByte)
    {
        StringBuilder hexString = new StringBuilder(32);
        int byteValue;
        for (byte bt : textByte) {
            byteValue = 0xFF & bt;
            if (byteValue < 16) {
                hexString.append('0').append(Integer.toHexString(byteValue));
            } else {
                hexString.append(Integer.toHexString(byteValue));
            }
        }
        return hexString.toString();
    }

    /**
     * 16进制字符转byte
     *
     * @param hexString
     * @return
     */
    public static byte[] hexStringToByte(String hexString)
    {
        byte[] result = new byte[hexString.length() / 2];
        String str;
        int byteValue;
        for (int index = 0; index < hexString.length(); index = index + 2) {
            str = hexString.substring(index, index + 2);
            byteValue = Integer.parseInt(str, 16);
            result[index / 2] = (byte) byteValue;
        }
        return result;
    }

    /**
     * 用MD5加密
     *
     * @param str
     * @return
     */
    public static String encryptByMd5(String str)
    {
        String result = "";
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            byte[] messageDigest = algorithm.digest(str.getBytes());
            result = byteToHexString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
        }
        return result;
    }

    public static String encryptBySHA256(String str)
    {
        String result = "";
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
            algorithm.reset();
            byte[] messageDigest = algorithm.digest(str.getBytes());
            result = byteToHexString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
        }
        return result;
    }

    public static String randomString(int targetStringLength)
    {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }

    public static String format(Date date)
    {
        return format(date, DATE_FORMAT);
    }

    public static String format(Date date, String format)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static Date parse(String date)
    {
        return parse(date, DATE_FORMAT);
    }

    public static Date parse(String date, String format)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> getAppParams(String appName)
    {
        Map<String, String> paramaterMap = new HashMap();
        String fileName = appName + ".properties";
        Properties properties = new Properties();
        InputStream in = ToolUtil.class.getClassLoader().getResourceAsStream(fileName);
        if (in != null) {
            try {
                properties.load(in);
            } catch (IOException ex) {
                Logger logger = LoggerFactory.getLogger(AppContext.class);
                logger.warn(fileName + " not found...");
            }
            for (String key : properties.stringPropertyNames()) {
                paramaterMap.put(key, properties.getProperty(key));
            }
        }
        return paramaterMap;
    }

    public static boolean isBasicType(Class<?> type)
    {
        return type.isPrimitive() || type == String.class || type == Date.class || Number.class.isAssignableFrom(type);
    }

}
