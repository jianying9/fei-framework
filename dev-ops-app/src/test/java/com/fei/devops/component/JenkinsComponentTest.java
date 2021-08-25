package com.fei.devops.component;

import com.fei.annotations.component.Resource;
import com.fei.app.test.ResourceMock;
import com.fei.devops.AppMain;
import com.fei.web.router.BizException;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jianying9
 */
public class JenkinsComponentTest
{

    public JenkinsComponentTest()
    {
    }

    @Resource
    private JenkinsComponent jenkinsComponent;

    private static ResourceMock resourceMock;

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
    }

    @After
    public void tearDown()
    {
    }

//    @Test
    public void getJob() throws IOException, BizException
    {
        String result = this.jenkinsComponent.getJob("server-zlw-center-dev");
        System.out.println(result);
    }

//    @Test
    public void createView() throws IOException, BizException
    {
        this.jenkinsComponent.createView("test3");
    }

//    @Test
    public void createJob() throws IOException, BizException
    {
        String configXml = "<?xml version='1.1' encoding='UTF-8'?>\n"
                + "<project>\n"
                + "  <description>auto create</description>\n"
                + "  <keepDependencies>false</keepDependencies>\n"
                + "  <properties>\n"
                + "    <com.dabsquared.gitlabjenkins.connection.GitLabConnectionProperty plugin=\"gitlab-plugin@1.5.20\">\n"
                + "      <gitLabConnection>devops_jenkins</gitLabConnection>\n"
                + "      <jobCredentialId></jobCredentialId>\n"
                + "      <useAlternativeCredential>false</useAlternativeCredential>\n"
                + "    </com.dabsquared.gitlabjenkins.connection.GitLabConnectionProperty>\n"
                + "  </properties>\n"
                + "  <scm class=\"hudson.scm.NullSCM\"/>\n"
                + "  <canRoam>true</canRoam>\n"
                + "  <disabled>false</disabled>\n"
                + "  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>\n"
                + "  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>\n"
                + "  <triggers/>\n"
                + "  <concurrentBuild>false</concurrentBuild>\n"
                + "  <builders/>\n"
                + "  <publishers/>\n"
                + "  <buildWrappers/>\n"
                + "</project>";
        this.jenkinsComponent.createViewJob("test3", "server-zlw-center-product5", configXml);
    }

//    @Test
    public void updateConfig() throws IOException, BizException
    {

        String formData = this.getResource("/config.txt");
        this.jenkinsComponent.updateConfig(formData);
    }

    private String getResource(String name) throws IOException
    {
        InputStream inputStream = this.getClass().getResourceAsStream(name);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }

//    @Test
    public void submitFile() throws IOException
    {
        String text = this.getResource("/configSubmit.txt");
        //
        text = URLDecoder.decode(text, "UTF-8");
        //
        String fileName = this.getClass().getResource("").getPath() + "config.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(text);
        }
    }

}
