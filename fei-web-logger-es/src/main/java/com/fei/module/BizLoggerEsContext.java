package com.fei.module;

import com.fei.annotations.module.Module;
import com.fei.app.module.ModuleContext;
import com.fei.web.logger.BizLoggerEsImpl;
import com.fei.web.logger.BizLoggerFactory;
import java.util.Set;

/**
 *
 * @author jianying9
 */
@Module
public class BizLoggerEsContext implements ModuleContext
{

    private final String name = "biz-logger-es";

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void init(Set<Class<?>> classSet)
    {
    }

    @Override
    public void build()
    {
        Object obj = EsContext.INSTANCE.get(BizLogStream.class);
        if (obj != null) {
            EsStreamDao<BizLogStream> bizLogStreamDao = (EsStreamDao<BizLogStream>) obj;
            BizLoggerEsImpl bizLoggerEsImpl = new BizLoggerEsImpl(bizLogStreamDao);
            BizLoggerFactory.setBizLogger(bizLoggerEsImpl);
        }
    }

}
