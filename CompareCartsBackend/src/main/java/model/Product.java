package model;

import java.util.Objects;
import fetch.WalmartFetch;
import java.util.List;
import java.io.IOException;

public class Product {
    private String upc;
    private String name;
    private String category;
    private String description;

    private List<Float> price_list;

    public Product(String upc, String name) {
        this.upc = upc;
        this.name = name;
    }

    public Product(String upc, String name, String category, String description) {
        this.upc = upc;
        this.name = name;
        this.category = category;
        this.description = description;
    }

    public String getUpc() {
        return upc;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }


    public void setUpc(String upc) {
        this.upc = upc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void getPrices() {
        WalmartFetch wf = new WalmartFetch();
        try {
            price_list.set(0, wf.getPrice(this));
        }
        catch (Exception ex) {
            System.out.println("TODO handle exception");
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return upc == product.upc;
    }

    @Override
    public int hashCode() {
        return Objects.hash(upc);
    }

    @Override
    public String toString() {
        return "Product{" +
                "upc=" + upc +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
