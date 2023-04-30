package org.mybatis.generator.plugins;

import freemarker.template.Configuration;
import java.io.StringWriter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

public class BulkInsertPlugin extends PluginAdapter {

  private final Configuration configuration;

  public BulkInsertPlugin() {
    var configuration = new Configuration(Configuration.VERSION_2_3_32);
    configuration.setClassForTemplateLoading(getClass(), "/templates");
    this.configuration = configuration;
  }

  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

  @Override
  public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
    var baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
    var sqlProvider =
        new FullyQualifiedJavaType(introspectedTable.getTableConfiguration().getSqlProviderName());

    var insertProvider = new FullyQualifiedJavaType("org.apache.ibatis.annotations.InsertProvider");
    interfaze.addImportedType(insertProvider);
    interfaze.addImportedType(new FullyQualifiedJavaType("java.util.List"));

    var bulkInsert = new Method("bulkInsert");
    bulkInsert.setDefault(false);
    bulkInsert.setAbstract(true);
    bulkInsert.addParameter(
        new Parameter(
            new FullyQualifiedJavaType("List<%s>".formatted(baseRecordType.getShortName())),
            "rows"));
    bulkInsert.addAnnotation(
        "@InsertProvider(type=%s.class, method=\"bulkInsert\")"
            .formatted(sqlProvider.getShortName()));
    bulkInsert.setReturnType(new FullyQualifiedJavaType("int"));

    interfaze.addMethod(bulkInsert);

    return true;
  }

  @Override
  public boolean providerGenerated(
      TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
    var tableName = introspectedTable.getTableConfiguration().getTableName();
    var baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
    var rows = introspectedTable.getAllColumns().stream().map(RowParam::of).toList();
    var templateParam =
        Map.of(
            "tableName", tableName,
            "rows", rows);

    var bulkInsert = new Method("bulkInsert");
    bulkInsert.setDefault(false);
    bulkInsert.setAbstract(false);
    bulkInsert.setVisibility(JavaVisibility.PUBLIC);
    bulkInsert.addParameter(
        new Parameter(
            new FullyQualifiedJavaType("List<%s>".formatted(baseRecordType.getShortName())),
            "rows"));
    bulkInsert.setReturnType(new FullyQualifiedJavaType("java.lang.String"));

    try (var writer = new StringWriter()) {
      var template = configuration.getTemplate("bulkInsert.ftl", Locale.JAPAN, "UTF-8");
      var environment = template.createProcessingEnvironment(templateParam, writer);
      environment.setOutputEncoding("UTF-8");
      environment.process();

      bulkInsert.addBodyLine(writer.toString());
    } catch (Exception e) {
      return true;
    }

    topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.List"));
    topLevelClass.addMethod(bulkInsert);

    return true;
  }
}
