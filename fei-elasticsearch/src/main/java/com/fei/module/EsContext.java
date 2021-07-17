package com.fei.module;

import com.fei.annotations.elasticsearch.EsColumn;
import com.fei.annotations.elasticsearch.EsEntity;
import com.fei.annotations.elasticsearch.EsKey;
import com.fei.annotations.elasticsearch.EsStream;
import com.fei.app.context.AppContext;
import com.fei.annotations.module.Module;
import com.fei.app.module.ModuleContext;
import com.fei.app.bean.BeanContext;
import com.fei.app.utils.ToolUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jianying9
 */
@Module
public class EsContext implements ModuleContext
{

    public final static EsContext INSTANCE = new EsContext();

    private final String name = "esDao";

    private final Logger logger = LoggerFactory.getLogger(EsContext.class);

    private String database;

    private String url;

    private String pathPrefix = "/";

    private RestClient restClient;
    
    //时间戳保留字段
    public final static String TIMESTAMP_FIELD_NAME = "timestamp";
    public final static String TIMESTAMP_COLUMN_NAME = "@timestamp";

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
        Set<Class<?>> esClassSet = new HashSet();
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(EsEntity.class)) {
                this.logger.info("find EsEntity class:{}.", clazz.getName());
                this.createEntityDao(clazz);
                esClassSet.add(clazz);
            } else if(clazz.isAnnotationPresent(EsStream.class)) {
                this.logger.info("find EsStream class:{}.", clazz.getName());
                this.createStreamDao(clazz);
                esClassSet.add(clazz);
            }
        }
        classSet.removeAll(esClassSet);
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

    private void createEntityDao(Class<?> clazz)
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
        Object defaultValue;
        for (Field field : fieldArray) {
            if (Modifier.isStatic(field.getModifiers()) == false) {
                //非静态字段
                fieldName = field.getName();
                //timestamp为保留字段,对应es的列@timestamp
                if (fieldName.equals(TIMESTAMP_FIELD_NAME) == false) {
                    if (field.isAnnotationPresent(EsColumn.class)) {
                        //
                        esColumn = field.getAnnotation(EsColumn.class);
                        columnType = this.getColumnType(clazz, field, esColumn);
                        defaultValue = this.getDefaultValue(columnType, esColumn);
                        EsColumnHandler columnHandler = new EsColumnHandler(fieldName, columnType, defaultValue);
                        columnHandlerList.add(columnHandler);
                    } else if (field.isAnnotationPresent(EsKey.class)) {
                        if (keyHandler == null) {
                            esKey = field.getAnnotation(EsKey.class);
                            if (field.getType().equals(String.class)) {
                                keyHandler = new EsKeyHandler(fieldName, esKey.auto(), field);
                            } else {
                                this.logger.error("{} EsEntity key must String.class", clazz.getName());
                                throw new RuntimeException("EsEntity key must String.class");
                            }
                        } else {
                            this.logger.error("{} EsEntity multy keys", clazz.getName());
                            throw new RuntimeException("EsEntity multy keys");
                        }
                    } else {
                        this.logger.error("{} field {} miss EsColumn", clazz.getName(), fieldName);
                        throw new RuntimeException("field miss EsColumn");
                    }
                }
            }
        }
        //增加时间戳
        EsColumnHandler timestampHandler = new EsColumnHandler(TIMESTAMP_FIELD_NAME, TIMESTAMP_COLUMN_NAME, EsColumnType.DATE, 0);
        columnHandlerList.add(timestampHandler);
        //
        if (keyHandler == null) {
            this.logger.error("{} EsEntity miss key", clazz.getName());
            throw new RuntimeException("EsEntity miss key");
        } else {
            //实例化dao
            EsEntityDao esEntityDao = new EsEntityDaoImpl(index, keyHandler, columnHandlerList, clazz);
            //注册到bean
            BeanContext beanContext = AppContext.INSTANCE.getBeanContext();
            beanContext.add(this.name, clazz.getName(), esEntityDao);
        }
    }

    private void createStreamDao(Class<?> clazz)
    {
        String dbName;
        EsStream esStream = clazz.getAnnotation(EsStream.class);
        if (esStream.database().isEmpty()) {
            dbName = this.database;
        } else {
            dbName = esStream.database();
        }
        //根据类型获取索引名称
        String index;
        if (esStream.index().isEmpty()) {
            index = ToolUtil.getTableName(clazz);
        } else {
            index = esStream.index();
        }
        if (dbName.isEmpty() == false) {
            index = dbName + "_" + index;
        }
        //获取该实体所有字段集合
        List<EsColumnHandler> columnHandlerList = new ArrayList(0);
        Field[] fieldArray = clazz.getDeclaredFields();
        String fieldName;
        EsColumn esColumn;
        EsColumnType columnType;
        Object defaultValue;
        for (Field field : fieldArray) {
            if (Modifier.isStatic(field.getModifiers()) == false) {
                //非静态字段
                fieldName = field.getName();
                if (fieldName.equals(TIMESTAMP_FIELD_NAME) == false) {
                    if (field.isAnnotationPresent(EsColumn.class)) {
                        //
                        esColumn = field.getAnnotation(EsColumn.class);
                        columnType = this.getColumnType(clazz, field, esColumn);
                        defaultValue = this.getDefaultValue(columnType, esColumn);
                        EsColumnHandler columnHandler = new EsColumnHandler(fieldName, columnType, defaultValue);
                        columnHandlerList.add(columnHandler);
                    } else if (field.isAnnotationPresent(EsKey.class)) {
                        this.logger.error("{} EsStream can not use key", clazz.getName());
                        throw new RuntimeException("EsColumn can not use key");
                    } else {
                        this.logger.error("{} field {} miss EsColumn", clazz.getName(), fieldName);
                        throw new RuntimeException("field miss EsColumn");
                    }
                }
            }
        }
        //增加时间戳
        EsColumnHandler timestampHandler = new EsColumnHandler(TIMESTAMP_FIELD_NAME, TIMESTAMP_COLUMN_NAME, EsColumnType.DATE, 0);
        columnHandlerList.add(timestampHandler);
        //实例化dao
        EsStreamDao esStreamDao = new EsStreamDaoImpl(index, esStream.lifecycle(), columnHandlerList, clazz);
        //注册到bean
        BeanContext beanContext = AppContext.INSTANCE.getBeanContext();
        beanContext.add(this.name, clazz.getName(), esStreamDao);
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
            Class<?> componentType = type.getComponentType();
            if (componentType == boolean.class || componentType == Boolean.class) {
                result = EsColumnType.BOOLEAN;
            } else if (componentType == long.class || int.class == componentType || componentType == Integer.class || componentType == Long.class) {
                result = EsColumnType.LONG;
            } else if (componentType == double.class || componentType == Double.class) {
                result = EsColumnType.DOUBLE;
            } else if (componentType == Date.class) {
                result = EsColumnType.DATE;
            } else if (componentType == String.class) {
                result = EsColumnType.KEYWORD;
            } else {
                this.logger.error("{},{} EsColumn Array only support Array<Boolean>,Array<Long>,Array<Double>,Array<Date>,Array<String>", clazz.getName(), field.getName());
                throw new RuntimeException("EsColumn Array only support Array<Boolean>,Array<Long>,Array<Double>,Array<Date>,Array<String>");
            }
        } else if (Collection.class.isAssignableFrom(type)) {
            //集合泛型
            Type generictype = field.getGenericType();
            ParameterizedType listGenericType = (ParameterizedType) generictype;
            Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
            Class<?> subType = (Class<?>) listActualTypeArguments[0];
            if (subType == boolean.class || subType == Boolean.class) {
                result = EsColumnType.BOOLEAN;
            } else if (subType == long.class || int.class == subType || subType == Integer.class || subType == Long.class) {
                result = EsColumnType.LONG;
            } else if (subType == double.class || subType == Double.class) {
                result = EsColumnType.DOUBLE;
            } else if (subType == Date.class) {
                result = EsColumnType.DATE;
            } else if (subType == String.class) {
                result = EsColumnType.KEYWORD;
            } else {
                this.logger.error("{},{} EsColumn Collection only support Collection<Boolean>,Collection<Long>,Collection<Double>,Collection<Date>,Collection<String>", clazz.getName(), field.getName());
                throw new RuntimeException("EsColumn Collection only support Collection<Boolean>,Collection<Long>,Collection<Double>,Collection<Date>,Collection<String>");
            }
        } else {
            this.logger.error("{},{} EsColumn unsupport Object", clazz.getName(), field.getName());
            throw new RuntimeException("EsColumn unsupport Object");
        }
        return result;
    }

    public Object getDefaultValue(EsColumnType esColumnType, EsColumn esColumn) {
        Object defaultValue;
        switch(esColumnType) {
            case LONG:
                if(esColumn.defaultValue().isEmpty()) {
                    defaultValue = 0;
                } else {
                    try {
                        defaultValue = Long.parseLong(esColumn.defaultValue());
                    } catch (NumberFormatException e) {
                        defaultValue = 0;
                    }
                }
                break;
            case DOUBLE:
                if(esColumn.defaultValue().isEmpty()) {
                    defaultValue = 0;
                } else {
                    try {
                        defaultValue = Double.parseDouble(esColumn.defaultValue());
                    } catch (NumberFormatException e) {
                        defaultValue = 0;
                    }
                }
                break;
            case DATE:
                defaultValue = 0;
                break;
            case TEXT:
            case KEYWORD:
                defaultValue = esColumn.defaultValue();
                break;
            case BOOLEAN:
                if(esColumn.defaultValue().isEmpty()) {
                    defaultValue = false;
                } else {
                    defaultValue = Boolean.parseBoolean(esColumn.defaultValue());
                }
                break;
            default:
                defaultValue = "";
        }
        return defaultValue;
    }

    @Override
    public void build()
    {
        BeanContext beanContext = AppContext.INSTANCE.getBeanContext();
        Map<String, Object> entityDaoMap = beanContext.get(this.name);
        for (Object value : entityDaoMap.values()) {
            ((AbstractEsDao) value).setUp();
        }
    }

    public String getDatabase()
    {
        return database;
    }

    public RestClient getRestClient()
    {
        return restClient;
    }
    
    public <B extends Object> B get(Class<?> clazz)
    {
        BeanContext beanContext = AppContext.INSTANCE.getBeanContext();
        return beanContext.get(this.name, clazz);
    }

}
