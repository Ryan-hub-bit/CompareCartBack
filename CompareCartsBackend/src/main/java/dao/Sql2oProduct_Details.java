package dao;

import exception.DaoException;
import model.Product_Detail;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

public class Sql2oProduct_Details implements Product_DetailsDao {
    private Sql2o sql2o;

    public Sql2oProduct_Details(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void add(Product_Detail product_detail) throws DaoException {
        try (Connection conn = sql2o.open()) {
            String sql = "INSERT INTO Product_Detail(upc,brand,date,url) VALUES(:upc,:brand,:date,:url);";
            conn.createQuery(sql).bind(product_detail).executeUpdate();
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to add new product_detail", ex);
        }

    }

    @Override
    public List<Product_Detail> findAll() {
        String sql = "SELECT * FROM Product_Detail;";
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql).executeAndFetch(Product_Detail.class);
        }
    }

    @Override
    public List<Product_Detail> findByUPC(String upc) {

        try (Connection conn = sql2o.open()) {
            String sql = "PRAGMA foreign_keys = 1";
            conn.createQuery(sql).executeUpdate();
            sql = "SELECT * FROM Product_Detail WHERE upc = :upc";
            return conn.createQuery(sql).addParameter("upc", upc).executeAndFetch(Product_Detail.class);
        }
    }
}
