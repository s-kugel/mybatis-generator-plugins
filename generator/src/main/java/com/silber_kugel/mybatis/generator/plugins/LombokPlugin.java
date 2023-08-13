package com.silber_kugel.mybatis.generator.plugins;

import java.util.List;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

public class LombokPlugin extends PluginAdapter {

  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

  @Override
  public boolean modelGetterMethodGenerated(
      Method method,
      TopLevelClass topLevelClass,
      IntrospectedColumn introspectedColumn,
      IntrospectedTable introspectedTable,
      ModelClassType modelClassType) {
    return false;
  }

  @Override
  public boolean modelSetterMethodGenerated(
      Method method,
      TopLevelClass topLevelClass,
      IntrospectedColumn introspectedColumn,
      IntrospectedTable introspectedTable,
      ModelClassType modelClassType) {
    return false;
  }

  @Override
  public boolean modelBaseRecordClassGenerated(
      TopLevelClass topLevelClass, //
      IntrospectedTable introspectedTable) {
    topLevelClass.addImportedType("lombok.Getter");
    topLevelClass.addAnnotation("@Getter");

    topLevelClass.addImportedType("lombok.Setter");
    topLevelClass.addAnnotation("@Setter");

    topLevelClass.addImportedType("lombok.EqualsAndHashCode");
    topLevelClass.addAnnotation("@EqualsAndHashCode");

    topLevelClass.addImportedType("lombok.ToString");
    topLevelClass.addAnnotation("@ToString");

    topLevelClass.addImportedType("lombok.NoArgsConstructor");
    topLevelClass.addAnnotation("@NoArgsConstructor");

    topLevelClass.addImportedType("lombok.experimental.Accessors");
    topLevelClass.addAnnotation("@Accessors(chain = true)");

    return true;
  }
}
