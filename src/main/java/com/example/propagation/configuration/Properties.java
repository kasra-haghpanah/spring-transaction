package com.example.propagation.configuration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;

@Configuration("properties")
/*@PropertySources(
        {
                @PropertySource(
                        value = "classpath:application.properties",
                        ignoreResourceNotFound = true,
                        encoding = "UTF-8"
                )
        }
)*/
public class Properties {

    private static final Map<String, Object> config = new HashMap<String, Object>();

    public Properties(
            ApplicationContext applicationContext,
            ConfigurableEnvironment configurableEnvironment,
            Environment environment
    ) throws IOException {

        Resource[] resources = applicationContext.getResources("classpath:*.properties");

        config.put("swagger.version", environment.getProperty("swagger.version"));
        config.put("time.zone", environment.getProperty("time.zone"));
        config.put("spring.application.name", environment.getProperty("spring.application.name"));
        config.put("springdoc.packagesToScan", environment.getProperty("springdoc.packagesToScan"));

        config.put("server.host", environment.getProperty("server.host"));
        config.put("server.port", Integer.valueOf(environment.getProperty("server.port")));
        config.put("contextPath", environment.getProperty("contextPath"));

        config.put("serverName", environment.getProperty("serverName"));
        config.put("poweredBy", environment.getProperty("poweredBy"));

        config.put("mysql.model.basePackage", environment.getProperty("mysql.model.basePackage"));
        config.put("mysql.version", environment.getProperty("mysql.version"));
        config.put("mysql.url", environment.getProperty("mysql.url"));
        config.put("mysql.driver-class-name", environment.getProperty("mysql.driver-class-name"));
        config.put("mysql.username", environment.getProperty("mysql.username"));
        config.put("mysql.password", environment.getProperty("mysql.password"));
        config.put("mysql.ddl-auto", environment.getProperty("mysql.ddl-auto"));
        config.put("mysql.show-sql", environment.getProperty("mysql.show-sql"));
        config.put("mysql.hibernate.format-sql", environment.getProperty("mysql.hibernate.format-sql"));
        config.put("mysql.dialect", environment.getProperty("mysql.dialect"));
        config.put("mysql.script.directory", environment.getProperty("mysql.script.directory"));


        Mono.just(config).block();
    }

    private static Object get(String key) {
        return config.get(key);
    }

    private static <T> T get(String key, Class<T> T) {
        return (T) config.get(key);
    }

    public static String getSwaggerVersion() {
        return get("swagger.version", String.class);
    }

    public static String getTimeZone() {
        return get("time.zone", String.class);
    }

    public static String getApplicationName() {
        return get("spring.application.name", String.class);
    }

    public static String getSpringdocPackagesToScan() {
        return get("springdoc.packagesToScan", String.class);
    }

    public static String getServerHost() {
        return get("server.host", String.class);
    }

    public static Integer getServerPort() {
        return get("server.port", Integer.class);
    }

    public static String getContextPath() {
        return get("contextPath", String.class);
    }

    public static String getServerName() {
        return get("serverName", String.class);
    }

    public static String getPoweredBy() {
        return get("poweredBy", String.class);
    }

    public static String[] getMysqlModelBasePackage() {
        String listAsString = get("mysql.model.basePackage", String.class);
        if (listAsString == null) {
            return null;
        }
        String[] array;
        if (listAsString.indexOf(",") > 0) {
            array = listAsString.split(",");
            for (int i = 0; i < array.length; i++) {
                array[i] = array[i].trim();
            }
            return array;
        }
        array = new String[]{listAsString};
        return array;
    }

    public static String getMysqlVersion() {
        return get("mysql.version", String.class);
    }

    public static String getMysqlUrl() {
        return get("mysql.url", String.class);
    }

    public static String getMysqlDriverClassName() {
        return get("mysql.driver-class-name", String.class);
    }

    public static String getMysqlUsername() {
        return get("mysql.username", String.class);
    }

    public static String getMysqlPassword() {
        return get("mysql.password", String.class);
    }

    public static String getMysqlDdlAuto() {
        return get("mysql.ddl-auto", String.class);
    }

    public static Boolean getMysqlShowSql() {
        String flag = get("mysql.show-sql", String.class);
        if (flag == null) flag = "";
        flag = flag.trim();
        if (flag.equals("true")) return true;
        return false;
    }

    public static String getMysqlHibernateFormatSql() {
        return get("mysql.hibernate.format-sql", String.class);
    }

    public static String getMysqlDialect() {
        return get("mysql.dialect", String.class);
    }

    public static String getMysqlScriptDirectory() {
        return get("mysql.script.directory", String.class);
    }
}
