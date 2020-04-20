package dao;

import exception.DaoException;
import model.GroceryList;
import model.Source;
import java.util.Map;
import java.util.List;

public interface GroceryListDao {
    void add(GroceryList groceryList) throws DaoException;
    void share(GroceryList groceryList) throws DaoException;
    void addProduct(String upc, int listID, Source source) throws DaoException;
    Map<String, String> findUpcProviderByListId(int listID) throws DaoException;
    List<String> findByListId(int listID) throws DaoException;

    List<GroceryList> findAll();
    List<GroceryList> findListByAccountID(int accountID);
    void deleteItem(int upc, int listID) throws DaoException;
    void deleteList(int accountID, int listID) throws DaoException;
    void modifyProduct(String upc, int listID, String provider) throws DaoException;
}

