package dao;

import exception.DaoException;
import model.Product;
import model.Product_Detail;
import model.Source;
import org.sql2o.Sql2o;

import org.sql2o.Connection;
import org.sql2o.Sql2oException;

import java.util.List;

public class Sql2oProductDao implements ProductDao {

    private Sql2o sql2o;
    private Product_DetailsDao product_detailsDao;
    private SourceDao sourceDao;
    public Sql2oProductDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

//    public Sql2oProductDao(Sql2o sql2o, Product_DetailsDao product_detailsDao, SourceDao sourceDao) {
//        this.sql2o = sql2o;
//        this.product_detailsDao = product_detailsDao;
//        this.sourceDao = sourceDao;
//    }

//    @Override
//    public void add(Product product) throws DaoException {
//        try (Connection conn = sql2o.open()) {
//            String sql = "INSERT INTO Products(upc, name, category, description) VALUES(:upc, :name, :category, :description);";
//            conn.createQuery(sql).bind(product).executeUpdate();
//        } catch (Sql2oException ex) {
//            throw new DaoException("Unable to add new product", ex);
//        }
//    }

    @Override
    public void add(Product product, Product_Detail product_detail) throws DaoException {
        try (Connection conn = sql2o.open()) {
            String sql = "INSERT INTO Products(upc, name, category, description) VALUES(:upc, :name, :category, :description);";
            conn.createQuery(sql).bind(product).executeUpdate();
            sql = "INSERT INTO Product_Detail(upc,brand,date,url) VALUES(:upc,:brand,:date,:url);";
            conn.createQuery(sql).bind(product_detail).executeUpdate();
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to add new product", ex);
        }
    }


    @Override
    public List<Product> findAll() {
        String sql = "SELECT * FROM Products;";
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql).executeAndFetch(Product.class);
        }
    }

    @Override
    public List<Product> findByUPC(String upc) {
        String sql = "SELECT * FROM Products WHERE upc = :upc";
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql).addParameter("upc", upc).executeAndFetch(Product.class);
        }
    }
}
