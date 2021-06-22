package com.haulmont.testtask.DAO.impl;

import com.haulmont.testtask.DAO.DAOClient;
import com.haulmont.testtask.Entity.Client;
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
public class DAOClientImpl implements DAOClient {
    private final DataSource dataSource;

    public DAOClientImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Client> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query("select id, \n" +
                        "fool_name,\n " +
                        "telephone_number, \n" +
                        "email,\n" +
                        "passport,\n" +
                        "bank_id \n" +
                        "from clients",
                resultSet -> {
                    List<Client> clients = new ArrayList<>();
                    while (resultSet.next()) {
                        clients.add(new Client(
                                resultSet.getString(1),
                                resultSet.getString(2),
                                resultSet.getString(3),
                                resultSet.getString(4),
                                resultSet.getInt(5),
                                resultSet.getString(6)
                        ));
                    }
                    return clients;
                }
        );
    }


    @Override
    public Client findById(String id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query("select id,\n" +
                        " fool_name,\n" +
                        " telephone_number,\n" +
                        " email,\n" +
                        " passport,\n" +
                        "bank_id \n" +
                        "from clients\n " +
                        "where id = ? ",
                new Object[]{
                        id
                },
                new int[]{
                        Types.VARCHAR
                },
                resultSet -> {
                    if (resultSet.next()) {
                        return new Client(
                                resultSet.getString(1),
                                resultSet.getString(2),
                                resultSet.getString(3),
                                resultSet.getString(4),
                                resultSet.getInt(5),
                                resultSet.getString(6)
                        );
                    }
                    return null;
                });
    }

    @Override
    public Client save(Client client) {
        if (client.getId() == null) {
            Client saveClient = new Client(UUID.randomUUID().toString(),
                    client.getFoolName(),
                    client.getTelephoneNumber(),
                    client.getEMailAdress(),
                    client.getPassportNumber(),
                    client.getBankId());
            return insertClient(saveClient);
        } else {
            return updateClient(client);
        }
    }

    private Client insertClient(Client client) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource);
        simpleJdbcInsert.withTableName("clients")
                .usingGeneratedKeyColumns()
                .usingColumns("id", "fool_name", "telephone_number", "email", "passport", "bank_id")
                .execute(Map.of(
                        "id", client.getId(),
                        "fool_name", client.getFoolName(),
                        "telephone_number", client.getTelephoneNumber(),
                        "email", client.getEMailAdress(),
                        "passport", client.getPassportNumber(),
                        "client_id", 1
                ));
        log.info("inserted client");
        return client;
    }

    private Client updateClient(Client client) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int updateCount = jdbcTemplate.update("update clients\n" +
                        "set fool_name = ?,\n" +
                        "telephone_number = ?,\n" +
                        "email = ?,\n" +
                        "passport = ?,\n" +
                        "bank_id = ? \n" +
                        "where id = ?",
                new Object[]{
                        client.getFoolName(),
                        client.getTelephoneNumber(),
                        client.getEMailAdress(),
                        client.getPassportNumber(),
                        client.getBankId(),
                        client.getId()
                },
                new int[]{
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.BIGINT,
                        Types.VARCHAR,
                        Types.VARCHAR
                }
        );
        if (updateCount == 0) {
            throw new IllegalStateException(String.format("Client with id %s not found", client.getId()));
        }
        return client;
    }

    @Override
    public void delete(String id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int deletedRowCount = jdbcTemplate.update("delete from clients\n" +
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
    public List<Client> findClientsOfBank(String bankId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query("select id,\n" +
                        " fool_name,\n" +
                        " telephone_number,\n" +
                        " email,\n" +
                        " passport,\n" +
                        "bank_id \n" +
                        "from clients\n " +
                        "where bank_id = ?",
                new Object[]{
                        bankId
                },
                new int[]{
                        Types.VARCHAR
                },
                resultSet -> {
                    List<Client> clients = new ArrayList<>();
                    while (resultSet.next()) {
                        clients.add(new Client(
                                resultSet.getString(1),
                                resultSet.getString(2),
                                resultSet.getString(3),
                                resultSet.getString(4),
                                resultSet.getInt(5),
                                resultSet.getString(6)
                        ));
                    }
                    return clients;
                }
        );
    }


    @Override
    public List<Client> findClientWithoutBank(String id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query("select id,\n" +
                        " fool_name,\n" +
                        " telephone_number,\n" +
                        " email,\n" +
                        " passport,\n" +
                        "bank_id \n" +
                        "from clients\n " +
                        "where bank_id is null or bank_id <> ?",
                new Object[]{
                        id
                },
                new int[]{
                        Types.VARCHAR
                },
                resultSet -> {
                    List<Client> clients = new ArrayList<>();
                    while (resultSet.next()) {
                        clients.add(new Client(
                                resultSet.getString(1),
                                resultSet.getString(2),
                                resultSet.getString(3),
                                resultSet.getString(4),
                                resultSet.getInt(5),
                                resultSet.getString(6)
                        ));
                    }
                    return clients;
                }
        );
    }

}