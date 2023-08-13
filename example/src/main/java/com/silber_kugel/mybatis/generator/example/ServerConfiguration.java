package com.silber_kugel.mybatis.generator.example;

import com.silber_kugel.mybatis.generator.example.settings.DataSourceConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@ConfigurationPropertiesScan(
    basePackages = {
      "org.mybatis.generator.example",
    })
@EnableAsync
@EnableCaching
@ComponentScan(
    basePackages = {
      "org.mybatis.generator.example",
    })
@ImportAutoConfiguration
@Import({DataSourceConfiguration.class})
public class ServerConfiguration {}
