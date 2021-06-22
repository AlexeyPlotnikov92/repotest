package com.haulmont.testtask.DAO;

import com.haulmont.testtask.Entity.Bank;
import com.haulmont.testtask.Entity.Client;

import java.util.List;

public interface DAOClient {
    List<Client> findAll();

    Client findById(String id);

    Client save(Client client);

    void delete(String id);

    List<Client> findClientsOfBank(String id);

    List<Client> findClientWithoutBank(String id);
}
