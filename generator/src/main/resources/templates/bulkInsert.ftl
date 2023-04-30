Objects.requireNonNull(rows);

        SQL sql = new SQL();

        sql.INSERT_INTO("${tableName}");

        sql.INTO_COLUMNS(
<#list rows as row>
            "${row.columnName}"<#sep>,</#sep>
</#list>
        );

        for (int i = 0; i < rows.size(); i++) {
            sql.INTO_VALUES(
<#list rows as row>
                "<#noparse>#</#noparse>{rows[%d].${row.javaProperty}, jdbcType=${row.jdbcType}}".formatted(i)<#sep>,</#sep>
</#list>
            );

            if (i < rows.size() - 1) {
                sql.ADD_ROW();
            }
        }

        return sql.toString();