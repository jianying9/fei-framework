package com.fei.module;

import java.lang.reflect.Field;

/**
 *
 * @author jianying9
 */
public class EsKeyHandler
{

    private final String name;

    private final EsColumnType columnType;

    private final boolean auto;

    private final Field field;

    public EsKeyHandler(String name, boolean auto, Field field)
    {
        this.name = name;
        this.columnType = EsColumnType.KEYWORD;
        this.auto = auto;
        this.field = field;
    }

    public String getName()
    {
        return name;
    }

    public EsColumnType getColumnType()
    {
        return columnType;
    }

    public boolean isAuto()
    {
        return auto;
    }

    public void setValue(Object obj, Object value)
    {
        try {
            this.field.setAccessible(true);
            this.field.set(obj, value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}
