package jhelp;

import jhelp.repos.TermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@EnableJpaRepositories(basePackages = "jhelp.repos")
@PropertySource("classpath:app.properties")
public class JavaConfig {

    @Value("${data.source.url}")
    private String dataSourceUrl;

    @Value("${server.socket.port}")
    private int port;

    @Value("${server.user.name}")
    private String userName;

    @Value("${server.user.password}")
    private String password;

    @Value("${script.fillTable.term.begin}" + "${term.source.path}" + "${script.fillTable.end}")
    private String fillTermScript;

    @Value("${script.fillTable.definition.begin}" +"${definition.source.path}" + "${script.fillTable.end}")
    private String fillDefinitionScript;

    @Value("${dataBase.fillTables}")
    private boolean fillTables;

    @Value("${dataBase.needPassword}")
    private boolean needPassword;

    @Value("${jdbcDriver}")
    private String jdbcDriver;

    @Value("${hibernate.dialect}")
    private String hibernateDialect;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(jdbcDriver);
        dataSource.setUrl(dataSourceUrl);
        dataSource.setUsername(userName);

        if(needPassword){
            dataSource.setPassword(password);
        }
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {


        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.DERBY);
        vendorAdapter.setDatabasePlatform(hibernateDialect);

        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("jhelp.orm");
        factory.setDataSource(dataSource());
        factory.setJpaDialect(jpaDialect());
        return factory;

    }

    @Bean
    public JpaDialect jpaDialect() {
        return new HibernateJpaDialect();
    }


    @Bean
    @Autowired
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory, DataSource dataSource) throws SQLException {

        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        if(fillTables){
            dataSource.getConnection().createStatement().execute(fillTermScript);
            dataSource.getConnection().createStatement().execute(fillDefinitionScript);
        }
        return txManager;

    }

    @Bean
    @Autowired
    public ServerDB jHelpServerDB(TermRepository termRepository){
        ServerDB serverDB = new ServerDB(termRepository);
        serverDB.setPORT(port);
        return serverDB;
    }

    @Bean
    @Autowired
    public TableSaver tableSaver(DataSource dataSource){
        TableSaver tableSaver = new TableSaver();
        tableSaver.setDataSource(dataSource);
        return tableSaver;
    }
}
