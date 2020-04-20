package dao;

import exception.DaoException;
import model.Product;
import model.Product_Detail;
import model.Source;

import java.util.List;

public interface ProductDao {
    void add(Product product, Product_Detail product_detail) throws DaoException;
    List<Product> findAll();
    List<Product> findByUPC(String upc);
}
