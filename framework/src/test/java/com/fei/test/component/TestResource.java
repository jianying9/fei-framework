package com.fei.test.component;

import com.fei.module.Component;
import com.fei.framework.bean.Resource;

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
