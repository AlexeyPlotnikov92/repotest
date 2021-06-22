package com.haulmont.testtask.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Credit {
    private String id;
    private Integer creditLimit;
    private Integer InterestRate;
    private String bankId;
    private String name;

    public Credit(String id, Integer creditLimit, Integer interestRate, String bankId) {
        this.id = id;
        this.creditLimit = creditLimit;
        this.InterestRate = interestRate;
        this.bankId = bankId;
        name = creditLimit + " рублей, " + interestRate + "%ставка";
    }
}
