package com.haulmont.testtask.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client implements Serializable {
    private String id;
    private String foolName;
    private String telephoneNumber;
    private String eMailAdress;
    private Integer passportNumber;
    private String bankId;
}
