package com.silber_kugel.mybatis.generator.plugins;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

public class VersionColumnPlugin extends PluginAdapter {

  private String versionColumnName;

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
  public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
    if (!hasVersionColumn(introspectedTable)) {
      return true;
    }

    var baseRecordType = PluginUtils.baseRecordType(introspectedTable);
    var sqlProvider = PluginUtils.sqlProviderType(introspectedTable);

    {
      var method = new Method("updateByPrimaryKeyAndVersion");
      method.setDefault(false);
      method.setAbstract(true);
      method.addParameter(new Parameter(baseRecordType, "row"));
      method.addAnnotation(
          "@%s(type=%s.class, method=\"%s\")"
              .formatted(
                  PluginUtils.UPDATE_PROVIDER.getShortName(),
                  sqlProvider.getShortName(),
                  "updateByPrimaryKeyAndVersion"));
      method.setReturnType(PluginUtils.INTEGER);

      interfaze.addMethod(method);
    }

    {
      var method = new Method("deleteByPrimaryKeyAndVersion");
      method.setDefault(false);
      method.setAbstract(true);
      method.addParameter(new Parameter(baseRecordType, "row"));
      method.addAnnotation(
          "@%s(type=%s.class, method=\"%s\")"
              .formatted(
                  PluginUtils.DELETE_PROVIDER.getShortName(),
                  sqlProvider.getShortName(),
                  "deleteByPrimaryKeyAndVersion"));
      method.setReturnType(PluginUtils.INTEGER);

      interfaze.addMethod(method);
    }

    interfaze.addImportedType(PluginUtils.UPDATE_PROVIDER);
    interfaze.addImportedType(PluginUtils.DELETE_PROVIDER);

    return true;
  }

  @Override
  public boolean providerGenerated(
      TopLevelClass topLevelClass, //
      IntrospectedTable introspectedTable) {
    if (!hasVersionColumn(introspectedTable)) {
      return true;
    }

    var tableName = PluginUtils.tableName(introspectedTable);
    var baseRecordType = PluginUtils.baseRecordType(introspectedTable);
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

    {
      var method = new Method("updateByPrimaryKeyAndVersion");
      method.setDefault(false);
      method.setAbstract(false);
      method.setVisibility(JavaVisibility.PUBLIC);
      method.addParameter(new Parameter(baseRecordType, "row"));
      method.setReturnType(PluginUtils.STRING);
      method.addBodyLine(
          PluginUtils.processTemplate("updateByPrimaryKeyAndVersion", templateParam));

      topLevelClass.addMethod(method);
    }

    {
      var method = new Method("deleteByPrimaryKeyAndVersion");
      method.setDefault(false);
      method.setAbstract(false);
      method.setVisibility(JavaVisibility.PUBLIC);
      method.addParameter(new Parameter(baseRecordType, "row"));
      method.setReturnType(PluginUtils.STRING);
      method.addBodyLine(
          PluginUtils.processTemplate("deleteByPrimaryKeyAndVersion", templateParam));

      topLevelClass.addMethod(method);
    }

    topLevelClass.addImportedType(PluginUtils.OBJECTS);

    return true;
  }

  private boolean hasVersionColumn(IntrospectedTable introspectedTable) {
    return introspectedTable.getAllColumns().stream()
        .map(IntrospectedColumn::getActualColumnName)
        .anyMatch(v -> Objects.equals(versionColumnName, v));
  }
}
