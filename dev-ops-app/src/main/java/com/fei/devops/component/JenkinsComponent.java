package com.fei.devops.component;

import com.alibaba.fastjson.JSONObject;
import com.fei.annotations.component.Component;
import com.fei.web.router.BizException;
import java.io.IOException;
import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author jianying9
 */
@Component
public class JenkinsComponent
{

    private final String host = "https://a.zlw333.com/jenkins";

    private final String username = "admin";

    private final String password = "1183d06391742b1a6f13cafc2c9cf914f1";

    public final String secretToken = "446495fa7a646f22849b83cb0be27ba5";
    //hudson.util.Secret.fromString(secretToken).getEncryptedValue();
    //hudson.util.Secret.fromString(secretTokenEncrypted).getPlainText();
    public final String secretTokenEncrypted = "{AQAAABAAAAAwzi+91e1VIlkYE5NJdk7rsqiEFyLWF730ndKRWaVjfjITdcVVZlExaxmzzu1xj1uI7KExdQX55yfXLLjaAv1eVg==}";

    private CloseableHttpClient client;

    private final HttpClientContext localContext = HttpClientContext.create();

    public void init()
    {
        URI uri = URI.create(host);
        HttpHost httpHost = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()),
                new UsernamePasswordCredentials(username, password));
        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local auth cache
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(httpHost, basicAuth);
        // Add AuthCache to the execution context
        localContext.setAuthCache(authCache);
        //
        this.client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
    }

    private String getErrorMsg(HttpResponseException ex)
    {
        String errorMsg;
        switch (ex.getStatusCode()) {
            case 400:
                errorMsg = "缺少必填参数";
                break;
            case 401:
                errorMsg = "缺少auth";
                break;
            case 403:
                errorMsg = "当前用户没有该权限";
                break;
            case 409:
                errorMsg = "新增信息已经存在";
                break;
            case 404:
                errorMsg = "目标不存在";
                break;
            default:
                errorMsg = "未知错误";
        }
        return errorMsg;
    }

    public String getJob(String jobName) throws IOException, BizException
    {
        String uri = this.host + "/job/" + jobName + "/api/json";
        HttpGet request = new HttpGet(uri);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler, this.localContext);
        } catch (HttpResponseException ex) {
            throw new BizException("jenkins_get_job_error", this.getErrorMsg(ex));
        }
        return responseBody;
    }

    public String getViewJob(String viewName, String jobName) throws IOException, BizException
    {
        String uri = this.host + "/view/" + viewName + "/job/" + jobName + "/api/json";
        HttpGet request = new HttpGet(uri);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler, this.localContext);
        } catch (HttpResponseException ex) {
            throw new BizException("jenkins_get_view_job_error", this.getErrorMsg(ex));
        }
        return responseBody;
    }

    public String getViewJobList(String viewName) throws IOException, BizException
    {
        String uri = this.host + "/view/" + viewName + "/api/json";
        HttpGet request = new HttpGet(uri);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler, this.localContext);
        } catch (HttpResponseException ex) {
            throw new BizException("jenkins_get_view_job_list_error", this.getErrorMsg(ex));
        }
        return responseBody;
    }

    public String createJob(String jobName, String configXml) throws IOException, BizException
    {
        String uri = this.host + "/createItem?name=" + jobName;
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-Type", "application/xml;charset=utf-8");
        HttpEntity httpEntity = new StringEntity(configXml, "utf-8");
        request.setEntity(httpEntity);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler, this.localContext);
        } catch (HttpResponseException ex) {
            throw new BizException("jenkins_create_job_error", this.getErrorMsg(ex));
        }
        return responseBody;
    }

    public String createViewJob(String viewName, String jobName, String configXml) throws IOException, BizException
    {
        String uri = this.host + "/view/" + viewName + "/createItem?name=" + jobName;
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-Type", "application/xml;charset=utf-8");
        HttpEntity httpEntity = new StringEntity(configXml, "utf-8");
        request.setEntity(httpEntity);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try {
            this.client.execute(request, responseHandler, this.localContext);
        } catch (HttpResponseException ex) {
            throw new BizException("jenkins_create_job_error", this.getErrorMsg(ex));
        }
        return this.host + "/project/" + jobName;
    }

    public void createView(String viewName) throws IOException, BizException
    {
        String uri = this.host + "/createView";
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        JSONObject json = new JSONObject();
        json.put("name", viewName);
        json.put("mode", "hudson.model.ListView");
        String data = "json=" + json.toJSONString() + "&name=" + viewName + "&mode=hudson.model.ListView";
        HttpEntity httpEntity = new StringEntity(data, "utf-8");
        request.setEntity(httpEntity);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try {
            this.client.execute(request, responseHandler, this.localContext);
        } catch (HttpResponseException ex) {
            if (ex.getStatusCode() != 302 && ex.getStatusCode() != 400) {
                throw new BizException("jenkins_create_view_error", this.getErrorMsg(ex));
            }
        }
    }

    public void updateConfig(String formData) throws IOException, BizException
    {
        String uri = this.host + "/configSubmit";
        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity httpEntity = new StringEntity(formData, "utf-8");
        request.setEntity(httpEntity);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try {
            this.client.execute(request, responseHandler, this.localContext);
        } catch (HttpResponseException ex) {
            throw new BizException("jenkins_updateConfig_error", this.getErrorMsg(ex));
        }
        
    }

}
