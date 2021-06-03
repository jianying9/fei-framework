package com.fei.module;

import com.fei.framework.context.AppContext;
import com.fei.framework.module.Module;
import com.fei.framework.module.ModuleContext;
import com.fei.framework.bean.BeanContext;
import com.fei.framework.util.ToolUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

/**
 *
 * @author jianying9
 */
@Module
public class EsContext implements ModuleContext
{

    public final static EsContext CONTEXT = new EsContext();

    private final String name = "esEntityDao";

    private final Logger logger = LogManager.getLogger(EsContext.class);

    private String host;

    private int port = 9200;

    private String database;

    private RestClient restClient;

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void init(Set<Class<?>> classSet)
    {
        //初始化es配置
        host = AppContext.CONTEXT.getParameter(EsConfig.HOST);
        String portStr = AppContext.CONTEXT.getParameter(EsConfig.PORT);
        if (portStr != null) {
            port = Integer.parseInt(portStr);
        }
        //
        database = AppContext.CONTEXT.getParameter(EsConfig.DATABASE);
        if (database == null) {
            database = "";
        }
        //
        String user = AppContext.CONTEXT.getParameter(EsConfig.USER);
        if (user == null) {
            user = "";
        }
        String passowrd = AppContext.CONTEXT.getParameter(EsConfig.PASSWORD);
        if (passowrd == null) {
            passowrd = "";
        }
        String httpCa = AppContext.CONTEXT.getParameter(EsConfig.HTTP_CERTIFICATE);
        if (httpCa == null) {
            httpCa = "";
        }
        //
        if (host != null) {
            try {
                KeyStore trustStore = KeyStore.getInstance("PKCS12");
                String storePassword = "";
                if (httpCa.isEmpty() == false) {
                    File file = new File(httpCa);
                    FileInputStream fileInputStream = new FileInputStream(file);
                    trustStore.load(fileInputStream, storePassword.toCharArray());
                }
                //
                SSLContextBuilder sslBuilder = SSLContexts.custom()
                        .loadTrustMaterial(trustStore, null);
                final SSLContext sslContext = sslBuilder.build();
                //
                final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, passowrd));
                //
                restClient = RestClient.builder(
                        new HttpHost(host, port, "https")).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback()
                        {
                            @Override
                            public HttpAsyncClientBuilder customizeHttpClient(
                                    HttpAsyncClientBuilder httpClientBuilder)
                            {
                                return httpClientBuilder.setSSLContext(sslContext).setSSLHostnameVerifier(new HostnameVerifier()
                                {
                                    @Override
                                    public boolean verify(String string, SSLSession ssls)
                                    {
                                        //不校验ssl证书是否和hostName一致
                                        return true;
                                    }
                                }).setDefaultCredentialsProvider(credentialsProvider);
                            }
                        }).build();
            } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | KeyManagementException ex) {
                this.logger.error("elasticsearch添加节点异常");
            }
        }
        //解析
        Set<Class<?>> esEntityClassSet = new HashSet();
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(EsEntity.class)) {
                this.logger.info("find EsEntity class:{}.", clazz.getName());
                this.createDao(clazz);
                esEntityClassSet.add(clazz);
            }
        }
        classSet.removeAll(esEntityClassSet);
    }

    private void createDao(Class<?> clazz)
    {
        //根据类型获取索引名称
        String type = ToolUtils.getTableName(clazz);
        String index;
        if (this.database.isEmpty()) {
            index = type;
        } else {
            index = this.database + "_" + type;
        }
        //获取该实体所有字段集合
        //ColumnHandler
        EsColumnHandler keyHandler = null;
        //column
        List<EsColumnHandler> columnHandlerList = new ArrayList(0);
        Field[] fieldArray = clazz.getDeclaredFields();
        String fieldName;
        EsColumn esColumn;
        EsColumnType columnType;
        for (Field field : fieldArray) {
            if (Modifier.isStatic(field.getModifiers()) == false) {
                //非静态字段
                fieldName = field.getName();
                if (field.isAnnotationPresent(EsColumn.class)) {
                    //
                    esColumn = field.getAnnotation(EsColumn.class);
                    columnType = this.getColumnType(clazz, field, esColumn);
                    if (esColumn.key()) {
                        if (keyHandler == null) {
                            keyHandler = new EsColumnHandler(fieldName, columnType, esColumn.defaultValue());
                        } else {
                            this.logger.error("{} EsColumn multy keys", clazz.getName());
                            throw new RuntimeException("EsColumn multy keys");
                        }
                    } else {
                        EsColumnHandler columnHandler = new EsColumnHandler(fieldName, columnType, esColumn.defaultValue());
                        columnHandlerList.add(columnHandler);
                    }
                }
            }
        }
        if (keyHandler == null) {
            this.logger.error("{} EsColumn miss key", clazz.getName());
            throw new RuntimeException("EsColumn miss key");
        } else {
            //实例化dao
            EsEntityDao esEntityDao = new EsEntityDaoImpl(index, type, keyHandler, columnHandlerList, clazz);
            //注册到bean
            BeanContext beanContext = AppContext.CONTEXT.getBeanContext();
            beanContext.add(this.name, clazz.getName(), esEntityDao);
        }
    }

    private EsColumnType getColumnType(Class<?> clazz, Field field, EsColumn esColumn)
    {
        EsColumnType result = null;
        Class<?> type = field.getType();
        if (type == boolean.class || type == Boolean.class) {
            result = EsColumnType.BOOLEAN;
        } else if (type == long.class || int.class == type || type == Integer.class || type == Long.class) {
            result = EsColumnType.LONG;
        } else if (type == double.class || type == Double.class) {
            result = EsColumnType.DOUBLE;
        } else if (type == String.class) {
            if (esColumn.analyzer()) {
                result = EsColumnType.TEXT;
            } else {
                result = EsColumnType.KEYWORD;
            }
        } else if (type.isArray()) {
            this.logger.error("{},{} EsColumn unsupport Array", clazz.getName(), field.getName());
            throw new RuntimeException("EsColumn unsupport Array");
        } else if (Collection.class.isAssignableFrom(type)) {
            this.logger.error("{},{} EsColumn unsupport Collection", clazz.getName(), field.getName());
            throw new RuntimeException("EsColumn unsupport Collection");
        } else {
            this.logger.error("{},{} EsColumn unsupport Object", clazz.getName(), field.getName());
            throw new RuntimeException("EsColumn unsupport Object");
        }
        return result;
    }

    @Override
    public void build()
    {
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public String getDatabase()
    {
        return database;
    }

    public RestClient getRestClient()
    {
        return restClient;
    }

}
