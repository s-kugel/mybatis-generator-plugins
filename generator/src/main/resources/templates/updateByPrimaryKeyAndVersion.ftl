
<#list primaryKeys as primaryKey>
        Objects.requireNonNull(row.get${primaryKey.javaProperty?cap_first}());
</#list>
        Objects.requireNonNull(row.getVersion());

        SQL sql = new SQL();

        sql.UPDATE("${tableName}");

<#list rows as row>
        if (row.get${row.javaProperty?cap_first}() != null) {
            sql.SET("${row.columnName} = <#noparse>#</#noparse>{${row.javaProperty}, jdbcType=${row.jdbcType}}");
        }
</#list>

<#if version.jdbcType == "INTEGER">
    <#noparse>
        sql.SET("version = #{version, jdbcType=INTEGER} + 1");
    </#noparse>
</#if>
<#if version.jdbcType == "VARCHAR">
    <#noparse>
        sql.SET("version = #{%s, jdbcType=VARCHAR}".formatted(UUID.randomUUID().toString()));
    </#noparse>
</#if>

<#list primaryKeys as primaryKey>
        sql.WHERE("${primaryKey.columnName} = <#noparse>#</#noparse>{${primaryKey.javaProperty}, jdbcType=${primaryKey.jdbcType}}");
</#list>
        sql.WHERE("${version.columnName} = <#noparse>#</#noparse>{${version.javaProperty}, jdbcType=${version.jdbcType}}");

        return sql.toString();