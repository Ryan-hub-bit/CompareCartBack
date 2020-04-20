package model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class GroceryList {
    private int listId;
    private String name;
    private int accountID;
    private List<Product> gList;
    private Date date;

    public GroceryList(String name, int accountID) {
        this.name = name;
        this.accountID = accountID;
        this.date = date;
    }

    public GroceryList(int listId, String name, int accountID) {
        this.listId = listId;
        this.name = name;
        this.accountID = accountID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public GroceryList(int listId, String name, int accountID, List<Product> gList, Date date) {
        this.listId = listId;
        this.name = name;
        this.accountID = accountID;
        this.gList = gList;
        this.date = date;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<Product> getgList() {
        return gList;
    }

    public void setgList(List<Product> gList) {
        this.gList = gList;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroceryList that = (GroceryList) o;
        return listId == that.listId &&
                accountID == that.accountID &&
                Objects.equals(name, that.name) &&
                Objects.equals(gList, that.gList) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listId, name, accountID, gList, date);
    }

    @Override
    public String toString() {
        return "GroceryList{" +
                "listId=" + listId +
                ", name='" + name + '\'' +
                ", accountID=" + accountID +
                ", gList=" + gList +
                ", date=" + date +
                '}';
    }
}
