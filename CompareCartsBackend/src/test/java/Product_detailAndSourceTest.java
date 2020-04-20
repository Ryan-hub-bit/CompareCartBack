import fetch.WalmartFetch;
import model.Product;
import model.Product_Detail;
import model.Source;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

public class Product_detailAndSourceTest {
    @Test
    public void getProductDetail() throws ParseException, IOException, InterruptedException {
        WalmartFetch wl = new WalmartFetch();
        Product product = new Product("123", "LaysChips", "Food", "12 oz bbq");
        float price1 = wl.getPrice(product);
        Product_Detail product_detail = new Product_Detail(product.getUpc(),"brand1", new Date());
        Source source = new Source(product.getUpc(),"amazon",price1);
        Product product2 = new Product("234", "LaysChips", "Food", "12 oz bbq");
        float price2 = wl.getPrice(product);
        Product_Detail  product_detail2 = new Product_Detail(product2.getUpc(),"brand1", new Date());
        Source source2 = new Source(product2.getUpc(),"amazon",price2);
        Product product3 = new Product("345", "banana", "fruit", "null");
        float price3 = wl.getPrice(product3);
        Product_Detail  product_detail3 = new Product_Detail(product3.getUpc(),"brand3", new Date());
        Source source3 = new Source(product.getUpc(),"amazon",price3);
        Product product4 = new Product("456", "orange", "fruit", "null");
        float price4 = wl.getPrice(product4);
        Product_Detail  product_detail4 = new Product_Detail(product4.getUpc(),"brand4", new Date());
        Source source4 = new Source(product4.getUpc(),"amazon",price4);
        System.out.println(product_detail.getUpc());
        System.out.println(product_detail2.getUpc());
        System.out.println(product_detail3.getUpc());
        System.out.println(product_detail4.getUpc());
        System.out.println(source.getPrice());
        System.out.println(source2.getPrice());
        System.out.println(source3.getPrice());
        System.out.println(source4.getPrice());

    }
}
