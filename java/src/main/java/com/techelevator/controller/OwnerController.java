package com.techelevator.controller;

import com.techelevator.dao.OwnerDao;
import com.techelevator.exception.DaoException;
import com.techelevator.model.RegisterOwnerDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping(path = "/owner")

public class OwnerController {

    private final OwnerDao ownerDao;

    public OwnerController(OwnerDao ownerDao) {
        this.ownerDao = ownerDao;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public void register(@Valid @RequestBody RegisterOwnerDto newOwner) {
        try {
            ownerDao.createOwner(newOwner);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Owner registration failed.");
        }
    }




}