package com.haulmont.testtask.DAO.impl;

import com.haulmont.testtask.DAO.DAOOffer;
import com.haulmont.testtask.Entity.Client;
import com.haulmont.testtask.Entity.Credit;
import com.haulmont.testtask.Entity.Offer;
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
public class DAOOfferImpl implements DAOOffer {
    private final DataSource dataSource;

    public DAOOfferImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Offer> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query("select o.id, \n" +
                        "l.id l_id, l.fool_name, \n " +
                        "l.telephone_number, \n" +
                        "l.email, l.passport, \n" +
                        "l.bank_id, \n" +
                        "r.id r_id, r.credit_limit, \n" +
                        "r.interest_rate, \n" +
                        "r.bank_id ,\n" +
                        "o.credit_amount \n" +
                        "from offers o \n" +
                        "join clients l on o.client_id = l.id \n" +
                        "join credits r on o.credit_id = r.id \n",
                resultSet -> {
                    List<Offer> offers = new ArrayList<>();
                    while (resultSet.next()) {
                        offers.add(new Offer(
                                resultSet.getString(1),
                                new Client(resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getInt(6), resultSet.getString(7)),
                                new Credit(resultSet.getString(8), resultSet.getInt(9), resultSet.getInt(10), resultSet.getString(7)),
                                resultSet.getInt(12)
                        ));
                    }
                    return offers;
                }
        );
    }

    @Override
    public Offer findById(String id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query("select o.id, \n" +
                        "l.id l_id, l.fool_name, \n " +
                        "l.telephone_number, \n" +
                        "l.email, l.passport, \n" +
                        "l.bank_id, \n" +
                        "r.id r_id, r.credit_limit, \n" +
                        "r.interest_rate, \n" +
                        "r.bank_id ,\n" +
                        "o.credit_amount \n" +
                        "from offers o \n" +
                        "join clients l on o.client_id = l.id \n" +
                        "join credits r on o.credit_id = r.id \n" +
                        "where o.id = ?",
                new Object[]{
                        id
                },
                new int[]{
                        Types.VARCHAR
                },
                resultSet -> {
                    if (resultSet.next()) {
                        return new Offer(
                                resultSet.getString(1),
                                new Client(resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getInt(6), resultSet.getString(7)),
                                new Credit(resultSet.getString(8), resultSet.getInt(9), resultSet.getInt(10), resultSet.getString(7)),
                                resultSet.getInt(12)
                        );
                    }
                    return null;
                });
    }

    @Override
    public Offer save(Offer offer) {
        if (offer.getCreditAmount() <= offer.getCredit().getCreditLimit()) {
            if (offer.getId() == null) {
                Offer saveOffer = new Offer(
                        UUID.randomUUID().toString(),
                        offer.getClient(),
                        offer.getCredit(),
                        offer.getCreditAmount());
                return insertOffer(saveOffer);
            } else {
                return updateOffer(offer);
            }
        } else {
            throw new IllegalStateException("unexpected credit amount");
        }
    }

    private Offer insertOffer(Offer offer) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource);
        simpleJdbcInsert.withTableName("offers")
                .usingGeneratedKeyColumns()
                .usingColumns("id", "client_id", "credit_id", "credit_amount")
                .execute(Map.of(
                        "id", offer.getId(),
                        "client_id", offer.getClient().getId(),
                        "credit_id", offer.getCredit().getId(),
                        "credit_amount", offer.getCreditAmount()
                ));
        log.info("insert offer");
        return offer;
    }

    private Offer updateOffer(Offer offer) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int updateCount = jdbcTemplate.update("update offers\n" +
                        "set client_id = ?,\n" +
                        "credit_id = ?,\n" +
                        "credit_amount = ?\n" +
                        "where id = ?",
                new Object[]{
                        offer.getClient().getId(),
                        offer.getCredit().getId(),
                        offer.getCreditAmount(),
                        offer.getId()
                },
                new int[]{
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.BIGINT,
                        Types.VARCHAR
                }
        );
        if (updateCount == 0) {
            throw new IllegalStateException(String.format("Offer with id %s not found", offer.getId()));
        }

        return offer;
    }


    @Override
    public void delete(String id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int deletedRowCount = jdbcTemplate.update("delete from offers\n" +
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
