package com.silber_kugel.mybatis.generator.example.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.ibatis.session.Configuration;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MyBatisProperties {

  String mapperLocations;

  String typeHandlersPackage;

  Configuration configuration;
}
