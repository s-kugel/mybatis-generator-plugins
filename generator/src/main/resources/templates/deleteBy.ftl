SQL sql = new SQL();

        sql.DELETE_FROM("${tableName}");

<#list rows as row>
        if (row.get${row.javaProperty?cap_first}() != null) {
            sql.WHERE("${row.columnName} = <#noparse>#</#noparse>{${row.javaProperty}, jdbcType=${row.jdbcType}}");
        }
</#list>

        return sql.toString();