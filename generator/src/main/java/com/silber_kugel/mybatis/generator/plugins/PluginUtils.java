package com.silber_kugel.mybatis.generator.plugins;

import freemarker.template.Configuration;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PluginUtils {

  public static final FullyQualifiedJavaType SELECT_PROVIDER =
      new FullyQualifiedJavaType("org.apache.ibatis.annotations.SelectProvider");

  public static final FullyQualifiedJavaType INSERT_PROVIDER =
      new FullyQualifiedJavaType("org.apache.ibatis.annotations.InsertProvider");

  public static final FullyQualifiedJavaType UPDATE_PROVIDER =
      new FullyQualifiedJavaType("org.apache.ibatis.annotations.UpdateProvider");

  public static final FullyQualifiedJavaType DELETE_PROVIDER =
      new FullyQualifiedJavaType("org.apache.ibatis.annotations.DeleteProvider");

  public static final FullyQualifiedJavaType SELECT_ANNOTATION =
      new FullyQualifiedJavaType("org.apache.ibatis.annotations.Select");

  public static final FullyQualifiedJavaType INSERT_ANNOTATION =
      new FullyQualifiedJavaType("org.apache.ibatis.annotations.Insert");

  public static final FullyQualifiedJavaType UPDATE_ANNOTATION =
      new FullyQualifiedJavaType("org.apache.ibatis.annotations.Update");

  public static final FullyQualifiedJavaType DELETE_ANNOTATION =
      new FullyQualifiedJavaType("org.apache.ibatis.annotations.Delete");

  public static final FullyQualifiedJavaType CURSOR =
      new FullyQualifiedJavaType("org.apache.ibatis.cursor.Cursor");

  public static final FullyQualifiedJavaType LIST = new FullyQualifiedJavaType("java.util.List");

  public static final FullyQualifiedJavaType OBJECTS =
      new FullyQualifiedJavaType("java.util.Objects");

  public static final FullyQualifiedJavaType STRING =
      new FullyQualifiedJavaType("java.lang.String");

  public static final FullyQualifiedJavaType INTEGER =
      new FullyQualifiedJavaType("java.lang.Integer");

  public static String tableName(IntrospectedTable table) {
    return table.getTableConfiguration().getTableName();
  }

  public static FullyQualifiedJavaType baseRecordType(IntrospectedTable table) {
    return new FullyQualifiedJavaType(table.getBaseRecordType());
  }

  public static FullyQualifiedJavaType listBaseRecordType(IntrospectedTable table) {
    var baseRecordType = baseRecordType(table);
    return new FullyQualifiedJavaType("List<%s>".formatted(baseRecordType.getShortName()));
  }

  public static FullyQualifiedJavaType cursorBaseRecordType(IntrospectedTable table) {
    var baseRecordType = baseRecordType(table);
    return new FullyQualifiedJavaType("Cursor<%s>".formatted(baseRecordType.getShortName()));
  }

  public static FullyQualifiedJavaType sqlProviderType(IntrospectedTable table) {
    return new FullyQualifiedJavaType(table.getTableConfiguration().getSqlProviderName());
  }

  @SneakyThrows
  public static String processTemplate(String templateName, Map<String, Object> templateParam) {
    var configuration = new Configuration(Configuration.VERSION_2_3_32);
    configuration.setClassForTemplateLoading(PluginUtils.class, "/templates");

    String generated;
    try (var writer = new StringWriter()) {
      var template = configuration.getTemplate(templateName + ".ftl", Locale.JAPAN, "UTF-8");
      var environment = template.createProcessingEnvironment(templateParam, writer);
      environment.setOutputEncoding("UTF-8");
      environment.process();
      generated = writer.toString();
    }

    return generated;
  }
}
