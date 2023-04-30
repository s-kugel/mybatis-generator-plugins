package org.mybatis.generator.plugins;

import java.util.List;
import java.util.Map;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

public class SelectiveSQLPlugin extends PluginAdapter {

  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

  @Override
  public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
    var baseRecordType = PluginUtils.baseRecordType(introspectedTable);
    var sqlProvider = PluginUtils.sqlProviderType(introspectedTable);

    {
      var method = new Method("selectBy");
      method.setDefault(false);
      method.setAbstract(true);
      method.addParameter(new Parameter(baseRecordType, "row"));
      method.addAnnotation(
          "@%s(type=%s.class, method=\"%s\")"
              .formatted(
                  PluginUtils.SELECT_PROVIDER.getShortName(),
                  sqlProvider.getShortName(),
                  "selectBy"));
      method.setReturnType(PluginUtils.listBaseRecordType(introspectedTable));

      interfaze.addMethod(method);
    }

    {
      var method = new Method("deleteBy");
      method.setDefault(false);
      method.setAbstract(true);
      method.addParameter(new Parameter(baseRecordType, "row"));
      method.addAnnotation(
          "@%s(type=%s.class, method=\"%s\")"
              .formatted(
                  PluginUtils.DELETE_PROVIDER.getShortName(),
                  sqlProvider.getShortName(),
                  "deleteBy"));
      method.setReturnType(PluginUtils.INT);

      interfaze.addMethod(method);
    }

    interfaze.addImportedType(PluginUtils.LIST);
    interfaze.addImportedType(PluginUtils.SELECT_PROVIDER);
    interfaze.addImportedType(PluginUtils.DELETE_PROVIDER);

    return true;
  }

  @Override
  public boolean providerGenerated(
      TopLevelClass topLevelClass, //
      IntrospectedTable introspectedTable) {
    var tableName = PluginUtils.tableName(introspectedTable);
    var baseRecordType = PluginUtils.baseRecordType(introspectedTable);
    var rows = introspectedTable.getAllColumns().stream().map(RowParam::of).toList();
    var templateParam =
        Map.of(
            "tableName", tableName,
            "rows", rows);

    {
      var method = new Method("selectBy");
      method.setDefault(false);
      method.setAbstract(false);
      method.setVisibility(JavaVisibility.PUBLIC);
      method.addParameter(new Parameter(baseRecordType, "row"));
      method.setReturnType(PluginUtils.STRING);
      method.addBodyLine(PluginUtils.processTemplate("selectBy", templateParam));

      topLevelClass.addMethod(method);
    }

    {
      var method = new Method("deleteBy");
      method.setDefault(false);
      method.setAbstract(false);
      method.setVisibility(JavaVisibility.PUBLIC);
      method.addParameter(new Parameter(baseRecordType, "row"));
      method.setReturnType(PluginUtils.STRING);
      method.addBodyLine(PluginUtils.processTemplate("deleteBy", templateParam));

      topLevelClass.addMethod(method);
    }

    return true;
  }
}
