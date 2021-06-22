package com.haulmont.testtask.controller;

import com.haulmont.testtask.DAO.DAOCredit;
import com.haulmont.testtask.Entity.Credit;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/credits")
public class CreditController {
    private final DAOCredit daoCredit;

    public CreditController(DAOCredit daoCredit) {
        this.daoCredit = daoCredit;
    }

    @GetMapping
    public ModelAndView getCredits() {
        ModelAndView modelAndView = new ModelAndView("credits");
        modelAndView.addObject("credits", daoCredit.findAll());
        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getCreditById(@PathVariable String id) {
        ModelAndView modelAndView = new ModelAndView("credit");
        modelAndView.addObject("credit", daoCredit.findById(id));
        modelAndView.addObject("creditId", id);
        return modelAndView;
    }

    @PostMapping
    public ModelAndView createCredit(@RequestParam Integer creditLimit,
                                     @RequestParam Integer interestRate) {
        Credit credit = new Credit(null, creditLimit, interestRate, null);
        daoCredit.save(credit);
        return new ModelAndView("redirect:/admin/credits");
    }

    @PostMapping("/{id}")
    public ModelAndView updateCredit(@PathVariable String id,
                                     @RequestParam Integer creditLimit,
                                     @RequestParam Integer interestRate) {
        Credit credit = new Credit(id, creditLimit, interestRate, daoCredit.findById(id).getBankId());
        daoCredit.save(credit);
        return new ModelAndView("redirect:/admin/credits");
    }

    @PostMapping("/{id}/remove")
    public ModelAndView delete(@PathVariable String id) {
        daoCredit.delete(id);
        return new ModelAndView("redirect:/admin/credits");
    }
}
