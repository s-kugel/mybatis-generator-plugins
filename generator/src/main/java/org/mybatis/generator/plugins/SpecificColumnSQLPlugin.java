package org.mybatis.generator.plugins;

import java.util.List;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;

public class SpecificColumnSQLPlugin extends PluginAdapter {

  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

  @Override
  public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
    var tableName = PluginUtils.tableName(introspectedTable);

    for (var column : introspectedTable.getAllColumns()) {
      var columnName = column.getActualColumnName();
      var javaProperty = column.getJavaProperty();
      var javaPropertyUpperCaseFirst =
          javaProperty.substring(0, 1).toUpperCase() + javaProperty.substring(1);
      var jdbcType = column.getJdbcTypeName();
      var javaType = column.getFullyQualifiedJavaType();

      {
        var selectBy = new Method("selectBy%s".formatted(javaPropertyUpperCaseFirst));
        selectBy.setDefault(false);
        selectBy.setAbstract(true);
        selectBy.addParameter(new Parameter(javaType, javaProperty));
        selectBy.addAnnotation(
            "@Select(\"SELECT * FROM %s WHERE %s = #{%s,jdbcType=%s}\")"
                .formatted(tableName, columnName, javaProperty, jdbcType));
        selectBy.setReturnType(PluginUtils.listBaseRecordType(introspectedTable));

        interfaze.addMethod(selectBy);
      }

      {
        var countBy = new Method("countBy%s".formatted(javaPropertyUpperCaseFirst));
        countBy.setDefault(false);
        countBy.setAbstract(true);
        countBy.addParameter(new Parameter(javaType, javaProperty));
        countBy.addAnnotation(
            "@Select(\"SELECT COUNT(*) FROM %s WHERE %s = #{%s,jdbcType=%s}\")"
                .formatted(tableName, columnName, javaProperty, jdbcType));
        countBy.setReturnType(PluginUtils.INTEGER);

        interfaze.addMethod(countBy);
      }

      {
        var deleteBy = new Method("deleteBy%s".formatted(javaPropertyUpperCaseFirst));
        deleteBy.setDefault(false);
        deleteBy.setAbstract(true);
        deleteBy.addParameter(new Parameter(javaType, javaProperty));
        deleteBy.addAnnotation(
            "@Delete(\"DELETE FROM %s WHERE %s = #{%s,jdbcType=%s}\")"
                .formatted(tableName, columnName, javaProperty, jdbcType));
        deleteBy.setReturnType(PluginUtils.INTEGER);

        interfaze.addMethod(deleteBy);
      }
    }

    return true;
  }
}
