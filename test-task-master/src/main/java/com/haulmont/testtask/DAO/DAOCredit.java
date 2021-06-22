package com.haulmont.testtask.DAO;

import com.haulmont.testtask.Entity.Credit;

import java.util.List;

public interface DAOCredit {
    List<Credit> findAll();

    Credit findById(String id);

    Credit save(Credit credit);

    void delete(String id);

    List<Credit> findCreditsOfBank(String id);

    List<Credit> findCreditsWithoutBank(String id);
}
