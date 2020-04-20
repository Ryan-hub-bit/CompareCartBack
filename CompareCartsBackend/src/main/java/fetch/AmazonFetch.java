package fetch;

import dao.ProductPrice;
import model.Product;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Float.parseFloat;


public class AmazonFetch {
    public AmazonFetch() {

    }
    private static int userAgentNum = 0;

    private class ModifiedProduct {
        public String name;

        public ModifiedProduct(String name) {
            this.name = name;
        }

        @java.lang.Override
        public java.lang.String toString() {
            return "ModifiedProduct{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    private class Ingredients {
        private AmazonFetch.ModifiedProduct ingredients;

        public Ingredients(AmazonFetch.ModifiedProduct mp) {
            this.ingredients = mp;
        }


        @java.lang.Override
        public java.lang.String toString() {
            return "Ingredients{" +
                    "name=" + ingredients.name +
                    '}';
        }
    }

    public List<ProductPrice> getItems(String name) throws IOException, InterruptedException {
        List<ProductPrice> products = new ArrayList<ProductPrice>();
        StringBuilder newProduct = new StringBuilder(name);
        for (int i = 0; i < newProduct.length(); i++) {
            if (newProduct.charAt(i) == ' ') {
                newProduct.deleteCharAt(i);
                newProduct.insert(i, "%20");
            }
        }
        String searchUrl = "https://www.amazon.com/s?k=" + newProduct.toString() + "&i=amazonfresh";
        Document doc;

        Connection.Response response = null;

        boolean successful = false;
        int count = 0;
        //TODO distinguish 503 from other errors
        while (!successful && count < 20) {
            try {
                response = Jsoup.connect(searchUrl)
                        .userAgent("Chrome/80.0." + userAgentNum + " (Windows 10; en-US)")
                        .header("Content-Language", "en-US")
                        .timeout(3000)
                        .execute();
                successful = true;
            } catch (IOException io) {
                userAgentNum++;
                count++;
            }
        }
        if (!successful) {
            return products;
        }
        //.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36")


        doc = response.parse();

        Elements picLinks = doc.select("img.s-image");
        String test = "";
        String test2 = "";
        String itemName;
        String picUrl;
        int numItems = 0;
        Random gen = new Random();

        for (Element e : picLinks) {

            //TODO integrate picture link passing, uncomment
            //This line retrieves the picture URL
            picUrl = e.attr("src");

            //This line retrieves the names of the items and makes ProductPrices based on them
            itemName = e.attr("alt");

            //TODO integrate UPC fetching, not just using a random number
            products.add(new ProductPrice(new Product(Integer.toString(gen.nextInt()), itemName), picUrl));
            numItems++;

        }

        //Select all product names, archaic (seems unreliable
        /*
        Elements names = doc.select("span.a-size-base-plus.a-color-base.a-text-normal");
        for (Element f : names) {
            test = test + f.html() + "\n";
        }*/

        //Select all prices
        Elements priceWhole = doc.select("span.a-price-whole");
        Elements priceFraction = doc.select("span.a-price-fraction");
        String temp;
        if (priceWhole.size() == numItems) {
            for (int i = 0; i < numItems; i++) {
                //Get entire HTML tag, containing whole price in dollars and junk
                temp = priceWhole.get(i).html();
                //Ignore junk, get whole price in numbers, then decimal and cents
                temp = temp.substring(0, temp.indexOf("<")) + "." + priceFraction.get(i).html();
                //Cast price String as float
                products.get(i).setPrice(parseFloat(temp));
            }
        }

        //Select all SKUs
        Elements skus = doc.select("div[data-asin]");

        if (skus.size() == numItems) {
            for (int i = 0; i < numItems; i++) {
                temp = skus.get(i).attr("data-asin");
                //Cast price String as float
                products.get(i).setAsin(temp);
            }
        }
        return products;
    }

    public String ASINtoUPC(String ASIN) throws IOException {
        String searchUrl = "https://www.amazon.com/dp/" + ASIN;
        Document doc;
        Connection.Response response = null;

        boolean successful = false;
        int count = 0;
        //TODO distinguish 503 from other errors
        while (!successful && count < 20) {
            try {
                response = Jsoup.connect(searchUrl)
                        .userAgent("Chrome/80.0." + userAgentNum + " (Windows 10; en-US)")
                        .header("Content-Language", "en-US")
                        .timeout(3000)
                        .execute();
                successful = true;
            } catch (IOException io) {
                userAgentNum++;
                count++;
            }
        }
        if (!successful) {
            return Integer.toString(-1);
        }
        doc = response.parse();


        Elements upcs = doc.select("td.bucket");
        String temp = upcs.get(0).html();
        try {
            temp = temp.substring(temp.indexOf("UPC"));
            temp = temp.substring(temp.indexOf(">") + 2);
            temp = temp.substring(0, temp.indexOf("<"));
        }
        catch (IndexOutOfBoundsException ie) {
            temp = Integer.toString(-1);
        }
        if (temp.indexOf(" ") > -1) {
            temp = temp.substring(0, temp.indexOf(" "));
        }

        return temp;
    }

    //TODO I (Jack) think we should implement this.
    public float getPrice(Product product) throws IOException{
        String name = product.getName();
        List<Product> products = new ArrayList<Product>();
        StringBuilder newProduct = new StringBuilder(name);
        for (int i = 0; i < newProduct.length(); i++) {
            if (newProduct.charAt(i) == ' ') {
                newProduct.deleteCharAt(i);
                newProduct.insert(i, "%20");
            }
        }
        String searchUrl = "https://www.amazon.com/s?k=" + newProduct.toString() + "&i=amazonfresh";

        Connection.Response response = null;

        boolean successful = false;
        int count = 0;
        //TODO distinguish 503 from other errors
        while (!successful && count < 20) {
            try {
                response = Jsoup.connect(searchUrl)
                        .userAgent("Chrome/80.0." + userAgentNum + " (Windows 10; en-US)")
                        .header("Content-Language", "en-US")
                        .timeout(3000)
                        .execute();
                successful = true;
            } catch (IOException io) {
                userAgentNum++;
                count++;
            }
        }
        if (!successful) {
            return -1;
        }

        Document doc = response.parse();

        Elements picLinks = doc.select("img.s-image");
        String test = "";
        String test2 = "";
        String price = "";
        int inc = 0;
        /*
        //Unnecessary for this function
        for (Element e: picLinks) {

            //Image link
            test = test + e.attr("src") + "\n";
            //Name of item
            test2 = test2 + e.attr("alt") + "\n";
        }

        //Select all product names
        Elements names = doc.select("span.a-size-base-plus.a-color-base.a-text-normal");
        for (Element f: names) {
            test = test + f.html() + "\n";
            inc++;
        }*/

        //Select all prices
        Elements prices = doc.select("span.a-offscreen");
        String temp = prices.toString();
        try {
            temp = temp.substring(temp.indexOf("$"));
            price = temp.substring(0, temp.indexOf("<"));
        }
        catch (StringIndexOutOfBoundsException se) {
            return -1;
        }
        /*
        //Doesn't work for some reason
        for (Element g: prices) {
            //test = test + g.html() + "\n";
            if (inc == 0) {
                price = g.html();
            }
            inc++;
        }*/


        //Select all SKUs
        /*
        //Unnecessary for this function
        Elements skus = doc.select("div[data-asin]");
        for (Element h: skus) {
            test = test + h.attr("data-asin") + "\n";
            inc++;
        }*/


//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(get_url))
//                .GET()
//                .build();
//        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        //TODO response is a Jsonobj.toString(), if there is a need to write this myself I can start here.


        //System.out.println(test);
        try {
            return Float.parseFloat(price.substring(1));
        }
        catch (Exception e) {
            return -1;
        }

    }


}
