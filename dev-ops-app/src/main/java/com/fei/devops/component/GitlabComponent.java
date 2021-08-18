package com.fei.devops.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.apache.http.client.methods.HttpDeleteWithBody;
import com.fei.annotations.component.Component;
import com.fei.devops.Global;
import com.fei.web.router.BizException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
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
        String newKey = sb.toString();
        //保留字
        if (newKey.equals("default")) {
            newKey = "isDefault";
        } else if (newKey.equals("protected")) {
            newKey = "isProtected";
        }
        return newKey;
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
            case 404:
                errorMsg = "目标不存在";
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
        request.setHeader("Content-Type", "application/json;charset=utf-8");
        if (gitlabToken != null) {
            request.setHeader("Authorization", gitlabToken.tokenType + " " + gitlabToken.accessToken);
        }
        return request;
    }

    private HttpPut createHttpPut(String url, GitlabToken gitlabToken)
    {
        HttpPut request = new HttpPut(url);
        request.setHeader("Content-Type", "application/json;charset=utf-8");
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

    private HttpDeleteWithBody createHttpDeleteWithBody(String url, GitlabToken gitlabToken)
    {
        HttpDeleteWithBody request = new HttpDeleteWithBody(url);
        request.setHeader("Content-Type", "application/json;charset=utf-8");
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
     *
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
        JSONObject json = new JSONObject();
        json.put("client_id", this.appId);
        json.put("client_secret", this.appSecret);
        json.put("code", code);
        json.put("grant_type", "authorization_code");
        json.put("redirect_uri", redirectUri);
        String dataJson = json.toJSONString();
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
     *
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
     *
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
     *
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
     *
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
        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("name", name);
        json.put("username", username);
        json.put("password", password);
        json.put("skip_confirmation", true);
        String dataJson = json.toJSONString();
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
     *
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
     *
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
     *
     * @param gitlabToken
     * @param name
     * @param description
     * @return
     * @throws IOException
     * @throws BizException
     */
    public GitlabGroup createGroup(GitlabToken gitlabToken, String name, String description) throws IOException, BizException
    {
        String url = this.apiPath + "/groups";
        HttpPost request = this.createHttpPost(url, gitlabToken);
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("path", name);
        json.put("description", description);
        json.put("visibility", "public");
        json.put("project_creation_level", "developer");
        String dataJson = json.toJSONString();
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
     *
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
     *
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
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("user_id", userId);
        json.put("access_level", accessLevel);
        String dataJson = json.toJSONString();
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
     *
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
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("user_id", userId);
        json.put("access_level", accessLevel);
        String dataJson = json.toJSONString();
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
     *
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

    public static class GitlabProject
    {

        public String id;
        public String name;
        public String path;
        public String visibility;
        public String avatarUrl;
        public String description;
        public String httpUrlToRepo;

    }

    /**
     * 查询群组项目
     *
     * @param gitlabToken
     * @param id
     * @return
     * @throws IOException
     * @throws BizException
     */
    public List<GitlabProject> searchGroupProject(GitlabToken gitlabToken, String id) throws IOException, BizException
    {
        String url = this.apiPath + "/groups/" + id + "/projects";
        HttpGet request = this.createHttpGet(url, gitlabToken);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);

        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_search_group_project_error", this.getErrorMsg(ex));
        }
        return this.parseArray(responseBody, GitlabProject.class);
    }

    /**
     * 为群组新增项目
     *
     * @param gitlabToken
     * @param id
     * @param name
     * @param description
     * @return
     * @throws IOException
     * @throws BizException
     */
    public GitlabProject createGroupProject(GitlabToken gitlabToken, String id, String name, String description) throws IOException, BizException
    {
        String url = this.apiPath + "/projects";
        HttpPost request = this.createHttpPost(url, gitlabToken);
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("path", name);
        json.put("description", description);
        json.put("visibility", "internal");
        json.put("namespace_id", id);
        //初始化main分支
        json.put("initialize_with_readme", true);
        json.put("default_branch", Global.gitlabMainBranchName);
        String dataJson = json.toJSONString();
        HttpEntity httpEntity = new StringEntity(dataJson, "utf-8");
        request.setEntity(httpEntity);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_add_group_project_error", this.getErrorMsg(ex));
        }
        return this.parseObject(responseBody, GitlabProject.class);
    }

    /**
     * 获取项目信息
     *
     * @param gitlabToken
     * @param id
     * @return
     * @throws IOException
     * @throws BizException
     */
    public GitlabProject getProject(GitlabToken gitlabToken, String id) throws IOException, BizException
    {
        String url = this.apiPath + "/projects/" + id;
        HttpGet request = this.createHttpGet(url, gitlabToken);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_get_project_error", this.getErrorMsg(ex));
        }
        return this.parseObject(responseBody, GitlabProject.class);
    }

    public static class GitlabFile
    {

        public String id;
        public String name;
        public String path;
        public String type;
        public String mode;
    }

    /**
     * 获取指定项目文件列表
     *
     * @param gitlabToken
     * @param id
     * @return
     * @throws IOException
     * @throws BizException
     */
    public List<GitlabFile> listRepositoryFile(GitlabToken gitlabToken, String id) throws IOException, BizException
    {
        String url = this.apiPath + "/projects/" + id + "/repository/tree?recursive=true&per_page=100";
        HttpGet request = this.createHttpGet(url, gitlabToken);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_list_repository_file_error", this.getErrorMsg(ex));
        }
        return this.parseArray(responseBody, GitlabFile.class);
    }

    /**
     * 读取文件文本信息
     *
     * @param gitlabToken
     * @param id
     * @param path
     * @return
     * @throws IOException
     * @throws BizException
     */
    public String getRepositoryFile(GitlabToken gitlabToken, String id, String path) throws IOException, BizException
    {
        path = URLEncoder.encode(path, "utf-8");
        String url = this.apiPath + "/projects/" + id + "/repository/files/" + path + "/raw";
        HttpGet request = this.createHttpGet(url, gitlabToken);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_list_repository_file_error", this.getErrorMsg(ex));
        }
        return responseBody;
    }

    /**
     * 新增文件
     *
     * @param gitlabToken
     * @param id
     * @param branch
     * @param path
     * @param content
     * @retur
     * @throws IOException
     * @throws BizException
     */
    public void createRepositoryFile(GitlabToken gitlabToken, String id, String branch, String path, String content) throws IOException, BizException
    {
        path = URLEncoder.encode(path, "utf-8");
        String url = this.apiPath + "/projects/" + id + "/repository/files/" + path;
        HttpPost request = this.createHttpPost(url, gitlabToken);
        JSONObject json = new JSONObject();
        json.put("branch", branch);
        json.put("content", content);
        json.put("commit_message", "devops init");
        String dataJson = json.toJSONString();
        HttpEntity httpEntity = new StringEntity(dataJson, "utf-8");
        request.setEntity(httpEntity);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try {
            this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_create_repository_file_error", this.getErrorMsg(ex));
        }
    }

    /**
     * 删除项目文件
     *
     * @param gitlabToken
     * @param id
     * @param branch
     * @param path
     * @throws IOException
     * @throws BizException
     */
    public void deleteRepositoryFile(GitlabToken gitlabToken, String id, String branch, String path) throws IOException, BizException
    {
        path = URLEncoder.encode(path, "utf-8");
        String url = this.apiPath + "/projects/" + id + "/repository/files/" + path;
        HttpDeleteWithBody request = this.createHttpDeleteWithBody(url, gitlabToken);
        JSONObject json = new JSONObject();
        json.put("branch", branch);
        json.put("commit_message", "devops init");
        String dataJson = json.toJSONString();
        HttpEntity httpEntity = new StringEntity(dataJson, "utf-8");
        request.setEntity(httpEntity);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try {
            this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_delete_repository_file_error", this.getErrorMsg(ex));
        }
    }

    public static class GitlabBranch
    {

        public String name;
        public boolean merged;
        public boolean isDefault;
        public boolean isProtected;
        public boolean canPush;
        public boolean developersCanPush;
        public boolean developersCanMerge;
    }

    /**
     * 获取项目资源的分支列表
     *
     * @param gitlabToken
     * @param id
     * @return
     * @throws IOException
     * @throws BizException
     */
    public List<GitlabBranch> listRepositoryBranch(GitlabToken gitlabToken, String id) throws IOException, BizException
    {
        String url = this.apiPath + "/projects/" + id + "/repository/branches";
        HttpGet request = this.createHttpGet(url, gitlabToken);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_list_repository_branch_error", this.getErrorMsg(ex));
        }
        return this.parseArray(responseBody, GitlabBranch.class);
    }

    /**
     * 创建项目资源分支
     *
     * @param gitlabToken
     * @param id
     * @param branch
     * @param ref
     * @return
     * @throws IOException
     * @throws BizException
     */
    public GitlabBranch createRepositoryBranch(GitlabToken gitlabToken, String id, String branch, String ref) throws IOException, BizException
    {
        String url = this.apiPath + "/projects/" + id + "/repository/branches?ref=" + ref + "&branch=" + branch;
        HttpPost request = this.createHttpPost(url, gitlabToken);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_create_repository_branch_error", this.getErrorMsg(ex));
        }
        return this.parseObject(responseBody, GitlabBranch.class);
    }

    public static class GitlabHook
    {

        public String id;
        public String url;
        public String projectId;
        public boolean pushEvents;
        public String pushEventsBranchFilter;
        public boolean issuesEvents;
        public boolean confidentialIssuesEvents;
        public boolean mergeRequestsEvents;
        public boolean tagPushEvents;
        public boolean noteEvents;
        public boolean confidentialNoteEvents;
        public boolean jobEvents;
        public boolean pipelineEvents;
        public boolean wikiPageEvents;
        public boolean deploymentEvents;
        public boolean releasesEvents;
        public boolean enableSslVerification;
        public String createdAt;

    }

    public List<GitlabHook> listProjectHook(GitlabToken gitlabToken, String id) throws IOException, BizException
    {
        String url = this.apiPath + "/projects/" + id + "/hooks";
        HttpGet request = this.createHttpGet(url, gitlabToken);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_list_project_hook_error", this.getErrorMsg(ex));
        }
        return this.parseArray(responseBody, GitlabHook.class);
    }

    public GitlabHook addProjectHook(GitlabToken gitlabToken, String id, String url, String token, String branch) throws IOException, BizException
    {
        String requestUrl = this.apiPath + "/projects/" + id + "/hooks";
        HttpPost request = this.createHttpPost(requestUrl, gitlabToken);
        JSONObject json = new JSONObject();
        json.put("url", url);
        json.put("token", token);
        json.put("enable_ssl_verification", true);
        json.put("push_events", true);
        if (branch != null && branch.isEmpty() == false) {
            json.put("push_events_branch_filter", branch);
        }
        String dataJson = json.toJSONString();
        HttpEntity httpEntity = new StringEntity(dataJson, "utf-8");
        request.setEntity(httpEntity);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody;
        try {
            responseBody = this.client.execute(request, responseHandler);
        } catch (HttpResponseException ex) {
            throw new BizException("gitlab_add_project_hook_error", this.getErrorMsg(ex));
        }
        return this.parseObject(responseBody, GitlabHook.class);
    }

}
