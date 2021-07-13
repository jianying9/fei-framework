package com.fei.web.logger;

/**
 *
 * @author jianying9
 */
public final class BizLoggerFactory
{

    private static BizLogger BIZ_LOGGER = new BizLoggerDefaultImpl();

    public static void setBizLogger(BizLogger bizLogger)
    {
        BIZ_LOGGER = bizLogger;
    }

    public static BizLogger getBizLogger()
    {
        return BIZ_LOGGER;
    }
}
