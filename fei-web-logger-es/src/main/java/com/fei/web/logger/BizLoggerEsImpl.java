package com.fei.web.logger;

import com.fei.app.context.AppContext;
import com.fei.app.utils.ToolUtil;
import com.fei.module.BizLogStream;
import com.fei.module.EsStreamDao;
import com.fei.web.router.BizContext;

/**
 *
 * @author jianying9
 */
public final class BizLoggerEsImpl implements BizLogger
{

    private final EsStreamDao<BizLogStream> bizLogStreamDao;

    public BizLoggerEsImpl(EsStreamDao<BizLogStream> bizLogStreamDao)
    {
        this.bizLogStreamDao = bizLogStreamDao;
    }

    @Override
    public void log(BizContext bizContext, String requestBody, String responseBody)
    {
        BizLogStream bizLogStream = ToolUtil.copy(bizContext, BizLogStream.class);
        bizLogStream.requestBody = requestBody;
        bizLogStream.responseBody = responseBody;
        bizLogStream.host = AppContext.INSTANCE.getHost();
        bizLogStream.port = AppContext.INSTANCE.getPort();
        bizLogStream.appName = AppContext.INSTANCE.getAppName();
        this.bizLogStreamDao.insert(bizLogStream);
    }

}
