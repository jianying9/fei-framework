package com.fei.module;

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

}
