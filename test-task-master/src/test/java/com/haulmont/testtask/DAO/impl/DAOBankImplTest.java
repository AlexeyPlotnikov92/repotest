package com.haulmont.testtask.DAO.impl;

import com.haulmont.testtask.ApplicationTest;
import com.haulmont.testtask.DAO.DAOBank;
import com.haulmont.testtask.DAO.DAOClient;
import com.haulmont.testtask.DAO.DAOCredit;
import com.haulmont.testtask.Entity.Bank;
import com.haulmont.testtask.Entity.Client;
import com.haulmont.testtask.Entity.Credit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

class DAOBankImplTest extends ApplicationTest {

    @Autowired
    DAOBank daoBank;

    @Autowired
    DAOClient daoClient;

    @Autowired
    DAOCredit daoCredit;

    @Test
    void checkCrud() {
        int initialSize = daoBank.findAll().size();
        Random random = new Random();
        String expectedFoolName = UUID.randomUUID().toString();
        String expectedTelephoneNumber = UUID.randomUUID().toString();
        String expectedEMail = UUID.randomUUID().toString();
        int expectedPasspotrNumber = random.nextInt(1000000);
        Client client = daoClient.save(new Client(null, expectedFoolName, expectedTelephoneNumber, expectedEMail, expectedPasspotrNumber, "1"));

        Integer expectedCreditLimit = random.nextInt(100000);
        Integer expectedInterestRate = random.nextInt(100);
        Credit credit = daoCredit.save(new Credit(null, expectedCreditLimit, expectedInterestRate, "1"));

        String expectedBankName = UUID.randomUUID().toString();
        Bank bank = daoBank.save(new Bank(null, expectedBankName, List.of(client), List.of(credit)));
        Assertions.assertNotNull(bank.getId());
        Assertions.assertEquals(expectedBankName, bank.getName());
        Assertions.assertEquals(client, daoBank.findById(bank.getId()).getClients().get(0));
        Assertions.assertEquals(credit, daoBank.findById(bank.getId()).getCredits().get(0));
        Assertions.assertEquals(client.getBankId(), bank.getId());
        Assertions.assertEquals(credit.getBankId(), bank.getId());

        int sizeClients = bank.getClients().size();
        int sizeCredits = bank.getCredits().size();
        Client client1 = daoClient.save(new Client(null, expectedFoolName, expectedTelephoneNumber, expectedEMail, expectedPasspotrNumber + 1, "1"));
        Credit credit1 = daoCredit.save(new Credit(null, expectedCreditLimit, expectedInterestRate, "1"));
        bank = daoBank.save(new Bank(bank.getId(), expectedBankName, List.of(client, client1), List.of(credit, credit1)));
        Assertions.assertEquals(sizeClients + 1, daoBank.findById(bank.getId()).getClients().size());
        Assertions.assertEquals(sizeCredits + 1, daoBank.findById(bank.getId()).getCredits().size());

        Assertions.assertEquals(initialSize + 1, daoBank.findAll().size());

        Bank byId = daoBank.findById(bank.getId());
        Assertions.assertEquals(expectedBankName, byId.getName());

        String updatedBankName = UUID.randomUUID().toString();
        daoBank.save(new Bank(bank.getId(), updatedBankName, List.of(client, client1), List.of(credit, credit1)));
        Assertions.assertEquals(initialSize + 1, daoBank.findAll().size());
        Assertions.assertNotEquals(byId, daoBank.findById(bank.getId()));

        byId = daoBank.findById(bank.getId());
        Assertions.assertEquals(bank.getId(), byId.getId());
        Assertions.assertEquals(updatedBankName, byId.getName());

        daoBank.delete(bank.getId());
        Assertions.assertEquals(initialSize, daoBank.findAll().size());
    }

    @Test
    void checkCreditAndClientOfBank() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            String expectedFoolName = UUID.randomUUID().toString();
            String expectedTelephoneNumber = UUID.randomUUID().toString();
            String expectedEMail = UUID.randomUUID().toString();
            int expectedPasspotrNumber = random.nextInt(1000000);
            daoClient.save(new Client(null, expectedFoolName, expectedTelephoneNumber, expectedEMail, expectedPasspotrNumber, "1"));
        }
        for (int i = 0; i < 10; i++) {
            Integer expectedCreditLimit = random.nextInt(100000);
            Integer expectedInterestRate = random.nextInt(100);
            daoCredit.save(new Credit(null, expectedCreditLimit, expectedInterestRate, "1"));
        }
        List<Client> clients = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            clients.add(daoClient.findAll().get(i));
        }
        List<Credit> credits = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            credits.add(daoCredit.findAll().get(i));
        }
        String expectedBankName = UUID.randomUUID().toString();
        Bank bank = daoBank.save(new Bank(null, expectedBankName, clients, credits));
        Assertions.assertEquals(daoClient.findAll().size(), daoClient.findClientsOfBank(bank.getId()).size() + daoClient.findClientWithoutBank(bank.getId()).size());
        Assertions.assertEquals(daoCredit.findAll().size(), daoCredit.findCreditsOfBank(bank.getId()).size() + daoCredit.findCreditsWithoutBank(bank.getId()).size());
        daoBank.delete(bank.getId());
    }

}