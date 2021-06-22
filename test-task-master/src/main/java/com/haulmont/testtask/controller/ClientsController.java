package com.haulmont.testtask.controller;

import com.haulmont.testtask.DAO.DAOClient;
import com.haulmont.testtask.Entity.Client;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/clients")
public class ClientsController {
    private final DAOClient daoClient;

    public ClientsController(DAOClient daoClient) {
        this.daoClient = daoClient;
    }

    @GetMapping
    public ModelAndView getClients() {
        ModelAndView modelAndView = new ModelAndView("clients");
        modelAndView.addObject("clients", daoClient.findAll());
        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getClient(@PathVariable String id) {
        ModelAndView modelAndView = new ModelAndView("client");
        modelAndView.addObject("client", daoClient.findById(id));
        return modelAndView;
    }

    @PostMapping
    public ModelAndView createClient(@RequestParam String foolName,
                                     @RequestParam String phoneNumber,
                                     @RequestParam String eMail,
                                     @RequestParam Integer passportNumber) {
        Client client = new Client(null, foolName, phoneNumber, eMail, passportNumber, null);
        daoClient.save(client);
        return new ModelAndView("redirect:/admin/clients");
    }

    @PostMapping("/{id}")
    public ModelAndView updateClient(@PathVariable String id,
                                     @RequestParam String foolName,
                                     @RequestParam String phoneNumber,
                                     @RequestParam String eMail,
                                     @RequestParam Integer passportNumber) {
        Client client = new Client(id, foolName,
                phoneNumber, eMail,
                passportNumber, daoClient.findById(id).getBankId());
        daoClient.save(client);
        return new ModelAndView("redirect:/admin/clients");
    }

    @PostMapping("/{id}/remove")
    public ModelAndView delete(@PathVariable String id) {
        daoClient.delete(id);
        return new ModelAndView("redirect:/admin/clients");
    }


}
