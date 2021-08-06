package com.fei.devops.component;

import com.fei.annotations.component.Resource;
import com.fei.app.test.ResourceMock;
import com.fei.app.utils.ToolUtil;
import com.fei.devops.AppMain;
import com.fei.devops.component.GitlabComponent.GitlabToken;
import com.fei.devops.component.GitlabComponent.GitlabUser;
import com.fei.web.router.BizException;
import java.io.IOException;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author jianying9
 */
public class GitlabAdminComponentTest
{

    public GitlabAdminComponentTest()
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
        this.gitlabToken.accessToken = "";
    }

    @After
    public void tearDown()
    {
    }

//    @Test
    public void searchUser() throws IOException
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
    public void getUser() throws IOException
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
}
