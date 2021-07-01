package com.fei.test.component;

import com.fei.annotations.component.Component;
import com.fei.annotations.component.Resource;

/**
 *
 * @author jianying9
 */
@Component
public class TestResource
{

    @Resource
    private TestComponent testComponent;

    public void info()
    {
        System.out.println(this.testComponent.getMsg());
    }

}
