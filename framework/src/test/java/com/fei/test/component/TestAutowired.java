package com.fei.test.component;

import com.fei.framework.bean.Autowired;
import com.fei.module.Component;

/**
 *
 * @author jianying9
 */
@Component
public class TestAutowired
{

    @Autowired
    private TestComponent testComponent;

    public void info()
    {
        System.out.println(this.testComponent.getMsg());
    }

}
