package org.mybatis.generator.plugins;

import freemarker.template.Configuration;
import java.io.StringWriter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.SneakyThrows;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

public class SelectiveSQLPlugin extends PluginAdapter {

  private final Configuration configuration;

  public SelectiveSQLPlugin() {
    var configuration = new Configuration(Configuration.VERSION_2_3_32);
    configuration.setClassForTemplateLoading(getClass(), "/templates");
    this.configuration = configuration;
  }

  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

  @Override
  public boolean clientGenerated(
      Interface interfaze, //
      IntrospectedTable introspectedTable) {
    var baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
    var sqlProvider =
        new FullyQualifiedJavaType(introspectedTable.getTableConfiguration().getSqlProviderName());

    var selectProvider = new FullyQualifiedJavaType("org.apache.ibatis.annotations.SelectProvider");
    interfaze.addImportedType(selectProvider);
    interfaze.addImportedType(new FullyQualifiedJavaType("java.util.List"));

    var selectBy =
        generateClientMethod(
            "selectBy",
            selectProvider,
            sqlProvider,
            baseRecordType,
            new FullyQualifiedJavaType("List<%s>".formatted(baseRecordType.getShortName())));
    interfaze.addMethod(selectBy);

    var deleteProvider = new FullyQualifiedJavaType("org.apache.ibatis.annotations.DeleteProvider");
    interfaze.addImportedType(deleteProvider);

    var deleteBy =
        generateClientMethod(
            "deleteBy",
            deleteProvider,
            sqlProvider,
            baseRecordType,
            new FullyQualifiedJavaType("int"));
    interfaze.addMethod(deleteBy);

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
    var tableName = introspectedTable.getTableConfiguration().getTableName();
    var baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
    var rows = introspectedTable.getAllColumns().stream().map(RowParam::of).toList();
    var templateParam =
        Map.of(
            "tableName", tableName,
            "rows", rows);

    try {
      var selectBy = generateProviderMethod("selectBy", baseRecordType, templateParam);
      topLevelClass.addMethod(selectBy);

      var deleteBy = generateProviderMethod("deleteBy", baseRecordType, templateParam);
      topLevelClass.addMethod(deleteBy);
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
}
