SQL sql = new SQL();

<#list rows as row>
        sql.SELECT("${row.columnName}");
</#list>

        sql.FROM("${tableName}");

<#list rows as row>
        if (row.get${row.javaProperty?cap_first}() != null) {
            sql.WHERE("${row.columnName} = <#noparse>#</#noparse>{row.${row.javaProperty}, jdbcType=${row.jdbcType}");
        }
</#list>

        return sql.toString();