
<#list primaryKeys as primaryKey>
        Objects.requireNonNull(row.get${primaryKey.javaProperty?cap_first}());
</#list>
        Objects.requireNonNull(row.getVersion());

        SQL sql = new SQL();

        sql.DELETE_FROM("${tableName}");

<#list primaryKeys as primaryKey>
        sql.WHERE("user_id = <#noparse>#</#noparse>{row.${primaryKey.javaProperty}, jdbcType=${primaryKey.jdbcType}");
</#list>
        sql.WHERE("${version.columnName} = <#noparse>#</#noparse>{row.${version.javaProperty}, jdbcType=${version.jdbcType}");

        return sql.toString();