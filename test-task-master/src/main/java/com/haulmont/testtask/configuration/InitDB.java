package com.haulmont.testtask.configuration;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Component
public class InitDB {
    private final DataSource dataSource;

    public InitDB(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @PostConstruct
    public void init() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS clients (\n" +
                "   id VARCHAR(50) NOT NULL, \n" +
                "   fool_name VARCHAR(50) NOT NULL,\n" +
                "   telephone_number VARCHAR(50) NOT NULL,\n" +
                "    email VARCHAR(50) NOT NULL,\n" +
                "    passport INT NOT NULL UNIQUE,\n" +
                "    bank_id VARCHAR(50), \n" +
                "    PRIMARY KEY (id) " +
                ");");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS credits (\n" +
                "   id VARCHAR(50) NOT NULL,\n" +
                "   credit_limit INT NOT NULL,\n" +
                "   interest_rate INT NOT NULL,\n" +
                "   bank_id VARCHAR(50),\n" +
                "    PRIMARY KEY (id) " +
                ");");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS offers (\n" +
                "   id VARCHAR(50) NOT NULL,\n" +
                "   client_id VARCHAR(50) NOT NULL,\n" +
                "   credit_id VARCHAR(50) NOT NULL,\n" +
                "   credit_amount INT NOT NULL, \n" +
                "   PRIMARY KEY (id),\n " +
                "   CONSTRAINT client_id_fk \n " +
                "   FOREIGN KEY (client_id)  REFERENCES clients (id), \n" +
                "   CONSTRAINT credit_id_fk \n" +
                "   FOREIGN KEY (credit_id)  REFERENCES credits (id)" +
                ");");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS banks (\n" +
                "  id VARCHAR(50) NOT NULL,\n" +
                "  name VARCHAR(50) NOT NULL," +
                "    PRIMARY KEY (id) " +
                ");");
    }
}
