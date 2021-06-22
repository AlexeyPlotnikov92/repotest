package com.haulmont.testtask.DAO;

import com.haulmont.testtask.Entity.Bank;

import java.util.List;

public interface DAOBank {
    List<Bank> findAll();

    Bank findById(String id);

    Bank save(Bank bank);

    void delete(String id);

}
