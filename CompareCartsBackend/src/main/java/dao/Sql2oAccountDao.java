package dao;


import exception.DaoException;
import javafx.util.Pair;
import model.Account;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.sql.ResultSet;
import java.util.List;

public class Sql2oAccountDao implements AccountDao {
    private Sql2o sql2o;

    public Sql2oAccountDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }


    // Password needs to be hashed
    @Override
    public boolean add(Account account) throws DaoException {
        try(Connection conn = sql2o.open()) {
            String sql = "SELECT * FROM Accounts WHERE username = :username;";
            List<Integer> resultSet = conn.createQuery(sql)
                    .addParameter("username", account.getusername())
                    .executeAndFetch(ResultSet::getType);
            if (!resultSet.isEmpty()) {
                return false;
            }
            sql = "INSERT INTO Accounts(username, fullname, mobileNumber, address) VALUES(:username, :fullname, :mobileNumber, :address);";
            int accID = (int)conn.createQuery(sql).bind(account).executeUpdate().getKey();
            account.setAccountID(accID);
            int hashedPassword = account.getPassword().hashCode();
            sql = "UPDATE Accounts SET password = :hashedPassword WHERE accountID = :accID";
            conn.createQuery(sql)
                    .addParameter("hashedPassword", hashedPassword)
                    .addParameter("accID", accID)
                    .executeUpdate();
            return true;
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to add new account", ex );
        }
    }

    @Override
    public Pair<Boolean, Integer> checkLogin(Account account) throws DaoException {
        try(Connection conn = sql2o.open()) {
            int hashedPassword = account.getPassword().hashCode();
            String sql = "SELECT accountID FROM Accounts WHERE username = :username AND password = :hashedPassword;";
            List<Integer> accNum = conn.createQuery(sql)
                    .addParameter("username", account.getusername())
                    .addParameter("hashedPassword", hashedPassword)
                    .executeAndFetch(Integer.class);
            if (accNum.isEmpty()) {
                return new Pair<>(false, -1);
            }
            return new Pair<>(true, accNum.get(0));
        }
    }

    @Override
    public int checkNumber(String mobileNumber) throws DaoException {
        try (Connection conn = sql2o.open()) {
            String sql = "SELECT accountID FROM Accounts WHERE mobileNumber = :mobileNumber;";
            List<Integer> accNum = conn.createQuery(sql)
                    .addParameter("mobileNumber", mobileNumber)
                    .executeAndFetch(Integer.class);
            if (accNum.isEmpty()) {
                return -1;
            }
            return accNum.get(0);
        }
    }
}
