package dao;

import exception.DaoException;
import model.Product_Detail;

import java.util.List;

public interface Product_DetailsDao {
    void add(Product_Detail product_detail) throws DaoException;
    List<Product_Detail> findAll();
    List<Product_Detail> findByUPC(String upc);
}
