package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exceptions.InsufficientBalanceException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDAO implements TransferDAO {
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transfer makeTransaction(Transfer transfer) throws InsufficientBalanceException {
        Account transferTo = getAccountById(transfer.getUserIdTo());
        Account transferFrom = getAccountById(transfer.getUserIdFrom());

        double subtractFromBalance = transferFrom.getBalance() - transfer.getAmount();
        double addToBalance = transferTo.getBalance() + transfer.getAmount();
        if (transfer.getAmount() <= 0) {
            return transfer;
        } else if (transfer.getAmount() <= transferFrom.getBalance()) {
            String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
            jdbcTemplate.update(sql, addToBalance, transferTo.getAccount_id());

            String sql1 = "UPDATE accounts SET balance = ? WHERE account_id = ?";
            jdbcTemplate.update(sql1, subtractFromBalance, transferFrom.getAccount_id());

            String sql2 = "INSERT INTO transfers (transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                    "VALUES(DEFAULT,2, 2,?,?,?)";
            jdbcTemplate.update(sql2, transferFrom.getAccount_id(), transferTo.getAccount_id(), transfer.getAmount());
            return transfer;

        } else {
            throw new InsufficientBalanceException("Insufficient balance");
        }
    }

    @Override
    public List<Transfer> displayTransferHistory(int userId) {
        List<Transfer> transfers = new ArrayList<>();
        Account account = getAccountById(userId);

        String sql = "SELECT amount, transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, userTo.username username_to, userFrom.username username_from,\n" +
                " userTo.user_id userId_to, userFrom.user_id userId_from\n" +
                "FROM transfers\n" +
                "JOIN accounts accountTo ON accountTo.account_id = transfers.account_to \n" +
                "JOIN accounts  accountFrom ON accountFrom.account_id = transfers.account_from\n" +
                "JOIN users userTo ON userTo.user_id = accountTo.user_id \n" +
                "JOIN users userFrom ON userFrom.user_id = accountFROM.user_id \n" +
                "WHERE transfers.account_from = ? OR transfers.account_to = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, account.getAccount_id(), account.getAccount_id());
        while (rowSet.next()) {
            Transfer transfer = mapRowToTransfer(rowSet);
            transfers.add(transfer);
        }
        return transfers;
    }

    private Account getAccountById(int userId) {
        String sql = "SELECT balance, user_id, account_id FROM accounts WHERE user_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        Account account = new Account();

        if (rowSet.next()) {
            account.setBalance(rowSet.getDouble("balance"));
            account.setAccount_id(rowSet.getInt("account_id"));
            account.setUser_id(rowSet.getInt("user_id"));
            return account;
        }
        throw new UsernameNotFoundException("User was not found.");
    }

    private Transfer mapRowToTransfer(SqlRowSet rowSet) {
        Transfer transfer = new Transfer();
        transfer.setAmount(rowSet.getDouble("amount"));
        transfer.setTransferId(rowSet.getInt("transfer_id"));
        transfer.setTransferTypeId(rowSet.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rowSet.getInt("transfer_status_id"));
        transfer.setAccountFrom(rowSet.getInt("account_from"));
        transfer.setAccountTo(rowSet.getInt("account_to"));
        transfer.setNameTo(rowSet.getString("username_to"));
        transfer.setNameFrom(rowSet.getString("username_from"));
        transfer.setUserIdFrom(rowSet.getInt("userId_from"));
        transfer.setUserIdTo(rowSet.getInt("userId_to"));
        return transfer;
    }

}
