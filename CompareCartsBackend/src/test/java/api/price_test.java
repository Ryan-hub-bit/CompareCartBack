
package api;


import fetch.AmazonFetch;
import fetch.WalmartFetch;
import fetch.WegmansFetch;
import model.Product;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class price_test {

    @Test
    public void getWegmansPriceFromUPC() throws ParseException {
        Product product = new Product("034000040254", "cadbury egg", null, null);
        Product product1 = new Product("2410010685", "cheez it", null, null);
        Product product2 = new Product("034500151368", "land o lakes butter", null, null);
        Product product3 = new Product("767707001253", "fake UPC, should return -1 w/o crashing", null, null);

        WegmansFetch wegf = new WegmansFetch();
        try {
            List<Product> prod = new ArrayList<Product>();
            prod.add(product);
            prod.add(product1);
            prod.add(product2);
            prod.add(product3);
            for (model.Product item : prod) {
                System.out.println(wegf.getPriceFromUPC(item.getUpc()));
            }
        }
        catch (IOException ex){
            System.out.println("TODO exception");
        }
        catch (InterruptedException ex) {
            System.out.println("TODO exception");
        }
    }

    @Test
    public void getWalmartPriceFromUPC() throws ParseException {
        Product product = new Product("034000040254", "cadbury egg", null, null);
        Product product1 = new Product("2410010684", "cheez it", null, null);
        Product product2 = new Product("034500151368", "land o lakes butter", null, null);
        Product product3 = new Product("767707001253", "fake UPC, should return -1 w/o crashing", null, null);

        WalmartFetch wf = new WalmartFetch();
        try {
            List<Product> prod = new ArrayList<Product>();
            prod.add(product);
            prod.add(product1);
            prod.add(product2);
            prod.add(product3);
            for (model.Product item : prod) {
                System.out.println(wf.getPriceFromUPC(item.getUpc()));
            }
        }
        catch (IOException ex){
            System.out.println("TODO exception");
        }
        catch (InterruptedException ex) {
            System.out.println("TODO exception");
        }
    }

    @Test
    public void getWalmartPrice() throws ParseException {
        Product product = new Product("12345", "banana", null, null);
        Product product1 = new Product("1234", "pear", null, null);
        Product product2 = new Product("123", "apples", null, null);
        //Cheez It by UPC
        Product product3 = new Product("2410010684", "2410010684", null, null);

        WalmartFetch wf = new WalmartFetch();
        try {
            for (dao.ProductPrice prod : wf.getItems(product3.getName())) {
                System.out.println(prod.toString());
            }
        }
        catch (IOException ex){
            System.out.println("TODO exception");
        }
        catch (InterruptedException ex) {
            System.out.println("TODO exception");
        }
    }

    @Test
    public void getAmazonPrice() throws ParseException {
        Product product = new Product("12345", "banana", null, null);
        Product product1 = new Product("1234", "pear", null, null);
        Product product2 = new Product("123", "apples", null, null);

        AmazonFetch af = new AmazonFetch();
        try {

            for (dao.ProductPrice prod : af.getItems(product.getName())) {
                System.out.println(prod.toString());
                System.out.println(af.ASINtoUPC(prod.getAsin()));
                System.out.println(af.getPrice(prod.getProduct()));

            }


        }

        catch (IOException ex){

            System.out.println(ex.toString());
        }
        catch (InterruptedException ex) {
            System.out.println("IE exception");
        }
    }



}
