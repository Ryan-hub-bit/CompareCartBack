package model;

import java.util.Objects;

// TODO: Account should not expose sensitive information. public class

public class Account {
    private int accountID;
    private String username;
    private String password;
    private String fullname;
    private String mobileNumber;
    private String address;


    public Account(int accountID, String username, String password, String fullname, String mobileNumber, String address) {
        this.accountID = accountID;
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.mobileNumber = mobileNumber;
        this.address = address;
    }

    public Account(int accountID, String username, String fullname, String mobileNumber, String address) {
        this.accountID = accountID;
        this.username = username;
        this.fullname = fullname;
        this.mobileNumber = mobileNumber;
        this.address = address;
    }

    public Account(String username, String password, String fullname, String mobileNumber, String address) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.mobileNumber = mobileNumber;
        this.address = address;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountID == account.accountID &&
                Objects.equals(username, account.username) &&
                Objects.equals(password, account.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountID, username, password);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountID=" + accountID +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}


