package com.haulmont.testtask.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bank {
    private String id;
    private String name;
    private List<Client> clients;
    private List<Credit> credits;
}