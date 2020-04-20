package dao;

import exception.DaoException;
import model.GroceryList;
import model.Source;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Sql2oGroceryListDao implements GroceryListDao {

    private Sql2o sql2o;

    public Sql2oGroceryListDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void add(GroceryList groceryList) throws DaoException {
        try(Connection conn = sql2o.open()) {
            String sql = "INSERT INTO GroceryLists(name, accountID) VALUES(:name, :accountID);";
            int listID = (int)conn.createQuery(sql)
                    .addParameter("name", groceryList.getName())
                    .addParameter("accountID", groceryList.getAccountID())
                    .executeUpdate().getKey();
            groceryList.setListId(listID);
            sql = "UPDATE GroceryLists SET listID = :listID WHERE name = :name AND accountID = :accountID;";
            conn.createQuery(sql)
                    .addParameter("listID", listID)
                    .addParameter("name", groceryList.getName())
                    .addParameter("accountID", groceryList.getAccountID())
                    .executeUpdate();
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to add new grocery list", ex);
        }
    }

    @Override
    public void share(GroceryList groceryList) throws DaoException {
        try(Connection conn = sql2o.open()) {
            String sql = "INSERT INTO GroceryLists(listID, name, accountID) VALUES (:listID, :name, :accountID);";
            conn.createQuery(sql)
                    .addParameter("listID", groceryList.getListId())
                    .addParameter("name", groceryList.getName())
                    .addParameter("accountID", groceryList.getAccountID())
                    .executeUpdate();
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to share grocery list", ex);
        }
    }
    @Override
    public List<GroceryList> findAll() {
        String sql = "SELECT * FROM GroceryLists;";
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql).executeAndFetch(GroceryList.class);
        }
    }

    @Override
    public List<GroceryList> findListByAccountID(int accountID) {
        String sql = "SELECT * FROM GroceryLists WHERE accountID = :accountID";
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql).addParameter("accountID", accountID)
                    .executeAndFetch(GroceryList.class);
        }

    }

    @Override
    public void addProduct(String upc, int listID, Source source) throws DaoException {
        try (Connection conn = sql2o.open()) {
            String sql = "PRAGMA foreign_keys = 1";
            conn.createQuery(sql).executeUpdate();
            String provider = source.getprovider();
            sql = "INSERT INTO ListContents(upc, listID, provider) VALUES (:upc, :listID, :provider);";
            conn.createQuery(sql)
                    .addParameter("upc", upc)
                    .addParameter("listID", listID)
                    .addParameter("provider", provider)
                    .executeUpdate();
        }
        catch (Exception ex) {
            throw new DaoException("Unable to add product to list", ex);
        }

    }

    public void modifyProduct(String upc, int listID, String provider) throws DaoException {
        try (Connection conn = sql2o.open()) {
            String sql = "PRAGMA foreign_keys = 1";
            conn.createQuery(sql).executeUpdate();
            sql = "UPDATE ListContents SET provider = :provider WHERE upc = :upc AND listID = :listID;";
            conn.createQuery(sql)
                    .addParameter("upc", upc)
                    .addParameter("listID", listID)
                    .addParameter("provider", provider)
                    .executeUpdate();
        }
        catch (Exception ex) {
            throw new DaoException("Unable to add product to list", ex);
        }

    }

    @Override
    public void deleteItem(int upc, int listID) throws DaoException {
        try (Connection conn = sql2o.open()) {
            //String sql = "PRAGMA foreign_keys = 1";
            //conn.createQuery(sql).executeUpdate();
            String sql = "DELETE FROM ListContents WHERE upc = " + upc + " AND listID = " + listID + ";";
            conn.createQuery(sql)
                    .executeUpdate();
        } catch (Exception ex) {
            throw new DaoException("Unable to delete product from list", ex);
        }
    }

    @Override
    public void deleteList(int accountID, int listID) throws DaoException {
        try (Connection conn = sql2o.open()) {
            String sql = "PRAGMA foreign_keys = 1";
            conn.createQuery(sql).executeUpdate();
            sql = "DELETE FROM ListContents WHERE listID = " + listID + ";";
            conn.createQuery(sql).executeUpdate();
            sql = "DELETE FROM GroceryLists WHERE listID = " + listID + " AND accountID = " + accountID +  ";";
            conn.createQuery(sql).executeUpdate();
        } catch (Exception ex) {
            throw new DaoException("Unable to delete list", ex);
        }
    }

    //The format that we store list objects in.
    private class RelationObject {
        String upc;
        int listID;
        int quantity;
        String provider;
    }
    public List<String> findByListId(int listID) {
        try (Connection conn = sql2o.open()) {
            String sql = "PRAGMA foreign_keys = 1";
            conn.createQuery(sql).executeUpdate();
            // get UPc
            sql = "SELECT * FROM ListContents WHERE listID = :listID";
            List<RelationObject> objList = conn.createQuery(sql).addParameter("listID", listID).executeAndFetch(RelationObject.class);
            List<String> returnList = new ArrayList<String>();
            for (int i = 0; i < objList.size(); i++) {
                returnList.add(objList.get(i).upc);
            }
            return returnList;
        } catch (Exception ex) {
            throw new DaoException("Unable to get products in list.", ex);
        }
    }
    public Map<String, String> findUpcProviderByListId(int listID) {
        try (Connection conn = sql2o.open()) {
            String sql = "PRAGMA foreign_keys = 1";
            conn.createQuery(sql).executeUpdate();
            // get UPc
            sql = "SELECT * FROM ListContents WHERE listID = :listID";
            List<RelationObject> objList = conn.createQuery(sql).addParameter("listID", listID).executeAndFetch(RelationObject.class);
            Map<String, String> returnList = new HashMap<String, String>(); //upc to provider
            for (int i = 0; i < objList.size(); i++) {
                returnList.put(objList.get(i).upc, objList.get(i).provider);
            }
            return returnList;
        } catch (Exception ex) {
            throw new DaoException("Unable to get products in list.", ex);
        }
    }
}