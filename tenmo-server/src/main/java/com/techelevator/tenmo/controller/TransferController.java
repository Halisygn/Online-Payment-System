package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.exceptions.InsufficientBalanceException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
@PreAuthorize("isAuthenticated()")
@RestController
public class TransferController {
        @Autowired
        private UserDAO userDAO;
        @Autowired
        private TransferDAO transferDAO;

        public TransferController(UserDAO userDAO, TransferDAO transferDAO) {
            this.userDAO = userDAO;
            this.transferDAO = transferDAO;
        }

        @RequestMapping(path = "/users", method = RequestMethod.GET)
        public List<User> retrieveUserList(){
            return userDAO.findAll();
        }
        @ResponseStatus(HttpStatus.CREATED)
        @RequestMapping(path = "/transfers", method = RequestMethod.POST )
        public Transfer makeTransfer(@RequestBody Transfer transfer) throws InsufficientBalanceException {
            return transferDAO.makeTransaction(transfer);
        }
        @RequestMapping(path = "/users/{id}/transfers", method = RequestMethod.GET)
        public List<Transfer> retrieveTransferList(@PathVariable int id){
           return transferDAO.displayTransferHistory(id);
        }
}
