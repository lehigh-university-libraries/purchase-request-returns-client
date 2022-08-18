package edu.lehigh.libraries.purchase_request.returns_client.security;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import edu.lehigh.libraries.purchase_request.returns_client.config.PersistenceConfig;

@Configuration
@EnableJpaRepositories(
  basePackages = "edu.lehigh.libraries.purchase_request.returns_client.security",
  entityManagerFactoryRef = "userEntityManager",
  transactionManagerRef = "userTransactionManager")
public class PersistenceUserConfig extends PersistenceConfig {

    @Primary
    @Bean(name = "userDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    @Override
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "userEntityManager")
    @Primary
    @Override
    public LocalContainerEntityManagerFactoryBean entityManager() {
        String packageName = PersistenceUserConfig.class.getPackageName();
        return super.entityManager(packageName);
    }

    @Bean(name = "userTransactionManager")
    @Primary
    @Override
    public PlatformTransactionManager transactionManager() {
        return super.transactionManager();
    }

}
