package com.haulmont.testtask.controller;

import com.haulmont.testtask.DAO.DAOBank;
import com.haulmont.testtask.DAO.DAOClient;
import com.haulmont.testtask.DAO.DAOCredit;
import com.haulmont.testtask.DAO.DAOOffer;
import com.haulmont.testtask.Entity.Bank;
import com.haulmont.testtask.Entity.Client;
import com.haulmont.testtask.Entity.Credit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/banks")
public class BankController {
    private final DAOBank daoBank;
    private final DAOClient daoClient;
    private final DAOCredit daoCredit;

    public BankController(DAOBank daoBank, DAOClient daoClient, DAOCredit daoCredit, DAOOffer daoOffer) {
        this.daoBank = daoBank;
        this.daoClient = daoClient;
        this.daoCredit = daoCredit;
    }


    @GetMapping
    public ModelAndView getBanks() {
        ModelAndView modelAndView = new ModelAndView("banks");
        modelAndView.addObject("banks", daoBank.findAll());
        modelAndView.addObject("clients", daoClient.findAll());
        modelAndView.addObject("credits", daoCredit.findAll());
        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getBankById(@PathVariable String id) {
        ModelAndView modelAndView = new ModelAndView("bank");
        Bank bank = daoBank.findById(id);
        modelAndView.addObject("bank", bank);
        modelAndView.addObject("credits", daoCredit.findCreditsWithoutBank(id));
        modelAndView.addObject("bankClients", bank.getClients());
        modelAndView.addObject("bankCredits", bank.getCredits());
        modelAndView.addObject("clients", daoClient.findClientWithoutBank(id));
        return modelAndView;
    }

    @PostMapping
    public ModelAndView createBank(@RequestParam String name,
                                   @RequestParam(required = false) String clientId,
                                   @RequestParam(required = false) String creditId) {
        List<Client> clients = new ArrayList<>();
        if (StringUtils.isNotEmpty(clientId)) {
            clients.add(daoClient.findById(clientId));
        }
        List<Credit> credits = new ArrayList<>();
        if (StringUtils.isNotEmpty(creditId)) {
            credits.add(daoCredit.findById(creditId));
        }
        Bank bank = new Bank(null, name, clients, credits);
        Bank saveBank = daoBank.save(bank);
        log.info("create bank {}", saveBank.getId());
        return new ModelAndView("redirect:/admin/banks");
    }

    @PostMapping("/{id}")
    public ModelAndView updateBank(@PathVariable String id,
                                   @RequestParam String name,
                                   @RequestParam(required = false) String clientId,
                                   @RequestParam(required = false) String creditId) {
        List<Client> clients = daoBank.findById(id).getClients();
        if (StringUtils.isNotEmpty(clientId)) {
            clients.add(daoClient.findById(clientId));
        }
        List<Credit> credits = daoBank.findById(id).getCredits();
        if (StringUtils.isNotEmpty(creditId)) {
            credits.add(daoCredit.findById(creditId));
        }
        Bank bank = new Bank(id, name, clients, credits);
        daoBank.save(bank);
        log.info("update bank{}", bank.getId());
        return new ModelAndView("redirect:/admin/banks");
    }

    @PostMapping("/{id}/remove")
    public ModelAndView delete(@PathVariable String id) {
        log.info("delete bank{}", daoBank.findById(id));
        daoBank.delete(id);
        return new ModelAndView("redirect:/admin/banks");
    }

}
