package org.mybatis.generator.plugins;

import java.util.List;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;

public class SelectAllPlugin extends PluginAdapter {

  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

  @Override
  public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
    var tableName = PluginUtils.tableName(introspectedTable);

    {
      var method = new Method("selectAll");
      method.setAbstract(true);
      method.setDefault(false);
      method.setVisibility(JavaVisibility.PUBLIC);
      method.addAnnotation("@Select(\"SELECT * FROM %s\")".formatted(tableName));
      method.setReturnType(PluginUtils.listBaseRecordType(introspectedTable));

      interfaze.addMethod(method);
    }

    {
      var method = new Method("selectAllWithCursor");
      method.setAbstract(true);
      method.setDefault(false);
      method.setVisibility(JavaVisibility.PUBLIC);
      method.addAnnotation("@Select(\"SELECT * FROM %s\")".formatted(tableName));
      method.setReturnType(PluginUtils.cursorBaseRecordType(introspectedTable));

      interfaze.addMethod(method);
    }

    interfaze.addImportedType(PluginUtils.LIST);
    interfaze.addImportedType(PluginUtils.SELECT_ANNOTATION);
    interfaze.addImportedType(PluginUtils.CURSOR);

    return true;
  }
}
