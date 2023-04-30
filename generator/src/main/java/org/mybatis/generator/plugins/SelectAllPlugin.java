package org.mybatis.generator.plugins;

import java.util.List;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;

public class SelectAllPlugin extends PluginAdapter {

  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

  @Override
  public boolean clientGenerated(
      Interface interfaze, //
      IntrospectedTable introspectedTable) {
    var tableName = introspectedTable.getTableConfiguration().getTableName();
    var baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());

    interfaze.addImportedType(new FullyQualifiedJavaType("java.util.List"));
    interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Select"));
    interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.cursor.Cursor"));

    var selectAll = new Method("selectAll");
    selectAll.setAbstract(true);
    selectAll.setDefault(false);
    selectAll.setVisibility(JavaVisibility.PUBLIC);
    selectAll.addAnnotation("@Select(\"SELECT * FROM %s\")".formatted(tableName));
    selectAll.setReturnType(
        new FullyQualifiedJavaType("List<%s>".formatted(baseRecordType.getShortName())));

    interfaze.addMethod(selectAll);

    var selectAllWithCursor = new Method("selectAllWithCursor");
    selectAllWithCursor.setAbstract(true);
    selectAllWithCursor.setDefault(false);
    selectAllWithCursor.setVisibility(JavaVisibility.PUBLIC);
    selectAllWithCursor.addAnnotation("@Select(\"SELECT * FROM %s\")".formatted(tableName));
    selectAllWithCursor.setReturnType(
        new FullyQualifiedJavaType("Cursor<%s>".formatted(baseRecordType.getShortName())));

    interfaze.addMethod(selectAllWithCursor);

    return true;
  }
}
