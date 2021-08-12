package com.apache.http.client.methods;

import java.net.URI;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

/**
 *
 * @author jianying9
 */
public class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase
{

    public final static String METHOD_NAME = "DELETE";

    public HttpDeleteWithBody()
    {
        super();
    }

    public HttpDeleteWithBody(final URI uri)
    {
        super();
        setURI(uri);
    }

    /**
     * @param uri
     * @throws IllegalArgumentException if the uri is invalid.
     */
    public HttpDeleteWithBody(final String uri)
    {
        super();
        setURI(URI.create(uri));
    }

    @Override
    public String getMethod()
    {
        return METHOD_NAME;
    }

}
