package model;

import java.util.Objects;

public class Source {
    private String upc;
    private String provider;
    private float price;

    public Source(String upc, String provider, float price) {
        this.upc = upc;
        this.provider = provider;
        this.price = price;
    }

    public Source(String upc) {
        this.upc = upc;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getprovider() {
        return provider;
    }

    public void setprovider(String provider) {
        this.provider = provider;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Source source = (Source) o;
        return Float.compare(source.price, price) == 0 &&
                Objects.equals(upc, source.upc) &&
                Objects.equals(provider, source.provider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upc, provider, price);
    }

    @Override
    public String toString() {
        return "Source{" +
                "upc='" + upc + '\'' +
                ", provider='" + provider + '\'' +
                ", price=" + price +
                '}';
    }
}
