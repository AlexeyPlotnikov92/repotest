package com.haulmont.testtask.DAO.impl;

import com.haulmont.testtask.ApplicationTest;
import com.haulmont.testtask.DAO.DAOClient;
import com.haulmont.testtask.Entity.Client;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;
import java.util.UUID;

class DAOClientImplTest extends ApplicationTest {

    @Autowired
    DAOClient daoClient;

    @Test
    void checkClientsCrud() {
        int initialSize = daoClient.findAll().size();
        Random random = new Random();
        String expectedFoolName = UUID.randomUUID().toString();
        String expectedTelephoneNumber = UUID.randomUUID().toString();
        String expectedEMail = UUID.randomUUID().toString();
        Integer expectedPasspotrNumber = random.nextInt(1000000);
        Client client = daoClient.save(new Client(null, expectedFoolName, expectedTelephoneNumber, expectedEMail, expectedPasspotrNumber, "1"));
        Assertions.assertNotNull(client.getId());
        Assertions.assertEquals(expectedFoolName, client.getFoolName());
        Assertions.assertEquals(expectedTelephoneNumber, client.getTelephoneNumber());
        Assertions.assertEquals(expectedEMail, client.getEMailAdress());
        Assertions.assertEquals(expectedPasspotrNumber, client.getPassportNumber());

        Assertions.assertEquals(initialSize + 1, daoClient.findAll().size());

        Client byId = daoClient.findById(client.getId());
        Assertions.assertEquals(client.getId(), byId.getId());
        Assertions.assertEquals(expectedFoolName, byId.getFoolName());
        Assertions.assertEquals(expectedTelephoneNumber, byId.getTelephoneNumber());
        Assertions.assertEquals(expectedEMail, byId.getEMailAdress());
        Assertions.assertEquals(expectedPasspotrNumber, byId.getPassportNumber());

        String updatedFoolName = UUID.randomUUID().toString();
        String updatedTelephoneNumber = UUID.randomUUID().toString();
        String updatedEMail = UUID.randomUUID().toString();
        Integer updatedPassportNumber = random.nextInt(1000000);
        daoClient.save(new Client(client.getId(), updatedFoolName, updatedTelephoneNumber, updatedEMail, updatedPassportNumber, "1"));
        Assertions.assertEquals(initialSize + 1, daoClient.findAll().size());
        Assertions.assertNotEquals(byId, daoClient.findById(client.getId()));

        byId = daoClient.findById(client.getId());
        Assertions.assertEquals(client.getId(), byId.getId());
        Assertions.assertEquals(updatedFoolName, byId.getFoolName());
        Assertions.assertEquals(updatedTelephoneNumber, byId.getTelephoneNumber());
        Assertions.assertEquals(updatedEMail, byId.getEMailAdress());
        Assertions.assertEquals(updatedPassportNumber, byId.getPassportNumber());

        daoClient.delete(client.getId());
        Assertions.assertEquals(initialSize, daoClient.findAll().size());
    }

}