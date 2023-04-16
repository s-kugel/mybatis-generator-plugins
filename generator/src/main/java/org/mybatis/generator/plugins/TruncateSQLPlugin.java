package org.mybatis.generator.plugins;

import java.util.List;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;

public class TruncateSQLPlugin extends PluginAdapter {

  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

  @Override
  public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
    var tableName = introspectedTable.getTableConfiguration().getTableName();

    var truncate = new Method("truncate");
    truncate.setAbstract(true);
    truncate.setDefault(false);
    truncate.addAnnotation("@Delete(\"TRUNCATE TABLE %s\")".formatted(tableName));
    truncate.setReturnType(new FullyQualifiedJavaType("int"));

    interfaze.addMethod(truncate);

    var deleteAnnotation = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Delete");
    interfaze.addImportedType(deleteAnnotation);

    return true;
  }
}
