package com.fei.module;

import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author jianying9
 */
public class EsColumnHandler
{

    private final String fieldName;
    
    private final String columnName;

    private final EsColumnType columnType;

    private final Object defaultValue;

    public EsColumnHandler(String filedName, EsColumnType columnType, Object defaultValue)
    {
        this.fieldName = filedName;
        this.columnName = filedName;
        this.columnType = columnType;
        this.defaultValue = defaultValue;
    }
    
    public EsColumnHandler(String filedName, String columnName, EsColumnType columnType, Object defaultValue)
    {
        this.fieldName = filedName;
        this.columnName = columnName;
        this.columnType = columnType;
        this.defaultValue = defaultValue;
    }
    
    public String getFieldName()
    {
        return fieldName;
    }

    public String getColumnName()
    {
        return columnName;
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
        switch (this.columnType) {
            case TEXT:
                propertyJson.put("analyzer", "ik_max_word");
                break;
            case DATE:
                propertyJson.put("format", "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis");
                break;
            case KEYWORD:
                propertyJson.put("ignore_above", 1024);
                break;
            default:
                break;
        }
        return propertyJson;
    }
}
