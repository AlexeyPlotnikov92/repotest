package com.haulmont.testtask.DAO.impl;

import com.haulmont.testtask.DAO.DAOBank;
import com.haulmont.testtask.DAO.DAOClient;
import com.haulmont.testtask.DAO.DAOCredit;
import com.haulmont.testtask.Entity.Bank;
import com.haulmont.testtask.Entity.Client;
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
public class DAOBankImpl implements DAOBank {
    private final DataSource dataSource;
    private final DAOClient daoClient;
    private final DAOCredit daoCredit;

    public DAOBankImpl(DataSource dataSource, DAOClient daoClient, DAOCredit daoCredit) {
        this.dataSource = dataSource;
        this.daoClient = daoClient;
        this.daoCredit = daoCredit;
    }

    @Override
    public List<Bank> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query("select id, \n" +
                        "name\n" +
                        "from banks",
                resultSet -> {
                    List<Bank> banks = new ArrayList<>();
                    while (resultSet.next()) {
                        banks.add(new Bank(
                                resultSet.getString(1),
                                resultSet.getString(2),
                                daoClient.findClientsOfBank(resultSet.getString(1)),
                                daoCredit.findCreditsOfBank(resultSet.getString(1))
                        ));
                    }
                    return banks;
                }
        );
    }

    @Override
    public Bank findById(String id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query("select id, \n" +
                        "name\n" +
                        "from banks\n" +
                        "where id = ?",
                new Object[]{
                        id
                },
                new int[]{
                        Types.VARCHAR
                },
                resultSet -> {
                    if (resultSet.next()) {
                        return new Bank(
                                resultSet.getString(1),
                                resultSet.getString(2),
                                daoClient.findClientsOfBank(resultSet.getString(1)),
                                daoCredit.findCreditsOfBank(resultSet.getString(1))
                        );
                    }
                    return null;
                });
    }

    @Override
    public Bank save(Bank bank) {
        if (bank.getId() == null) {
            String bankId = UUID.randomUUID().toString();
            Bank saveBank = new Bank(
                    bankId,
                    bank.getName(),
                    bank.getClients(),
                    bank.getCredits()
            );
            for (Client client : bank.getClients()) {
                client.setBankId(bankId);
                daoClient.save(client);
                log.info("client in bank {}", client.getBankId());
                log.info("return client from daobank{}", client.getFoolName());
            }
            for (Credit credit : bank.getCredits()) {
                credit.setBankId(bankId);
                daoCredit.save(credit);
            }
            if (findAll().size() == 0) {
                return insertBank(saveBank);
            } else {
                throw new IllegalArgumentException("extra bank");
            }
        } else {
            for (Client client : bank.getClients()) {
                client.setBankId(bank.getId());
                daoClient.save(client);
            }
            for (Credit credit : bank.getCredits()) {
                credit.setBankId(bank.getId());
                daoCredit.save(credit);
            }
            return updateBank(bank);
        }
    }

    private Bank insertBank(Bank bank) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource);
        simpleJdbcInsert.withTableName("banks")
                .usingGeneratedKeyColumns()
                .usingColumns("id", "name")
                .execute(Map.of(
                        "id", bank.getId(),
                        "name", bank.getName()
                ));
        log.info("insert bank");
        return bank;
    }

    private Bank updateBank(Bank bank) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int updateCount = jdbcTemplate.update("update banks\n" +
                        "set name = ?\n" +
                        "where id = ?",
                new Object[]{
                        bank.getName(),
                        bank.getId()
                },
                new int[]{
                        Types.VARCHAR,
                        Types.VARCHAR,
                }
        );
        if (updateCount == 0) {
            throw new IllegalStateException(String.format("Bank with id %s not found", bank.getId()));
        }
        return bank;
    }

    @Override
    public void delete(String id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int deletedRowCount = jdbcTemplate.update("delete from banks\n" +
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


}
