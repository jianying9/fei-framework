package com.fei.devops.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fei.annotations.component.Component;
import com.fei.web.router.BizException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 *
 * @author jianying9
 */
@Component
public class GitlabComponent
{

    private final String host = "https://a.zlw333.com/gitlab";

    private final String apiPath = "https://a.zlw333.com/gitlab/api/v4";

    private final String appId = "b62eca585678d5f089e034dea04c8c2cc8d50c02f7bd627c67ea35c1252bb4ed";

    private final String appSecret = "b450f7a1920961eb0cdac64ff41a88e2cb2d2487aa9607bf01c299e51ba40fbd";

    private CloseableHttpClient client;

    public void init()
    {
        HttpClientBuilder builder = HttpClientBuilder.create();
        this.client = builder.build();
    }

    private String getJavaKey(String key)
    {
        //git参数名称转为java标准参数名client_id => clientId
        StringBuilder sb = new StringBuilder();
        char[] charArray = key.toCharArray();
        boolean nextToUp = false;
        for (char c : charArray) {
            if (c == '_') {
                if (sb.length() > 0) {
                    nextToUp = true;
                }
            } else {
                if (nextToUp && c >= 'a' && c <= 'z') {
                    c = (char) (c - 32);
                    nextToUp = false;
                }
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private <T> T parseObject(String text, Class<T> clazz)
    {
        JSONObject obj = JSON.parseObject(text);
        JSONObject newObj = new JSONObject();
        String javaKey;
        for (String key : obj.keySet()) {
            javaKey = this.getJavaKey(key);
            newObj.put(javaKey, obj.get(key));
        }
        //
        T t = newObj.toJavaObject(clazz);
        return t;
    }

    private <T> List<T> parseArray(String text, Class<T> clazz)
    {
        JSONArray array = JSON.parseArray(text);
        List<T> tList = new ArrayList();
        T t;
        String javaKey;
        JSONObject obj;
        JSONObject newObj;
        for (int i = 0; i < array.size(); i++) {
            obj = array.getJSONObject(i);
            newObj = new JSONObject();
            for (String key : obj.keySet()) {
                javaKey = this.getJavaKey(key);
                newObj.put(javaKey, obj.get(key));
            }
            t = newObj.toJavaObject(clazz);
            tList.add(t);
        }
        return tList;
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
            default:
                errorMsg = "未知错误";
        }
        return errorMsg;
    }

    private HttpPost createHttpPost(String url)
    {
        return this.createHttpPost(url, null);
    }

    private HttpPost createHttpPost(String url, GitlabToken gitlabToken)
    {
        HttpPost request = new HttpPost(url);
        request.setHeader("Content-Type", "application/json;charset=UTF-8");
        if (gitlabToken != null) {
            request.setHeader("Authorization", gitlabToken.tokenType + " " + gitlabToken.accessToken);
        }
        return request;
    }
    
    private HttpPut createHttpPut(String url, GitlabToken gitlabToken)
    {
        HttpPut request = new HttpPut(url);
        request.setHeader("Content-Type", "application/json;charset=UTF-8");
        if (gitlabToken != null) {
            request.setHeader("Authorization", gitlabToken.tokenType + " " + gitlabToken.accessToken);
        }
        return request;
    }

    private HttpGet createHttpGet(String url, GitlabToken gitlabToken)
    {
        HttpGet request = new HttpGet(url);
        if (gitlabToken != null) {
            request.setHeader("Authorization", gitlabToken.tokenType + " " + gitlabToken.accessToken);
        }
        return request;
    }
    
    private HttpDelete createHttpDelete(String url, GitlabToken gitlabToken)
    {
        HttpDelete request = new HttpDelete(url);
        if (gitlabToken != null) {
            request.setHeader("Authorization", gitlabToken.tokenType + " " + gitlabToken.accessToken);
        }
        return request;
    }

    public static class GitlabToken
    {

        public String accessToken;
        public String tokenType;
        public long expires;
        public String refreshToken;
        public long createdAt;
    }

    /**
     * 获取token
     * @param code
     * @param redirectUri
     * @return
     * @throws IOException
     * @throws BizException 
     */
    public GitlabToken getToken(String code, String redirectUri) throws IOException, BizException
    {
        String url = this.host + "/oauth/token";
        HttpPost request = this.createHttpPost(url);
        Map<String, Object> dataMap = new HashMap();
        dataMap.put("client_id", this.appId);
        dataMap.put("client_secret", this.appSecret);
        dataMap.put("code", code);
        dataMap.put("grant_type", "authorization_code");
        dataMap.put("redirect_uri", redirectUri);
        String dataJson = JSON.toJSONString(dataMap);
        HttpEntity httpEntity = new StringEntity(dataJson, "utf-8");
        request.setEntity(httpEntity);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("git_oauth_token_error", this.getErrorMsg(ex));
        }
        return this.parseObject(responseBody, GitlabToken.class);
    }

    public static class GitlabUser
    {

        public String id;
        public String username;
        public String email;
        public String name;
        public String state;
        public String avatarUrl;
        public boolean isAdmin;
    }

    /**
     * 用户查询
     * @param gitlabToken
     * @return
     * @throws IOException
     * @throws BizException 
     */
    public List<GitlabUser> searchUser(GitlabToken gitlabToken) throws IOException, BizException
    {
        String url = this.apiPath + "/users";
        HttpGet request = this.createHttpGet(url, gitlabToken);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_search_user_error", this.getErrorMsg(ex));
        }
        return this.parseArray(responseBody, GitlabUser.class);
    }

    /**
     * 当前登录用户信息
     * @param gitlabToken
     * @return
     * @throws IOException
     * @throws BizException 
     */
    public GitlabUser getCurrentUser(GitlabToken gitlabToken) throws IOException, BizException
    {
        String url = this.apiPath + "/user";
        HttpGet request = this.createHttpGet(url, gitlabToken);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_get_user_error", this.getErrorMsg(ex));
        }
        return this.parseObject(responseBody, GitlabUser.class);
    }

    /**
     * 获取目标用户信息
     * @param gitlabToken
     * @param id
     * @return
     * @throws IOException
     * @throws BizException 
     */
    public GitlabUser getUser(GitlabToken gitlabToken, String id) throws IOException, BizException
    {
        String url = this.apiPath + "/users/" + id;
        HttpGet request = this.createHttpGet(url, gitlabToken);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_get_user_error", this.getErrorMsg(ex));
        }
        return this.parseObject(responseBody, GitlabUser.class);
    }

    /**
     * 新增用户
     * @param gitlabToken
     * @param email
     * @param name
     * @param username
     * @param password
     * @return
     * @throws IOException
     * @throws BizException 
     */
    public GitlabUser addUser(GitlabToken gitlabToken, String email, String name, String username, String password) throws IOException, BizException
    {
        String url = this.apiPath + "/users";
        HttpPost request = this.createHttpPost(url, gitlabToken);
        Map<String, Object> dataMap = new HashMap();
        dataMap.put("email", email);
        dataMap.put("name", name);
        dataMap.put("username", username);
        dataMap.put("password", password);
        dataMap.put("skip_confirmation", true);
        String dataJson = JSON.toJSONString(dataMap);
        HttpEntity httpEntity = new StringEntity(dataJson, "utf-8");
        request.setEntity(httpEntity);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_add_user_error", this.getErrorMsg(ex));
        }
        return this.parseObject(responseBody, GitlabUser.class);
    }
    
    public static class GitlabGroup
    {

        public String id;
        public String name;
        public String path;
        public String visibility;
        public String avatarUrl;
        public String description;
        
    }
    
    /**
     * 群组查询
     * @param gitlabToken
     * @return
     * @throws IOException
     * @throws BizException 
     */
    public List<GitlabGroup> searchGroup(GitlabToken gitlabToken) throws IOException, BizException
    {
        String url = this.apiPath + "/groups";
        HttpGet request = this.createHttpGet(url, gitlabToken);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_search_group_error", this.getErrorMsg(ex));
        }
        return this.parseArray(responseBody, GitlabGroup.class);
    }
    
    /**
     * 群组详细信息查询
     * @param gitlabToken
     * @param id
     * @return
     * @throws IOException
     * @throws BizException 
     */
    public GitlabGroup getGroup(GitlabToken gitlabToken, String id) throws IOException, BizException
    {
        String url = this.apiPath + "/groups/" + id;
        HttpGet request = this.createHttpGet(url, gitlabToken);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_get_group_error", this.getErrorMsg(ex));
        }
        return this.parseObject(responseBody, GitlabGroup.class);
    }
    
    /**
     * 新增群组
     * @param gitlabToken
     * @param name
     * @param description
     * @return
     * @throws IOException
     * @throws BizException 
     */
    public GitlabGroup addGroup(GitlabToken gitlabToken, String name, String description) throws IOException, BizException
    {
        String url = this.apiPath + "/groups";
        HttpPost request = this.createHttpPost(url, gitlabToken);
        Map<String, Object> dataMap = new HashMap();
        dataMap.put("name", name);
        dataMap.put("path", name);
        dataMap.put("description", description);
        dataMap.put("visibility", "public");
        dataMap.put("project_creation_level", "developer");
        String dataJson = JSON.toJSONString(dataMap);
        HttpEntity httpEntity = new StringEntity(dataJson, "utf-8");
        request.setEntity(httpEntity);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_add_group_error", this.getErrorMsg(ex));
        }
        return this.parseObject(responseBody, GitlabGroup.class);
    }
    
    public static class GitlabMember
    {

        public String id;
        public String username;
        public String name;
        public String state;
        public String avatarUrl;
        public int accessLevel;
    }
    
    /**
     * 查询群组成员
     * @param gitlabToken
     * @param id
     * @return
     * @throws IOException
     * @throws BizException 
     */
    public List<GitlabMember> searchGroupMember(GitlabToken gitlabToken, String id) throws IOException, BizException
    {
        String url = this.apiPath + "/groups/" + id + "/members";
        HttpGet request = this.createHttpGet(url, gitlabToken);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);
            
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_search_group_member_error", this.getErrorMsg(ex));
        }
        return this.parseArray(responseBody, GitlabMember.class);
    }
    
    /**
     * 新增群组用户
     * @param gitlabToken
     * @param id
     * @param userId
     * @param accessLevel
     * @return
     * @throws IOException
     * @throws BizException 
     */
    public GitlabMember addGroupMember(GitlabToken gitlabToken, String id, String userId, int accessLevel) throws IOException, BizException
    {
        String url = this.apiPath + "/groups/" + id + "/members";
        HttpPost request = this.createHttpPost(url, gitlabToken);
        Map<String, Object> dataMap = new HashMap();
        dataMap.put("id", id);
        dataMap.put("user_id", userId);
        dataMap.put("access_level", accessLevel);
        String dataJson = JSON.toJSONString(dataMap);
        HttpEntity httpEntity = new StringEntity(dataJson, "utf-8");
        request.setEntity(httpEntity);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_add_group_member_error", this.getErrorMsg(ex));
        }
        return this.parseObject(responseBody, GitlabMember.class);
    }
    
    /**
     * 更新群组成员信息
     * @param gitlabToken
     * @param id
     * @param userId
     * @param accessLevel
     * @return
     * @throws IOException
     * @throws BizException 
     */
    public GitlabMember updateGroupMember(GitlabToken gitlabToken, String id, String userId, int accessLevel) throws IOException, BizException
    {
        String url = this.apiPath + "/groups/" + id + "/members/" + userId;
        HttpPut request = this.createHttpPut(url, gitlabToken);
        Map<String, Object> dataMap = new HashMap();
        dataMap.put("id", id);
        dataMap.put("user_id", userId);
        dataMap.put("access_level", accessLevel);
        String dataJson = JSON.toJSONString(dataMap);
        HttpEntity httpEntity = new StringEntity(dataJson, "utf-8");
        request.setEntity(httpEntity);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_update_group_member_error", this.getErrorMsg(ex));
        }
        return this.parseObject(responseBody, GitlabMember.class);
    }
    
    /**
     * 删除群组用户
     * @param gitlabToken
     * @param id
     * @param userId
     * @return
     * @throws IOException
     * @throws BizException 
     */
    public GitlabMember deleteGroupMember(GitlabToken gitlabToken, String id, String userId) throws IOException, BizException
    {
        String url = this.apiPath + "/groups/" + id + "/members/" + userId;
        HttpDelete request = this.createHttpDelete(url, gitlabToken);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_add_group_member_error", this.getErrorMsg(ex));
        }
        return this.parseObject(responseBody, GitlabMember.class);
    }
    

}
