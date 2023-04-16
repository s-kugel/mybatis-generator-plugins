
<#list primaryKeys as primaryKey>
        Objects.requireNonNull(row.get${primaryKey.javaProperty?cap_first}());
</#list>
        Objects.requireNonNull(row.getVersion());

        SQL sql = new SQL();

        sql.UPDATE("${tableName}");

<#list rows as row>
        if (row.get${row.javaProperty?cap_first}() != null) {
            sql.SET("${row.columnName} = <#noparse>#</#noparse>{row.${row.javaProperty}, jdbcType=${row.jdbcType}");
        }
</#list>

<#if version.jdbcType == "INTEGER">
    <#noparse>
        sql.SET("version = #{%d, jdbcType=INTEGER}".formatted(row.getVersion() + 1));
    </#noparse>
</#if>
<#if version.jdbcType == "VARCHAR">
    <#noparse>
        sql.SET("version = #{%d, jdbcType=VARCHAR}".formatted(UUID.randomUUID().toString()));
    </#noparse>
</#if>

<#list primaryKeys as primaryKey>
        sql.WHERE("user_id = <#noparse>#</#noparse>{row.${primaryKey.javaProperty}, jdbcType=${primaryKey.jdbcType}");
</#list>
        sql.WHERE("${version.columnName} = <#noparse>#</#noparse>{row.${version.javaProperty}, jdbcType=${version.jdbcType}");

        return sql.toString();