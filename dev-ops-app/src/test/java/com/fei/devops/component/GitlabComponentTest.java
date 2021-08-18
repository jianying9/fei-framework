package com.fei.devops.component;

import com.fei.annotations.component.Resource;
import com.fei.app.test.ResourceMock;
import com.fei.app.utils.ToolUtil;
import com.fei.devops.AppMain;
import com.fei.devops.component.GitlabComponent.GitlabBranch;
import com.fei.devops.component.GitlabComponent.GitlabFile;
import com.fei.devops.component.GitlabComponent.GitlabProject;
import com.fei.devops.component.GitlabComponent.GitlabToken;
import com.fei.devops.component.GitlabComponent.GitlabUser;
import com.fei.web.router.BizException;
import java.io.IOException;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jianying9
 */
public class GitlabComponentTest
{

    public GitlabComponentTest()
    {
    }

    @Resource
    private GitlabComponent gitlabComponent;

    private static ResourceMock resourceMock;

    private final GitlabToken gitlabToken = new GitlabToken();

    @BeforeClass
    public static void setUpClass()
    {
        resourceMock = new ResourceMock(AppMain.class);
    }

    //YsC7XXqmGb46XMjqqBnpxi
    //jianying9
    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
        //注入
        resourceMock.resource(this);
        //
        this.gitlabToken.accessToken = "95ec3d6c019139f8fa62352798221d6b6afc4de23f9eb1b0ff71be2cd16f6ce1";
        this.gitlabToken.refreshToken = "d59275e01c13d58771f16caf8107209ddd761debb4e8e70300f8e9c2ee87ece0";
        this.gitlabToken.tokenType = "Bearer";
    }

    @After
    public void tearDown()
    {
    }

//    @Test
    public void searchUser() throws IOException, BizException
    {
        List<GitlabUser> userList = this.gitlabComponent.searchUser(this.gitlabToken);
        for (GitlabUser user : userList) {
            System.out.println(user.id + "_" + user.name + "_" + user.username);
        }
    }

//    @Test
    public void addUser() throws IOException, BizException
    {
        String pwd = ToolUtil.getAutomicId();
        System.out.println(pwd);
        GitlabUser user = this.gitlabComponent.addUser(this.gitlabToken, "271411342@qq.com", "ljy", "271411342", pwd);
        System.out.println(user.id);
    }

//    @Test
    public void getUser() throws IOException, BizException
    {
        GitlabUser user = this.gitlabComponent.getUser(this.gitlabToken, "8");
        if (user != null) {
            System.out.println(user.username);
        }
    }

//    @Test
    public void getToken() throws IOException, BizException
    {
        String code = "9bb0e17b59af03b753079bc0c35e94c8f10174028ad0f58c654f0a28f1222df5";
        String redirectUri = "http://localhost:8080/login.html";
        GitlabToken token = this.gitlabComponent.getToken(code, redirectUri);
        if (token != null) {
            System.out.println(token.accessToken);
        }
    }

//    @Test
    public void listRepositoryFile() throws IOException, BizException
    {
        String id = "6";
        List<GitlabFile> fileList = this.gitlabComponent.listRepositoryFile(gitlabToken, id);
        for (GitlabFile gitlabFile : fileList) {
            System.out.println(gitlabFile.path);
        }
    }

//    @Test
    public void getRepositoryFile() throws IOException, BizException
    {
        String id = "7";
        String result = this.gitlabComponent.getRepositoryFile(gitlabToken, id, "public/favicon.ico");
        System.out.println(result);
    }

    @Test
    public void copyRepositoryFile() throws IOException, BizException
    {
        String fromId = "6";
        String toId = "19";
        String appName = "java_app_test";
        GitlabProject gitlabProject = this.gitlabComponent.getProject(gitlabToken, fromId);
        //初始化项目分支
        GitlabBranch gitlabBranch = null;
        List<GitlabBranch> branchList = this.gitlabComponent.listRepositoryBranch(gitlabToken, toId);
        for (GitlabBranch branch : branchList) {
            if (branch.name.equals("main")) {
                gitlabBranch = branch;
                break;
            }
        }
        if (gitlabBranch != null) {
            //清空文件
//            List<GitlabFile> toFileList = this.gitlabComponent.listRepositoryFile(gitlabToken, toId);
//            for (GitlabFile gitlabFile : toFileList) {
//                if (gitlabFile.type.equals("blob")) {
//                    this.gitlabComponent.deleteRepositoryFile(gitlabToken, toId, gitlabBranch.name, gitlabFile.path);
//                }
//            }
            //导入文件
            List<GitlabFile> formFileList = this.gitlabComponent.listRepositoryFile(gitlabToken, fromId);
            for (GitlabFile gitlabFile : formFileList) {
                System.out.println(gitlabFile.type + ":" + gitlabFile.path);
                if (gitlabFile.type.equals("blob")) {
                    String content = this.gitlabComponent.getRepositoryFile(gitlabToken, fromId, gitlabFile.path);
                    content = content.replaceAll("${appName}", appName);
                    //
//                    this.gitlabComponent.createRepositoryFile(gitlabToken, toId, gitlabBranch.name, gitlabFile.path, content);
                }
            }
//            this.gitlabComponent.createRepositoryBranch(gitlabToken, toId, "dev", "main");
        }
    }

}
