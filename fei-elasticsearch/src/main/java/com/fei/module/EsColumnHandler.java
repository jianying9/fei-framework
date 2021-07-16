package com.fei.module;

import com.alibaba.fastjson.JSONObject;
import static com.fei.module.EsContext.TIMESTAMP_FIELD_NAME;

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
        if(this.fieldName.equals(TIMESTAMP_FIELD_NAME) == false) {
            Object nullValue = this.getNullValue();
            propertyJson.put("null_value", nullValue);
        }
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
