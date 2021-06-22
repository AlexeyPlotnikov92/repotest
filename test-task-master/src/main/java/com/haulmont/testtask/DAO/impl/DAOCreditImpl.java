package com.haulmont.testtask.DAO.impl;

import com.haulmont.testtask.DAO.DAOCredit;
import com.haulmont.testtask.Entity.Credit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class DAOCreditImpl implements DAOCredit {
    private final DataSource dataSource;

    public DAOCreditImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Credit> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query("select id, \n" +
                        "credit_limit,\n " +
                        "interest_rate,\n" +
                        "bank_id \n" +
                        "from credits",
                resultSet -> {
                    List<Credit> credits = new ArrayList<>();
                    while (resultSet.next()) {
                        credits.add(new Credit(
                                resultSet.getString(1),
                                resultSet.getInt(2),
                                resultSet.getInt(3),
                                resultSet.getString(4)
                        ));
                    }
                    return credits;
                }
        );
    }

    @Override
    public Credit findById(String id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query("select id, \n" +
                        "credit_limit,\n " +
                        "interest_rate, \n" +
                        "bank_id \n" +
                        "from credits\n" +
                        "where id = ? ",
                new Object[]{
                        id
                },
                new int[]{
                        Types.VARCHAR
                },
                resultSet -> {
                    if (resultSet.next()) {
                        return new Credit(
                                resultSet.getString(1),
                                resultSet.getInt(2),
                                resultSet.getInt(3),
                                resultSet.getString(4)
                        );
                    }
                    return null;
                });
    }

    @Override
    public Credit save(Credit credit) {
        if (credit.getCreditLimit() > 0 && credit.getInterestRate() >= 0) {
            if (credit.getId() == null) {
                Credit saveCredit = new Credit(UUID.randomUUID().toString(),
                        credit.getCreditLimit(),
                        credit.getInterestRate(),
                        credit.getBankId());
                return insertCredit(saveCredit);
            } else {
                return updateCredit(credit);
            }
        } else {
            throw new IllegalArgumentException("only positive number");
        }
    }

    private Credit insertCredit(Credit credit) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource);
        simpleJdbcInsert.withTableName("credits")
                .usingGeneratedKeyColumns()
                .usingColumns("id", "credit_limit", "interest_rate", "bank_id")
                .execute(Map.of(
                        "id", credit.getId(),
                        "credit_limit", credit.getCreditLimit(),
                        "interest_rate", credit.getInterestRate(),
                        "bank_id", 1
                ));
        log.info("insert credit");
        return credit;
    }

    private Credit updateCredit(Credit credit) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int updateCount = jdbcTemplate.update("update credits\n" +
                        "set credit_limit = ?,\n" +
                        "interest_rate = ?,\n" +
                        "bank_id = ?\n" +
                        "where id = ?",
                new Object[]{
                        credit.getCreditLimit(),
                        credit.getInterestRate(),
                        credit.getBankId(),
                        credit.getId()
                },
                new int[]{
                        Types.BIGINT,
                        Types.BIGINT,
                        Types.VARCHAR,
                        Types.VARCHAR
                }
        );
        if (updateCount == 0) {
            throw new IllegalStateException(String.format("Credit with id %s not found", credit.getId()));
        }
        return credit;
    }

    @Override
    public void delete(String id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int deletedRowCount = jdbcTemplate.update("delete from credits\n" +
                        "where id = ?",
                new Object[]{
                        id
                },
                new int[]{
                        Types.VARCHAR
                }
        );
        log.info("deleted row count {}", deletedRowCount);
    }

    @Override
    public List<Credit> findCreditsOfBank(String bankId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query("select id,\n" +
                        " credit_limit,\n" +
                        " interest_rate,\n" +
                        " bank_id\n" +
                        "from credits\n " +
                        "where bank_id = ?",
                new Object[]{
                        bankId
                },
                new int[]{
                        Types.VARCHAR
                },
                resultSet -> {
                    List<Credit> credits = new ArrayList<>();
                    while (resultSet.next()) {
                        credits.add(new Credit(
                                resultSet.getString(1),
                                resultSet.getInt(2),
                                resultSet.getInt(3),
                                resultSet.getString(4)
                        ));
                    }
                    return credits;
                }
        );
    }

    @Override
    public List<Credit> findCreditsWithoutBank(String bankId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query("select id,\n" +
                        " credit_limit,\n" +
                        " interest_rate,\n" +
                        " bank_id\n" +
                        "from credits\n " +
                        "where bank_id <> ?",
                new Object[]{
                        bankId
                },
                new int[]{
                        Types.VARCHAR
                },
                resultSet -> {
                    List<Credit> credits = new ArrayList<>();
                    while (resultSet.next()) {
                        credits.add(new Credit(
                                resultSet.getString(1),
                                resultSet.getInt(2),
                                resultSet.getInt(3),
                                resultSet.getString(4)
                        ));
                    }
                    return credits;
                }
        );
    }
}
