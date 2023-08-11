package org.mybatis.generator.plugins;

import java.util.List;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;

public class TruncateSQLPlugin extends PluginAdapter {

  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

  @Override
  public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
    var tableName = PluginUtils.tableName(introspectedTable);

    var method = new Method("truncate");
    method.setAbstract(true);
    method.setDefault(false);
    method.addAnnotation("@Delete(\"TRUNCATE TABLE %s\")".formatted(tableName));
    method.setReturnType(PluginUtils.INTEGER);

    interfaze.addMethod(method);

    interfaze.addImportedType(PluginUtils.DELETE_ANNOTATION);

    return true;
  }
}
