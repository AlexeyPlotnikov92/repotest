package com.haulmont.testtask.controller;

import com.haulmont.testtask.DAO.DAOBank;
import com.haulmont.testtask.DAO.DAOClient;
import com.haulmont.testtask.DAO.DAOCredit;
import com.haulmont.testtask.DAO.DAOOffer;
import com.haulmont.testtask.Entity.Bank;
import com.haulmont.testtask.Entity.Offer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/offers")
public class OffersController {
    private final DAOOffer daoOffer;
    private final DAOBank daoBank;
    private final DAOCredit daoCredit;
    private final DAOClient daoClient;

    public OffersController(DAOOffer daoOffer, DAOBank daoBank, DAOCredit daoCredit, DAOClient daoClient) {
        this.daoOffer = daoOffer;
        this.daoBank = daoBank;
        this.daoCredit = daoCredit;
        this.daoClient = daoClient;
    }

    @GetMapping
    public ModelAndView getOffers() {
        ModelAndView modelAndView = new ModelAndView("offers");
        Bank bank = daoBank.findAll().get(0);
        modelAndView.addObject("offers", daoOffer.findAll());
        modelAndView.addObject("clients", bank.getClients());
        modelAndView.addObject("credits", bank.getCredits());
        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getOfferById(@PathVariable String id) {
        Offer offer = daoOffer.findById(id);
        ModelAndView modelAndView = new ModelAndView("offer");
        Bank bank = daoBank.findAll().get(0);
        modelAndView.addObject("offer", offer);
        modelAndView.addObject("offerId", id);
        modelAndView.addObject("clients", bank.getClients());
        modelAndView.addObject("credits", bank.getCredits());
        modelAndView.addObject("clientOffer", offer.getClient());
        modelAndView.addObject("creditOffer", offer.getCredit());
        return modelAndView;
    }

    @PostMapping
    public ModelAndView createOffer(@RequestParam String clientId,
                                    @RequestParam String creditId,
                                    @RequestParam Integer creditAmount) {
        Offer offer = new Offer(null,
                daoClient.findById(clientId),
                daoCredit.findById(creditId),
                creditAmount);
        daoOffer.save(offer);
        return new ModelAndView("redirect:/admin/offers");
    }

    @PostMapping("/{id}")
    public ModelAndView updateOffer(@PathVariable String id,
                                    @RequestParam String clientId,
                                    @RequestParam String creditId,
                                    @RequestParam Integer creditAmount) {
        Offer offer = new Offer(id, daoClient.findById(clientId), daoCredit.findById(creditId), creditAmount);
        daoOffer.save(offer);
        return new ModelAndView("redirect:/admin/offers");
    }

    @PostMapping("/{id}/remove")
    public ModelAndView delete(@PathVariable String id) {
        daoOffer.delete(id);
        return new ModelAndView("redirect:/admin/offers");
    }
}
