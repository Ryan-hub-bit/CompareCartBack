package dao;

import model.Product;

public class ProductPrice {
    private Product product;
    private float price;
    private String url;
    private String asin;

    public ProductPrice(Product product) {
        this.product = product;
        this.price = -1;
        this.url = "";
        this.asin = "";
    }

    public ProductPrice(Product product, float price) {
        this.price = price;
        this.product = product;
        this.url = "";
        this.asin = "";
    }

    public ProductPrice(Product product, float price, String url) {
        this.price = price;
        this.product = product;
        this.url = url;
        this.asin = "";
    }

    public ProductPrice(Product product, String url) {
        this.price = price;
        this.product = product;
        this.url = url;
        this.asin = "";
    }

    public Product getProduct() {
        return this.product;
    }

    public float getPrice() {
        return this.price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ProductPrice{" +
                "product=" + product +
                ", price=" + price +
                ", url='" + url + '\'' +
                ", asin='" + asin + '\'' +
                '}';
    }
}