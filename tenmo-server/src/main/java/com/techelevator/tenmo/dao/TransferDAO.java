package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exceptions.InsufficientBalanceException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDAO {
    Transfer makeTransaction(Transfer transfer) throws InsufficientBalanceException;
    List<Transfer> displayTransferHistory(int userId);
}
