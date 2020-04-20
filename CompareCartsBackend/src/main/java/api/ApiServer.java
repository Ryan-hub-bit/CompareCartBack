package api;

import com.google.gson.Gson;
import dao.*;
import exception.ApiError;
import exception.DaoException;
import fetch.AmazonFetch;
import fetch.WalmartFetch;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJson;
import javafx.util.Pair;
import model.*;
import org.json.simple.parser.ParseException;
import org.jsoup.HttpStatusException;

import java.util.Set;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ApiServer {

    public static int PORT = 9999;
    public static boolean INITIALIZE_WITH_SAMPLE_DATA = false;
    private static Javalin app;

    private ApiServer() {

    }

    public static void start() throws InterruptedException, ParseException, IOException {
        SourceDao sourceDao = DaoFactory.getSourceDao();
        ProductDao productDao = DaoFactory.getProductDao();
        GroceryListDao groceryListDao = DaoFactory.getGroceryListDao();
        AccountDao accountDao = DaoFactory.getAccountDao();
        Product_DetailsDao product_detailsDao = DaoFactory.getProduct_DetailsDao();
        if (INITIALIZE_WITH_SAMPLE_DATA) {
            DaoUtil.addSampleProducts(productDao,product_detailsDao,sourceDao);
            DaoUtil.addSampleGroceryList(groceryListDao);
            DaoUtil.addSampleProductsToList(groceryListDao);
        }
        app = startJavalin();
        getHomepage();
        getProductByUPC(productDao);
        getGroceryListByAccountID(groceryListDao);
        addProductToList(groceryListDao, productDao,product_detailsDao,sourceDao);
        getGroceryLists(groceryListDao);
        GetProductsFromList(groceryListDao, productDao,product_detailsDao,sourceDao);
        addNewListForUser(groceryListDao);
        GetItemFromName();
        deleteList(groceryListDao);
        deleteProductFromList(groceryListDao);
        addNewUser(accountDao);
        verifyUser(accountDao);
        shareList(groceryListDao, accountDao);
        OptimizeList(groceryListDao, sourceDao, productDao);
        app.exception(ApiError.class, (exception, ctx) -> {
            ApiError err = (ApiError) exception;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("status", err.getStatus());
            jsonMap.put("errorMessage", err.getMessage());
            ctx.status(err.getStatus());
            ctx.json(jsonMap);
        });

        app.after(ctx -> {
            ctx.contentType("application/json");
        });
    }
    public static void stop() {
        app.stop();
    }

    private static Javalin startJavalin() {
        Gson gson = new Gson();
        JavalinJson.setFromJsonMapper(gson::fromJson);
        JavalinJson.setToJsonMapper(gson::toJson);
        return Javalin.create().start(PORT);
    }

    private static void getHomepage() {
        app.get("/", ctx -> ctx.result("Welcome to CompareCarts"));
    }

    private static void getGroceryLists(GroceryListDao groceryListDao) {
        app.get("/lists", ctx -> {
            List<GroceryList> groceryLists = groceryListDao.findAll();
            ctx.json(groceryLists);
            ctx.status(200);
        });
    }

    private static void getProductByUPC(ProductDao productDao) {
        app.get("/products/:upc", ctx -> {
            List<Product> product = productDao.findByUPC((ctx.pathParam("upc")));
            ctx.json(product);
            ctx.status(200);
        });
    }

    private static class listSendback {
        String name;
        //TODO add date
        int listId;
        int numItems;
        public listSendback(GroceryList gl, int numItems) {
            this.listId = gl.getListId();
            this.name = gl.getName();
            this.numItems = numItems;
        }

    }
    private static void getGroceryListByAccountID(GroceryListDao groceryListDao) {
        app.get("/:accountID/lists", ctx -> {
            List<GroceryList> groceryLists = groceryListDao.findListByAccountID(Integer.parseInt(ctx.pathParam("accountID")));
            List<listSendback> send = new ArrayList<listSendback>();
            if (groceryLists.size() == 0) {
                ctx.status(200); //TODO What should the actual status be for this case?
                return;
            }
            for (int i = 0; i < groceryLists.size(); i++) {
                List<String> productIds= groceryListDao.findByListId(groceryLists.get(i).getListId());
                send.add(new listSendback(groceryLists.get(i), productIds.size()));
            }
            ctx.json(send);
            ctx.status(200);
        });
    }

    private static void addProductToList(GroceryListDao groceryListDao, ProductDao productDao,Product_DetailsDao product_detailsDao,SourceDao sourceDao) {

        try {
            app.post("/:accountID/lists/:listID/", ctx -> {
                Product_Detail product_detail = ctx.bodyAsClass(Product_Detail.class);
                Source source = ctx.bodyAsClass(Source.class);
                Product product = ctx.bodyAsClass(Product.class);
                System.out.println("PRODUCT IS " + product.toString());
                System.out.println("SOURCE IS " + source.toString());
                System.out.println("PRODUCT_DETAIL is" + product_detail.toString());
                productDao.add(product,product_detail);
                groceryListDao.addProduct(product.getUpc(), Integer.parseInt(ctx.pathParam("listID")), source);
                try {
                    sourceDao.setPrices(product);
                    ctx.status(200);
                } catch (Exception ex){
                    ctx.status(503);
                }


            });
        } catch (DaoException ex) {
            throw new ApiError(ex.getMessage(), 500);
        }

    }

    private static void deleteProductFromList(GroceryListDao groceryListDao) {
        try {
            app.delete("/deleteProduct/:listID/:upc", ctx -> {
                int listID = Integer.parseInt(ctx.pathParam("listID"));
                int upc = Integer.parseInt(ctx.pathParam("upc"));
                groceryListDao.deleteItem(upc, listID);
                ctx.status(200);
            });
        } catch (DaoException ex) {
            throw new ApiError(ex.getMessage(), 500);
        }
    }

    private static void deleteList(GroceryListDao groceryListDao) {
        try {
            app.delete("/deleteList/:accountID/:listID", ctx -> {
                int accountID = Integer.parseInt(ctx.pathParam("accountID"));
                int listID = Integer.parseInt(ctx.pathParam("listID"));
                groceryListDao.deleteList(accountID, listID);
                ctx.status(200);
            });
        }   catch (DaoException ex) {
            throw new ApiError(ex.getMessage(), 500);
        }
    }

    private static void addNewListForUser(GroceryListDao groceryListDao) {
        app.post("/:accountID", ctx -> {
            GroceryList groceryList = ctx.bodyAsClass(GroceryList.class);
            int accountID = Integer.parseInt(ctx.pathParam("accountID"));
            int requestAccountID = groceryList.getAccountID();
            if (accountID != requestAccountID) {
                throw new ApiError("Account ID does not match current account", 400);
            }
            try {
                groceryListDao.add(groceryList);
                ctx.status(201);
                ctx.json(groceryList);
            } catch (DaoException ex) {
                throw new ApiError(ex.getMessage(), 500);
            }
        });
    }

    private static class PriceList {
        float walmartPrice;
        float amazonPrice;
        String provider;

        public PriceList(float walmartPrice, float amazonPrice) {
            this.walmartPrice = walmartPrice;
            this.amazonPrice = amazonPrice;
        }
    }
    //TODO I forgot to check for quantity
    private static void OptimizeList(GroceryListDao groceryListDao, SourceDao sourceDao, ProductDao productDao) {
        float WALMARTDELIVERYFEE = Float.parseFloat("5.00"); //TODO these are fake fee values.
        float AMAZONDELIVERYFEE = Float.parseFloat("7.00");  //
        app.get("/:accountID/lists/:listID/opt", ctx -> {
            List<String> upcList = groceryListDao.findByListId(Integer.parseInt(ctx.pathParam("listID")));

            Map<Integer, PriceList> upcPriceMap = new HashMap<Integer, PriceList>();
            List<Integer> walmartCheck = new ArrayList<Integer>();
            List<Integer> amazonCheck = new ArrayList<Integer>();
            float walmartDiff = 0;
            float amazonDiff = 0;
            float totalAzPrice = 0;
            float totalWmPrice = 0;
            for (int i = 0; i < upcList.size(); i++) {
                PriceList priceList = new PriceList(-1, -1);
                List<Source> sourceList = sourceDao.findByUPC(upcList.get(i));
                System.out.println("HERE" + sourceList);
                for (int j = 0; j < sourceList.size(); j++) {
                    if (sourceList.get(j).getprovider().equals("Walmart Grocery")) {
                        priceList.walmartPrice = sourceList.get(j).getPrice();
                    }
                    if (sourceList.get(j).getprovider().equals("Amazon Fresh")) {
                        priceList.amazonPrice = sourceList.get(j).getPrice();
                    }
                }

                //TODO I'm open to feedback on the algorithm, we will obviously have to modify it as we add providers but the general idea is the same.
                    priceList.provider = "Amazon Fresh";
                    if (priceList.walmartPrice == -1) {
                        priceList.walmartPrice = 50;
                    }
                    if (priceList.walmartPrice < priceList.amazonPrice) {

                        walmartCheck.add(i);
                        walmartDiff += (priceList.amazonPrice - priceList.walmartPrice);
                    }
                    else {
                        amazonCheck.add(i);
                        amazonDiff += (priceList.walmartPrice - priceList.amazonPrice);
                    }
                    totalAzPrice += priceList.amazonPrice;
                    totalWmPrice += priceList.walmartPrice;
                upcPriceMap.put(i, priceList);
            }
            Boolean allWm = false;
            if (totalWmPrice < totalAzPrice) {
                allWm = true;
                for (int i = 0; i < upcPriceMap.size(); i++) {
                    PriceList pl = upcPriceMap.get(i);
                    pl.provider = "Walmart Grocery";
                    upcPriceMap.put(i, pl);
                }
            }

            if (walmartDiff > WALMARTDELIVERYFEE && !allWm) {
                for (int i = 0; i < walmartCheck.size(); i++) {
                    int index = walmartCheck.get(i);
                    PriceList pl = upcPriceMap.get(index);
                    pl.provider = "Walmart Grocery";
                    upcPriceMap.put(index, pl);
                }
            }

            if (amazonDiff > AMAZONDELIVERYFEE && allWm) {
                for (int i = 0; i < amazonCheck.size(); i++) {
                    int index = amazonCheck.get(i);
                    PriceList pl = upcPriceMap.get(index);
                    pl.provider = "Amazon Fresh";
                    upcPriceMap.put(index, pl);
                }
            }

            //End optimization algorithm

            //TODO I didn't test it, but I don't think we can update Product_Details or Source. Could be problematic if two lists map to the same UPC. What do you guys think? I might be mistaken.
            //TODO no guarantees about this working until UPC fetch works.
            List<ProductSendback> prodList = new ArrayList<ProductSendback>();
            for (int i = 0; i < upcList.size(); i++) {
                //String groceryProvider, String name, String brand, float price
                String upc = upcList.get(i);
                Product prod = productDao.findByUPC(upc).get(0); //TODO why does this return a list?
                PriceList pl = upcPriceMap.get(i);

                //TODO fix the next ten lines, so horrible. I would do it now but desperately need to sleep. I think provider should be an index instead of a string, maybe?
                Boolean wm = false;
                if (pl.provider.equals("Walmart Grocery")) {
                    wm = true;
                }
                if (wm) {
                    ProductSendback prod_send = new ProductSendback(upc, pl.provider, prod.getName(), prod.getDescription(), pl.walmartPrice);
                    groceryListDao.modifyProduct(prod.getUpc(), Integer.parseInt(ctx.pathParam("listID")), "Walmart Grocery");
                    //prodList.add(prod_send);
                }
                else {
                    ProductSendback prod_send = new ProductSendback(upc, pl.provider, prod.getName(), prod.getDescription(), pl.amazonPrice);
                    groceryListDao.modifyProduct(prod.getUpc(), Integer.parseInt(ctx.pathParam("listID")), "Amazon Fresh");
                    //prodList.add(prod_send);
                }

            }

            ctx.status(200);
            //ctx.json(prodList);


        });
    }

    private static void shareList (GroceryListDao groceryListDao, AccountDao accountDao) {
        app.post("/share/:name/:mobileNumber/:listID", ctx -> {
            String mobileNumber = ctx.pathParam("mobileNumber");
            int listID = Integer.parseInt(ctx.pathParam("listID"));
            String name = ctx.pathParam("name");
            int accountID = accountDao.checkNumber(mobileNumber);
            if (accountID != -1) {
                GroceryList groceryList = new GroceryList(listID, name, accountID);
                groceryListDao.share(groceryList);
            }
        });
    }
    private static void addNewUser(AccountDao accountDao) {
        app.post("/welcome/1", ctx -> {
            Account account = ctx.bodyAsClass(Account.class);
            try {
                if (accountDao.add(account)) {
                    ctx.status(201);
                    ctx.json(account);
                } else {
                    ctx.status(403);

                    ctx.result("Account already found");
                }
            } catch (DaoException ex) {
                throw new ApiError(ex.getMessage(), 500);
            }
        });
    }

    private static void verifyUser(AccountDao accountDao) {
        app.post("/login/1", ctx -> {
            Account account = ctx.bodyAsClass(Account.class);
            try {
                Pair<Boolean,Integer> ret = accountDao.checkLogin(account);
                if (ret.getKey()) {
                    ctx.status(202);
                    //ctx.json(account);
                    Map<String, Integer> accID = new HashMap<>();
                    accID.put("account", ret.getValue());
                    ctx.json(accID);
                } else {
                    ctx.status(403);
                    ctx.json(account);
                    ctx.result("Error");
                }
                System.out.println(ret);
            } catch (DaoException ex) {
                throw new ApiError(ex.getMessage(), 500);
            }
        });
    }

    private  static class ProductSendback{
        String upc;
        String groceryProvider;
        String name;
        String brand;
        float price;
        String url;
        int quantity=1;
        public ProductSendback(Product product,Product_Detail product_detail,Source source)
        {
            this.upc = product.getUpc();
            this.groceryProvider = source.getprovider();
            this.name = product.getName();
            this.brand = product_detail.getBrand();
            this.price=source.getPrice();
            this.url = product_detail.getUrl();
            if (this.url == null) {
                this.url = "No image found";
            }
        }

    public ProductSendback(String upc, String groceryProvider, String name, String brand, float price) {
            this.upc = upc;
        this.groceryProvider = groceryProvider;
        this.name = name;
        this.brand = brand;
        this.price = price;
    }

    @Override
    public String toString() {
        return "ProductSendback{" +
                "groceryProvider='" + groceryProvider + '\'' +
                ", itemName='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", url=" + url +
                '}';
    }
}

    //TODO get ProductPrice list from Amazon, check if null
    //TODO integrate AmazonFetch and WalmartFetch
    private static void GetProductsFromList(GroceryListDao groceryListDao, ProductDao productDao,Product_DetailsDao product_detailsDao, SourceDao sourceDao) {
        try {
            app.get("/:accountID/lists/:listID/", ctx -> {
                Map<String, String> upcProviderMap = groceryListDao.findUpcProviderByListId(Integer.parseInt(ctx.pathParam("listID")));

                List<ProductSendback> send = new ArrayList<ProductSendback>();
                if (upcProviderMap.size() == 0) {
                    ctx.status(203); //TODO this should probably have a different code
                    return;
                }
                Set<String> keys = upcProviderMap.keySet();
                for (String upc : keys) {
                    List<Source> sourceList = sourceDao.findByUPC(upc);
                    System.out.println(sourceList);
                    Source source = new Source("No upc", "no provider", (float) -1);
                    for (int i = 0; i < sourceList.size(); i++) {
                        if (sourceList.get(i).getprovider().equals(upcProviderMap.get(upc))) {
                            source = sourceList.get(i);
                            System.out.println("here in sourceLIst loop");
                            Product product= productDao.findByUPC(upc).get(0);//TODO this is wrong findByUPC will return a list. Fix it later.
                            Product_Detail product_detail = product_detailsDao.findByUPC(upc).get(0);
                            System.out.print(product_detail);
                            ProductSendback sendProd = new ProductSendback(product,product_detail,source);
                            System.out.println(sendProd);
                            send.add(sendProd);
                        }
                    }
                }

//                for (int i = 0; i < listItems.size(); i++) {
//                    List<Source> sourceList = sourceDao.findByUPC(listItems.get(i));
//                    Source source = new Source("No upc", "no provider", (float) -1);
//                    for (int j = 0; j < sourceList.size(); j++) {
//                        if (sourceList.get(j).getprovider().equals(listItems.get(0))) {
//
//                        }
//                        if (sourceList.get(j).getprovider().equals("amazon")) {
//                            priceList.amazonPrice = sourceList.get(j).getPrice();
//                        }
//                    }

                ctx.status(200);
                ctx.json(send);
            });
        } catch (DaoException ex) {
            throw new ApiError(ex.getMessage(), 500);
        }


    }

    private static class Stringw {
        String name;
    }

    private static class itemSendback {

        String groceryProvider;
        String name;
        String upc;
        float price;
        String img = "No link provided";

        public itemSendback(String groceryProvider, String itemName, String upc, float price) {
            this.groceryProvider = groceryProvider;
            this.name = itemName;
            this.upc = upc;
            this.price = price;
        }
        public itemSendback(String groceryProvider, String itemName, String upc, float price, String img) {
            this.groceryProvider = groceryProvider;
            this.name = itemName;
            this.upc = upc;
            this.price = price;
            this.img = img;
        }
    }
    private static void GetItemFromName() {
        app.post("/:accountID/product", ctx -> {
            AmazonFetch af = new AmazonFetch();
            WalmartFetch wf = new WalmartFetch();
            Boolean error = false;
            Stringw namew = ctx.bodyAsClass(Stringw.class);
            String name = namew.name;
            List<ProductPrice> products = wf.getItems(name);
            List<ProductPrice> AmazonProducts = null;
            try {
                AmazonProducts = af.getItems(name);
            }
            catch (HttpStatusException ex) {
                ctx.status(503);
                error = true;
            }

            List<itemSendback> send = new ArrayList<itemSendback>();
            try {
                for (int i = 0; i < 5; i++) {
                    ProductPrice productprice = AmazonProducts.get(i);
                    Product product = productprice.getProduct();
                    send.add(new itemSendback("Amazon Fresh", product.getName(), product.getUpc(), productprice.getPrice(), productprice.getUrl()));
                }

                for (int i = 0; i < 5; i++) {
                    ProductPrice productprice = products.get(i);
                    Product product = productprice.getProduct();
                    send.add(new itemSendback("Walmart Grocery", product.getName(), product.getUpc(), productprice.getPrice(), productprice.getUrl()));
                }
            } catch (Exception ex) {
                ctx.status(500);
                error = true;
            }

            if (!error) {
                ctx.status(200);
            }
            ctx.json(send);

        });

    }
}
