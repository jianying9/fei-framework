package com.fei.test.component;

import com.fei.annotations.component.Component;

/**
 *
 * @author jianying9
 */
@Component
public class TestComponent
{

    private final String msg = "test component";

    public String getMsg()
    {
        return msg;
    }

    public void init()
    {
        System.out.println("TestComponent init");
    }
}
