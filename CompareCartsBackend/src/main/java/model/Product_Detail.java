package model;

import java.util.Date;
import java.util.Objects;

public class Product_Detail {
    private String upc;
    private String brand;
    private Date date;
    private String url;

    public Product_Detail(String upc, String brand) {
        this.upc = upc;
        this.brand = brand;
        this.url = "No image";
    }

    public Product_Detail(String upc, String brand, Date date) {
        this.upc = upc;
        this.brand = brand;
        this.date = date;
        this.url = "No image";
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setUrl(String url) { this.url = url; }

    public String getUrl() { return this.url; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product_Detail that = (Product_Detail) o;
        return Objects.equals(upc, that.upc) &&
                Objects.equals(brand, that.brand) &&
                Objects.equals(date, that.date) &&
                Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upc, brand, date);
    }

    @Override
    public String toString() {
        return "Product_Detail{" +
                "upc='" + upc + '\'' +
                ", brand='" + brand + '\'' +
                ", date=" + date +
                ", url=" + url +
                '}';
    }
}
