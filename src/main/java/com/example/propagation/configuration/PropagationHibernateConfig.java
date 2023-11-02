package com.example.propagation.configuration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.net.ConnectException;


/**
 * Created by kasra.haghpanah on 10/5/2019.
 */
// https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa
//https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.5-Release-Notes#upgrading-from-spring-boot-24
@Configuration("propagationHibernateConfig")
@DependsOn("properties")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@EnableTransactionManagement
@EnableJpaRepositories(
        //repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class,
        basePackages = {"com.example.propagation.ddd.repository"},
        entityManagerFactoryRef = "propagationEMF",
        transactionManagerRef = "propagationTM"
)
public class PropagationHibernateConfig {

    final String ddlAuto = Properties.getMysqlDdlAuto();
    final String dialect = Properties.getMysqlDialect();
    final Boolean showSql = Properties.getMysqlShowSql();
    final String formatSql = Properties.getMysqlHibernateFormatSql();
    final String version = Properties.getMysqlVersion();
    final String url = Properties.getMysqlUrl();
    final String driverClassName = Properties.getMysqlDriverClassName();
    final String username = Properties.getMysqlUsername();
    final String password = Properties.getMysqlPassword();
    final String[] mysqlModelBasePackage = Properties.getMysqlModelBasePackage();
    final String mysqlScriptDirectory = Properties.getMysqlScriptDirectory();

    @Bean
    @Qualifier("propagationDataSource")
    @Retryable(include = {ConnectException.class},
            maxAttemptsExpression = "30000",
            backoff = @Backoff(
                    delayExpression = "30000",
                    maxDelayExpression = "2",
                    multiplierExpression = "500000"
            ))
    public DataSource propagationDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean("propagationTM")
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(propagationEMF());
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    @Qualifier("propagationTemplate")
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(propagationDataSource());
    }

    @Bean("propagationEMF")
    public EntityManagerFactory propagationEMF() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(propagationDataSource());
        factory.setPackagesToScan(mysqlModelBasePackage);

        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();

        jpaVendorAdapter.setDatabasePlatform(dialect);
        if (!ddlAuto.equals("none")) {
            jpaVendorAdapter.setGenerateDdl(true);
        }
        jpaVendorAdapter.setShowSql(showSql);
        jpaVendorAdapter.setDatabase(Database.ORACLE);

        factory.setJpaVendorAdapter(jpaVendorAdapter);
        factory.setJpaDialect(jpaVendorAdapter.getJpaDialect());


        java.util.Properties jpaProperties = new java.util.Properties();

        jpaProperties.put(AvailableSettings.DIALECT, dialect);
        jpaProperties.put(AvailableSettings.FORMAT_SQL, formatSql);
        jpaProperties.put(AvailableSettings.SHOW_SQL, showSql);
        //jpaProperties.put(AvailableSettings.DEFAULT_SCHEMA, "pdb_dev_ehs");

        // https://thorben-janssen.com/standardized-schema-generation-data-loading-jpa-2-1/

        String sqlFileName = "1__init.sql";
        String ddlSqlFile = String.format("%s%s", mysqlScriptDirectory, sqlFileName);
        String ddlSqlDropFile = String.format("%s%s%s", mysqlScriptDirectory, version, "-drop.sql");

        String action = "metadata"; //"script-then-metadata";
        //jpaProperties.put("jakarta.persistence.schema-generation.scripts.create-source", action);
        //jpaProperties.put("jakarta.persistence.schema-generation.scripts.drop-source", action);
        //jpaProperties.put(AvailableSettings.JAKARTA_HBM2DDL_SCRIPTS_ACTION, ddlAuto);
        jpaProperties.put(AvailableSettings.HBM2DDL_DELIMITER, ";");
        jpaProperties.put(AvailableSettings.HBM2DDL_CHARSET_NAME, "UTF-8");
        jpaProperties.put(AvailableSettings.JAKARTA_HBM2DDL_SCRIPTS_CREATE_TARGET, ddlSqlFile);
        jpaProperties.put(AvailableSettings.JAKARTA_HBM2DDL_SCRIPTS_DROP_TARGET, ddlSqlDropFile);
        jpaProperties.put(AvailableSettings.HBM2DDL_AUTO, ddlAuto);
        //jpaProperties.put("spring.jpa.hibernate.ddl-auto", ddlAuto);
        factory.setJpaProperties(jpaProperties);
        factory.afterPropertiesSet();

        return factory.getObject();
    }

    @Bean("propagationEntityManager")
    @DependsOn("propagationEMF")
    public EntityManager propagationEntityManager() {
        return propagationEMF().createEntityManager();
    }

    @Bean
    public DataSourceInitializer healthDataSourceInitializer(Environment environment) {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(propagationDataSource());
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        //databasePopulator.addScript(new ClassPathResource("ars-data.sql"));
        //databasePopulator.setSqlScriptEncoding("UTF-8");
        dataSourceInitializer.setDatabasePopulator(databasePopulator);
        dataSourceInitializer.setEnabled(environment.getProperty("spring.datasource.initialize", Boolean.class, false));
        return dataSourceInitializer;
    }
}
