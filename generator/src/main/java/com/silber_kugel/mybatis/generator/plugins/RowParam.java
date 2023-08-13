package com.silber_kugel.mybatis.generator.plugins;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.mybatis.generator.api.IntrospectedColumn;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RowParam {

  private String columnName;

  private String javaProperty;

  private String jdbcType;

  public static RowParam of(IntrospectedColumn column) {
    var param = new RowParam();
    param.setColumnName(column.getActualColumnName());
    param.setJavaProperty(column.getJavaProperty());
    param.setJdbcType(column.getJdbcTypeName());
    return param;
  }
}
