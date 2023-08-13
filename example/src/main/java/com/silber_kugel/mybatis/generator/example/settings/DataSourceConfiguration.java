package com.silber_kugel.mybatis.generator.example.settings;

import com.silber_kugel.mybatis.generator.example.properties.DataSourceProperties;
import com.silber_kugel.mybatis.generator.example.properties.MyBatisProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import javax.sql.DataSource;
import lombok.val;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

@MapperScan(
    basePackages = { //
      "org.mybatis.generator.example.domain.repository", //
    },
    sqlSessionTemplateRef = "sqlSessionTemplate")
public class DataSourceConfiguration {

  private static final ResourcePatternResolver RESOLVER = new PathMatchingResourcePatternResolver();

  @Bean(name = "dataSourceProperties")
  @ConfigurationProperties(prefix = "spring.datasource")
  public DataSourceProperties dataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean(name = "dataSource")
  @Primary
  public DataSource dataSource(
      @Qualifier("dataSourceProperties") final DataSourceProperties props) {
    HikariConfig config = new HikariConfig();
    config.setDriverClassName(props.getDriverClassName());
    config.setJdbcUrl(props.getUrl());
    config.setUsername(props.getUsername());
    config.setPassword(props.getPassword());
    config.setPoolName(props.getPoolName());
    config.setMaximumPoolSize(props.getMaximumPoolSize());
    config.setMinimumIdle(props.getMaximumPoolSize());
    config.setIdleTimeout(props.getIdleTimeout());
    config.setConnectionTimeout(props.getConnectionTimeout());
    config.setLeakDetectionThreshold(props.getLeakDetectionThreshold());
    return new HikariDataSource(config);
  }

  @Bean(name = "myBatisProperties")
  @ConfigurationProperties(prefix = "mybatis")
  public MyBatisProperties myBatisProperties() {
    return new MyBatisProperties();
  }

  @Bean(name = "sqlSessionFactory")
  public SqlSessionFactory sqlSessionFactory(
      @Qualifier("dataSource") final DataSource dataSource,
      @Qualifier("myBatisProperties") MyBatisProperties props)
      throws Exception {
    val bean = new SqlSessionFactoryBean();
    bean.setDataSource(dataSource);
    bean.setMapperLocations(getResource(props.getMapperLocations()));
    bean.setTypeHandlersPackage(props.getTypeHandlersPackage());
    bean.setConfiguration(props.getConfiguration());
    return bean.getObject();
  }

  @Bean(name = "sqlSessionTemplate")
  public SqlSessionTemplate sqlSessionTemplate(
      @Qualifier("sqlSessionFactory") final SqlSessionFactory sqlSessionFactory) {
    return new SqlSessionTemplate(sqlSessionFactory);
  }

  private static Resource[] getResource(String location) {
    try {
      return RESOLVER.getResources(location);
    } catch (IOException e) {
      return new Resource[0];
    }
  }
}
