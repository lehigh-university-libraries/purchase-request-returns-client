package edu.lehigh.libraries.purchase_request.returns_client.reshare;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import edu.lehigh.libraries.purchase_request.returns_client.config.PersistenceConfig;

@Configuration
@ConditionalOnProperty(name="returns-client.reshare.db.host")
@EnableJpaRepositories(
  basePackages = "edu.lehigh.libraries.purchase_request.returns_client.reshare",
  entityManagerFactoryRef = "reshareEntityManager",
  transactionManagerRef = "reshareTransactionManager")
public class PersistanceReShareConfig extends PersistenceConfig {
    
  @Bean(name = "reshareDataSource")
  @ConfigurationProperties(prefix="spring.reshare-datasource")
  @Override
  public DataSource dataSource() {
      return DataSourceBuilder.create().build();
  }

  @Bean(name = "reshareEntityManager")
  @Override
  public LocalContainerEntityManagerFactoryBean entityManager() {
      String packageName = PersistanceReShareConfig.class.getPackageName();
      return super.entityManager(packageName);
  }

  @Bean(name = "reshareTransactionManager")
  @Override
  public PlatformTransactionManager transactionManager() {
      return super.transactionManager();
  }
}
