package dao;

import exception.DaoException;
import javafx.util.Pair;
import model.Account;

public interface AccountDao {
    boolean add(Account account) throws DaoException;
    Pair<Boolean, Integer> checkLogin(Account account) throws DaoException;
    int checkNumber(String mobileNumber) throws DaoException;
}
