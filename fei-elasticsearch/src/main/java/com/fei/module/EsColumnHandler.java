package com.fei.module;

import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author jianying9
 */
public class EsColumnHandler
{

    private final String name;

    private final EsColumnType columnType;

    private final Object defaultValue;

    public EsColumnHandler(String name, EsColumnType columnType, Object defaultValue)
    {
        this.name = name;
        this.columnType = columnType;
        this.defaultValue = defaultValue;
    }

    public String getName()
    {
        return name;
    }

    public EsColumnType getColumnType()
    {
        return columnType;
    }

    public Object getDefaultValue()
    {
        return defaultValue;
    }

    public JSONObject getProperty()
    {
        JSONObject propertyJson = new JSONObject();
        propertyJson.put("type", this.columnType.name().toLowerCase());
        Object nullValue = this.getNullValue();
        propertyJson.put("null_value", nullValue);
        if (this.columnType.equals(EsColumnType.TEXT)) {
            propertyJson.put("analyzer", "ik_max_word");
        } else if (this.columnType.equals(EsColumnType.DATE)) {
            propertyJson.put("format", "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis");
        }
        return propertyJson;
    }
    
    /**
     * 创建mapping时处理null值,首次初始化后不能修改
     * @return 
     */
    private Object getNullValue() {
        Object nullValue;
        switch(this.columnType) {
            case LONG:
            case DOUBLE:
                nullValue = 0;
                break;
            case DATE:
                nullValue = "2014-01-14 08:00:00";
                break;
            case TEXT:
            case KEYWORD:
                nullValue = "";
                break;
            case BOOLEAN:
                nullValue = false;
                break;
            default:
                nullValue = "";
        } 
        return nullValue;
    }

}
