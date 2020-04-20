package dao;

import exception.DaoException;
import fetch.AmazonFetch;
import fetch.WalmartFetch;
import model.Product;
import model.Source;
import org.apache.http.HttpStatus;
import org.jsoup.HttpStatusException;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

public class Sql2oSourceDao implements SourceDao {
    private Sql2o sql2o;

    public Sql2oSourceDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void add(Source source) throws DaoException {
        try (Connection conn = sql2o.open()) {
            String sql = "INSERT INTO Source(upc,provider,price) VALUES(:upc,:provider,:price);";
            conn.createQuery(sql).bind(source).executeUpdate();
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to add new source", ex);
        }
    }

    @Override
    public List<Source> findAll() {
        String sql = "SELECT * FROM Source;";
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql).executeAndFetch(Source.class);
        }
    }

    @Override
    public List<Source> findByUPC(String upc) {
            try (Connection conn = sql2o.open()) {
                String sql = "PRAGMA foreign_keys = 1";
                conn.createQuery(sql).executeUpdate();
                sql = "SELECT * FROM Source WHERE upc = :upc";
                return conn.createQuery(sql).addParameter("upc", upc).executeAndFetch(Source.class);
            }
    }

    @Override
    public void setPrices(Product prod) throws DaoException {
        WalmartFetch wf = new WalmartFetch();
        AmazonFetch af = new AmazonFetch();
        String upc = prod.getUpc();
        try(Connection conn = sql2o.open()) {
            float azPrice = af.getPrice(prod);
            float wmPrice = wf.getPrice(prod);
            Source walmartSource = new Source(upc, "Walmart Grocery", wmPrice);
            this.add(walmartSource);
            Source amazonSource = new Source(upc, "Amazon Fresh", azPrice);
            this.add(amazonSource);
        } catch (HttpStatusException ex) {
            throw new DaoException("503 error", ex);
        } catch (Exception ex) {
            System.out.println(ex);
            throw new DaoException("Unable to set prices", ex);
        }
    }




}
