package org.mybatis.generator.plugins;

import freemarker.template.Configuration;
import java.io.StringWriter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import lombok.SneakyThrows;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

public class VersionColumnPlugin extends PluginAdapter {

  private String versionColumnName;

  private final Configuration configuration;

  public VersionColumnPlugin() {
    var configuration = new Configuration(Configuration.VERSION_2_3_32);
    configuration.setClassForTemplateLoading(getClass(), "/templates");
    this.configuration = configuration;
  }

  @Override
  public void setProperties(Properties properties) {
    this.versionColumnName =
        Optional.ofNullable(properties.getProperty("column")).orElse("version");
  }

  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

  @Override
  public boolean clientGenerated(
      Interface interfaze, //
      IntrospectedTable introspectedTable) {
    if (!hasVersionColumn(introspectedTable)) {
      return true;
    }

    var baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
    var sqlProvider =
        new FullyQualifiedJavaType(introspectedTable.getTableConfiguration().getSqlProviderName());

    var updateProvider = new FullyQualifiedJavaType("org.apache.ibatis.annotations.UpdateProvider");
    interfaze.addImportedType(updateProvider);

    var updateByPrimaryKeyAndVersion =
        generateClientMethod(
            "updateByPrimaryKeyAndVersion",
            updateProvider,
            sqlProvider,
            baseRecordType,
            new FullyQualifiedJavaType("int"));
    interfaze.addMethod(updateByPrimaryKeyAndVersion);

    var deleteProvider = new FullyQualifiedJavaType("org.apache.ibatis.annotations.DeleteProvider");
    interfaze.addImportedType(deleteProvider);

    var deleteByPrimaryKeyAndVersion =
        generateClientMethod(
            "deleteByPrimaryKeyAndVersion",
            deleteProvider,
            sqlProvider,
            baseRecordType,
            new FullyQualifiedJavaType("int"));
    interfaze.addMethod(deleteByPrimaryKeyAndVersion);

    return true;
  }

  private Method generateClientMethod(
      String methodName,
      FullyQualifiedJavaType annotation,
      FullyQualifiedJavaType providerType,
      FullyQualifiedJavaType baseRecordType,
      FullyQualifiedJavaType returnType) {
    var method = new Method(methodName);

    method.setDefault(false);
    method.setAbstract(true);
    method.addParameter(new Parameter(baseRecordType, "row"));
    method.addAnnotation(
        "@%s(type=%s.class, method=\"%s\")"
            .formatted(annotation.getShortName(), providerType.getShortName(), methodName));
    method.setReturnType(returnType);

    return method;
  }

  @Override
  public boolean providerGenerated(
      TopLevelClass topLevelClass, //
      IntrospectedTable introspectedTable) {
    if (!hasVersionColumn(introspectedTable)) {
      return true;
    }

    topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.Objects"));

    var tableName = introspectedTable.getTableConfiguration().getTableName();
    var baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
    var primaryKeys = introspectedTable.getPrimaryKeyColumns().stream().map(RowParam::of).toList();
    var rows =
        introspectedTable.getAllColumns().stream()
            .filter(v -> !Objects.equals(v.getActualColumnName(), versionColumnName))
            .map(RowParam::of)
            .toList();
    var version =
        introspectedTable.getAllColumns().stream()
            .filter(v -> Objects.equals(v.getActualColumnName(), versionColumnName))
            .map(RowParam::of)
            .findFirst()
            .get();

    var templateParam =
        Map.of(
            "tableName", tableName, "primaryKeys", primaryKeys, "rows", rows, "version", version);

    try {
      var updateByPrimaryKeyAndVersion =
          generateProviderMethod(
              "updateByPrimaryKeyAndVersion", //
              baseRecordType, //
              templateParam);
      topLevelClass.addMethod(updateByPrimaryKeyAndVersion);

      var deleteByPrimaryKeyAndVersion =
          generateProviderMethod(
              "deleteByPrimaryKeyAndVersion", //
              baseRecordType, //
              templateParam);
      topLevelClass.addMethod(deleteByPrimaryKeyAndVersion);
    } catch (Exception e) {
      return true;
    }

    return true;
  }

  @SneakyThrows
  private Method generateProviderMethod(
      String methodName, //
      FullyQualifiedJavaType baseRecordType, //
      Map<String, Object> templateParam) {
    var method = new Method(methodName);

    method.setDefault(false);
    method.setAbstract(false);
    method.setVisibility(JavaVisibility.PUBLIC);
    method.addParameter(new Parameter(baseRecordType, "row"));
    method.setReturnType(new FullyQualifiedJavaType("java.lang.String"));

    try (var writer = new StringWriter()) {
      var template = configuration.getTemplate(methodName + ".ftl", Locale.JAPAN, "UTF-8");
      var environment = template.createProcessingEnvironment(templateParam, writer);
      environment.setOutputEncoding("UTF-8");
      environment.process();

      method.addBodyLine(writer.toString());
    }

    return method;
  }

  private boolean hasVersionColumn(IntrospectedTable introspectedTable) {
    return introspectedTable.getAllColumns().stream()
        .map(IntrospectedColumn::getActualColumnName)
        .anyMatch(v -> Objects.equals(versionColumnName, v));
  }
}
