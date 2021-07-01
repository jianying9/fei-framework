package com.fei.web.router.validation;

/**
 * 非空通用类型处理
 *
 * @author jianying9
 */
public class NotNullHandlerImpl implements ValidationHandler
{

    private final String errorMsg;

    private final ValidationHandler typeValidationHandler;

    public NotNullHandlerImpl(ValidationHandler typeValidationHandler)
    {
        this.errorMsg = typeValidationHandler.getName() + " can't be null";
        this.typeValidationHandler = typeValidationHandler;
    }

    @Override
    public final String validate(Object value)
    {
        String result;
        if (value != null) {
            result = this.typeValidationHandler.validate(value);
        } else {
            result = this.errorMsg;
        }
        return result;
    }

    @Override
    public final String getName()
    {
        return this.typeValidationHandler.getName();
    }

    @Override
    public String getKey()
    {
        return this.typeValidationHandler.getKey();
    }

    @Override
    public String getType()
    {
        return this.typeValidationHandler.getType();
    }

}
