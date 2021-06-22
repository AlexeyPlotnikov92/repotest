package com.haulmont.testtask.DAO.impl;

import com.haulmont.testtask.ApplicationTest;
import com.haulmont.testtask.DAO.DAOCredit;
import com.haulmont.testtask.Entity.Credit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;

class DAOCreditImplTest extends ApplicationTest {

    @Autowired
    DAOCredit daoCredit;

    @Test
    void checkCrud() {
        int initialSize = daoCredit.findAll().size();
        Random random = new Random();
        Integer expectedCreditLimit = random.nextInt(100000);
        Integer expectedInterestRate = random.nextInt(100);
        Credit credit = daoCredit.save(new Credit(null, expectedCreditLimit, expectedInterestRate, "1"));
        Assertions.assertNotNull(credit.getId());
        Assertions.assertEquals(expectedCreditLimit, credit.getCreditLimit());
        Assertions.assertEquals(expectedInterestRate, credit.getInterestRate());

        Assertions.assertEquals(initialSize + 1, daoCredit.findAll().size());

        Credit byId = daoCredit.findById(credit.getId());
        Assertions.assertEquals(credit.getId(), byId.getId());
        Assertions.assertEquals(expectedCreditLimit, byId.getCreditLimit());
        Assertions.assertEquals(expectedInterestRate, byId.getInterestRate());

        Integer updatedCreditLimit = random.nextInt(1000000);
        Integer updatedInterestRate = random.nextInt(100);
        daoCredit.save(new Credit(credit.getId(), updatedCreditLimit, updatedInterestRate, "1"));
        Assertions.assertEquals(initialSize + 1, daoCredit.findAll().size());
        Assertions.assertNotEquals(byId, daoCredit.findById(credit.getId()));

        byId = daoCredit.findById(credit.getId());
        Assertions.assertEquals(credit.getId(), byId.getId());
        Assertions.assertEquals(updatedCreditLimit, byId.getCreditLimit());
        Assertions.assertEquals(updatedInterestRate, byId.getInterestRate());
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            daoCredit.save((new Credit(null,-1,10,"1")));
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            daoCredit.save((new Credit(null,10,-1,"1")));
        });

        daoCredit.delete(credit.getId());
        Assertions.assertEquals(initialSize, daoCredit.findAll().size());
    }

}