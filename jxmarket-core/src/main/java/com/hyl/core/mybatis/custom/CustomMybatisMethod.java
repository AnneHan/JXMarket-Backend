package com.hyl.core.mybatis.custom;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

public class CustomMybatisMethod {


    public static class DeleteAll extends AbstractMethod {
        @Override
        public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
            /* 执行 SQL ，动态 SQL 参考类 SqlMethod */
            String sql = "delete from " + tableInfo.getTableName();
            /* mapper 接口方法名一致 */
            String method = "deleteAll";
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
            return this.addDeleteMappedStatement(mapperClass, method, sqlSource);
        }
    }

    public static class BatchInsert extends AbstractMethod {

        @Override
        public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
            final String sql = "<script>insert into %s %s values %s</script>";
            final String fieldSql = prepareFieldSql(tableInfo);
            final String valueSql = prepareValuesSqlForMysqlBatch(tableInfo);
            final String sqlResult = String.format(sql, tableInfo.getTableName(), fieldSql, valueSql);
            SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlResult, modelClass);
            return this.addInsertMappedStatement(mapperClass, modelClass, "insertAllBatch", sqlSource, new NoKeyGenerator(), null, null);
        }

        private String prepareFieldSql(TableInfo tableInfo) {
            StringBuilder fieldSql = new StringBuilder();
            fieldSql.append(tableInfo.getKeyColumn()).append(",");
            tableInfo.getFieldList().forEach(x -> {
                fieldSql.append(x.getColumn()).append(",");
            });
            fieldSql.delete(fieldSql.length() - 1, fieldSql.length());
            fieldSql.insert(0, "(");
            fieldSql.append(")");
            return fieldSql.toString();
        }

        private String prepareValuesSqlForMysqlBatch(TableInfo tableInfo) {
            final StringBuilder valueSql = new StringBuilder();
            valueSql.append("<foreach collection=\"list\" item=\"item\" index=\"index\" open=\"(\" separator=\"),(\" close=\")\">");
            valueSql.append("#{item.").append(tableInfo.getKeyProperty()).append("},");
            tableInfo.getFieldList().forEach(x -> valueSql.append("#{item.").append(x.getProperty()).append("},"));
            valueSql.delete(valueSql.length() - 1, valueSql.length());
            valueSql.append("</foreach>");
            return valueSql.toString();
        }
    }


}
