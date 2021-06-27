package com.fei.module;

import com.fei.framework.context.AppContext;
import com.fei.framework.module.Module;
import com.fei.framework.module.ModuleContext;
import com.fei.framework.bean.BeanContext;
import com.fei.framework.utils.ToolUtil;
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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    public final static EsContext INSTANCE = new EsContext();

    private final String name = "esEntityDao";

    private final Logger logger = LogManager.getLogger(EsContext.class);

    private String database;

    private String url;

    private String pathPrefix = "/";

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
        url = AppContext.INSTANCE.getParameter(EsConfig.URL);
        //
        database = AppContext.INSTANCE.getParameter(EsConfig.DATABASE);
        if (database == null) {
            database = "";
        }
        //
        final String user = AppContext.INSTANCE.getParameter(EsConfig.USER);
        final String password = AppContext.INSTANCE.getParameter(EsConfig.PASSWORD);
        //
        String httpCa = AppContext.INSTANCE.getParameter(EsConfig.HTTP_CERTIFICATE);
        String httpCaPassword = AppContext.INSTANCE.getParameter(EsConfig.HTTP_CERTIFICATE_PASSWORD);
        if (httpCaPassword == null) {
            httpCaPassword = "";
        }
        //
        if (url != null) {
            try {
                //如果有自定义ssl证书,则初始化
                final SSLContext sslContext;
                if (httpCa != null && httpCa.isEmpty() == false) {
                    KeyStore trustStore = KeyStore.getInstance("PKCS12");
                    File file = new File(httpCa);
                    FileInputStream fileInputStream = new FileInputStream(file);
                    trustStore.load(fileInputStream, httpCaPassword.toCharArray());
                    SSLContextBuilder sslBuilder = SSLContexts.custom()
                            .loadTrustMaterial(trustStore, null);
                    sslContext = sslBuilder.build();
                } else {
                    sslContext = null;
                }
                //
                final HttpHost httpHost = this.createHostAndPathPrefix(url);
                RestClientBuilder restClientBuilder = RestClient.builder(httpHost)
                        .setPathPrefix(pathPrefix)
                        .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback()
                        {
                            @Override
                            public HttpAsyncClientBuilder customizeHttpClient(
                                    HttpAsyncClientBuilder httpClientBuilder)
                            {
                                //处理账号密码
                                if (user.isEmpty() == false && password.isEmpty() == false) {
                                    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                                    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
                                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                                }
                                //处理https
                                if (httpHost.getSchemeName().equals("https")) {
                                    if (sslContext != null) {
                                        httpClientBuilder.setSSLContext(sslContext);
                                    }
                                    httpClientBuilder.setSSLHostnameVerifier(new HostnameVerifier()
                                    {
                                        @Override
                                        public boolean verify(String string, SSLSession ssls)
                                        {
                                            //不校验ssl证书是否和hostName一致
                                            return true;
                                        }
                                    });
                                }
                                return httpClientBuilder;
                            }
                        });
                this.restClient = restClientBuilder.build();
            } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | KeyManagementException ex) {
                this.logger.error("elasticsearch rest client init error", ex);
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

    /**
     * 根据url自动生成host和pathPrefix
     *
     * @param s
     * @return
     */
    private HttpHost createHostAndPathPrefix(final String s)
    {
        String text = s;
        //http or https
        String scheme = null;
        final int schemeIdx = text.indexOf("://");
        if (schemeIdx > 0) {
            scheme = text.substring(0, schemeIdx);
            text = text.substring(schemeIdx + 3);
        }
        //pathPrefix
        final int pathIdx = text.indexOf("/");
        if (pathIdx > 0) {
            this.pathPrefix = text.substring(pathIdx);
            text = text.substring(0, pathIdx);
        }
        //port
        int port = -1;
        final int portIdx = text.lastIndexOf(":");
        if (portIdx > 0) {
            try {
                port = Integer.parseInt(text.substring(portIdx + 1));
            } catch (final NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid HTTP host: " + text);
            }
            text = text.substring(0, portIdx);
        }
        return new HttpHost(text, port, scheme);
    }

    private void createDao(Class<?> clazz)
    {
        String dbName;
        EsEntity esEntity = clazz.getAnnotation(EsEntity.class);
        if (esEntity.database().isEmpty()) {
            dbName = this.database;
        } else {
            dbName = esEntity.database();
        }
        //根据类型获取索引名称
        String index;
        if (esEntity.index().isEmpty()) {
            index = ToolUtil.getTableName(clazz);
        } else {
            index = esEntity.index();
        }
        if (dbName.isEmpty() == false) {
            index = dbName + "_" + index;
        }
        //获取该实体所有字段集合
        EsKeyHandler keyHandler = null;
        List<EsColumnHandler> columnHandlerList = new ArrayList(0);
        Field[] fieldArray = clazz.getDeclaredFields();
        String fieldName;
        EsColumn esColumn;
        EsKey esKey;
        EsColumnType columnType;
        for (Field field : fieldArray) {
            if (Modifier.isStatic(field.getModifiers()) == false) {
                //非静态字段
                fieldName = field.getName();

                if (field.isAnnotationPresent(EsColumn.class)) {
                    //
                    esColumn = field.getAnnotation(EsColumn.class);
                    columnType = this.getColumnType(clazz, field, esColumn);
                    EsColumnHandler columnHandler = new EsColumnHandler(fieldName, columnType, esColumn.defaultValue());
                    columnHandlerList.add(columnHandler);
                } else if (field.isAnnotationPresent(EsKey.class)) {
                    if (keyHandler == null) {
                        esKey = field.getAnnotation(EsKey.class);
                        if (field.getType().equals(String.class)) {
                            keyHandler = new EsKeyHandler(fieldName, esKey.auto(), field);
                        } else {
                            this.logger.error("{} EsColumn key must String.class", clazz.getName());
                            throw new RuntimeException("EsColumn key must String.class");
                        }
                    } else {
                        this.logger.error("{} EsColumn multy keys", clazz.getName());
                        throw new RuntimeException("EsColumn multy keys");
                    }
                }
            }
        }
        if (keyHandler == null) {
            this.logger.error("{} EsColumn miss key", clazz.getName());
            throw new RuntimeException("EsColumn miss key");
        } else {
            //实例化dao
            EsEntityDao esEntityDao = new EsEntityDaoImpl(index, keyHandler, columnHandlerList, clazz);
            //注册到bean
            BeanContext beanContext = AppContext.INSTANCE.getBeanContext();
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
        } else if (type == Date.class) {
            result = EsColumnType.DATE;
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
        this.updateMapping();
    }

    public String getDatabase()
    {
        return database;
    }

    public RestClient getRestClient()
    {
        return restClient;
    }

    public void updateMapping()
    {
        BeanContext beanContext = AppContext.INSTANCE.getBeanContext();
        Map<String, Object> entityDaoMap = beanContext.get(this.name);
        for (Object value : entityDaoMap.values()) {
            ((EsEntityDaoImpl) value).updateMapping();
        }
    }

}
