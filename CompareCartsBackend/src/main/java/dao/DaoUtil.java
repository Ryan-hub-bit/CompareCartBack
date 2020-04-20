package dao;

import fetch.WalmartFetch;
import model.GroceryList;
import model.Product;
import model.Product_Detail;
import model.Source;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Date;

public class DaoUtil {
    private DaoUtil() {

    }

    public static void addSampleProducts(ProductDao productDao,Product_DetailsDao product_detailsDao,SourceDao sourceDao) throws InterruptedException, ParseException, IOException {
        WalmartFetch wl = new WalmartFetch();
        Product product = new Product("123", "LaysChips", "Food", "12 oz bbq");
        float price1 = wl.getPrice(product);
        Product_Detail  product_detail = new Product_Detail(product.getUpc(),"brand1", new Date());
        Source source = new Source(product.getUpc(),"amazon",price1);
        Product product2 = new Product("234", "Lays", "Food", "12 oz bbq");
        float price2 = wl.getPrice(product);
        Product_Detail  product_detail2 = new Product_Detail(product2.getUpc(),"brand1", new Date());
        Source source2 = new Source(product2.getUpc(),"amazon",price2);
        Product product3 = new Product("345", "banana", "fruit", "null");
        float price3 = wl.getPrice(product3);
        Product_Detail  product_detail3 = new Product_Detail(product3.getUpc(),"brand3", new Date());
        Source source3 = new Source(product3.getUpc(),"amazon",price3);
        Product product4 = new Product("456", "orange", "fruit", "null");
        float price4 = wl.getPrice(product4);
        Product_Detail  product_detail4 = new Product_Detail(product4.getUpc(),"brand4", new Date());
        Source source4 = new Source(product4.getUpc(),"amazon",price4);

        System.out.println(product_detail.toString());
        System.out.println(source.toString());
        productDao.add(product,product_detail);
        productDao.add(product2,product_detail2);
        productDao.add(product3,product_detail3);
        productDao.add(product4,product_detail4);



    }

    public static void addSampleGroceryList(GroceryListDao groceryListDao) {
        groceryListDao.add(new GroceryList("Bob's Grocery List", 1));
        groceryListDao.add(new GroceryList("Sam's Grocery List", 2));
        groceryListDao.add(new GroceryList("My Shopping List", 1));
    }

    // This depends on the first two setup
    public static void addSampleProductsToList(GroceryListDao groceryListDao) {
        groceryListDao.addProduct("123", 1, new Source("123", "walmart", (float)2.00));
    }
}