package dao;

import exception.DaoException;
import model.Product;
import model.Source;

import java.util.List;

public interface SourceDao {
    void add(Source source) throws DaoException;
    List<Source> findAll();
    List<Source> findByUPC(String upc);
    void setPrices(Product prod);
}
