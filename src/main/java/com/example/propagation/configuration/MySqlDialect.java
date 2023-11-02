package com.example.propagation.configuration;


import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.Oracle12cDialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class MySqlDialect extends MySQLDialect {

    public MySqlDialect() {
        super();
        //this.appendDatetimeFormat();
//        registerFunction("json_value", new StandardSQLFunction("json_value", StandardBasicTypes.STRING));
//        registerFunction("json_exists", new StandardSQLFunction("json_exists", StandardBasicTypes.STRING));
//        registerFunction("json_query", new StandardSQLFunction("json_query", StandardBasicTypes.STRING));
//        registerFunction("json_table", new StandardSQLFunction("json_table", StandardBasicTypes.STRING));
    }
}
