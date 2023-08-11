package org.mybatis.generator.plugins;

import java.util.List;
import java.util.Map;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

public class BulkInsertPlugin extends PluginAdapter {

  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

  @Override
  public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
    var sqlProvider = PluginUtils.sqlProviderType(introspectedTable);

    var method = new Method("bulkInsert");
    method.setDefault(false);
    method.setAbstract(true);
    method.addParameter(new Parameter(PluginUtils.listBaseRecordType(introspectedTable), "rows"));
    method.addAnnotation(
        "@InsertProvider(type=%s.class, method=\"bulkInsert\")"
            .formatted(sqlProvider.getShortName()));
    method.setReturnType(PluginUtils.INTEGER);

    interfaze.addImportedType(PluginUtils.INSERT_PROVIDER);
    interfaze.addImportedType(PluginUtils.LIST);
    interfaze.addMethod(method);

    return true;
  }

  @Override
  public boolean providerGenerated(
      TopLevelClass topLevelClass, //
      IntrospectedTable introspectedTable) {
    var rows = introspectedTable.getAllColumns().stream().map(RowParam::of).toList();
    var templateParam = Map.of("tableName", PluginUtils.tableName(introspectedTable), "rows", rows);

    var method = new Method("bulkInsert");
    method.setDefault(false);
    method.setAbstract(false);
    method.setVisibility(JavaVisibility.PUBLIC);
    method.addParameter(new Parameter(PluginUtils.listBaseRecordType(introspectedTable), "rows"));
    method.setReturnType(PluginUtils.STRING);
    method.addBodyLine(PluginUtils.processTemplate("bulkInsert", templateParam));

    topLevelClass.addImportedType(PluginUtils.OBJECTS);
    topLevelClass.addImportedType(PluginUtils.LIST);
    topLevelClass.addMethod(method);

    return true;
  }
}
