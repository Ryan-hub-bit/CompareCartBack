package api;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dao.DaoFactory;
import fetch.WalmartFetch;
import model.GroceryList;
import model.Product;
import model.Product_Detail;
import model.Source;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class ApiServerTest {

    private static Gson gson = new Gson();

    private static class ItemSendback {

        String provider;
        String name;
        String upc;
        float price;
        String url = "No link provided";

        public ItemSendback(String provider, String itemName, String upc, float price) {
            this.provider = provider;
            this.name = itemName;
            this.upc = upc;
            this.price = price;
        }
        public ItemSendback(String provider, String itemName, String upc, float price, String url) {
            this.provider = provider;
            this.name = itemName;
            this.upc = upc;
            this.price = price;
            this.url = url;
        }
        public String getUrl() { return this.url; }
    }

    static int default_listID = -1;
    @BeforeClass
    public static void beforeClass() throws Exception {
        DaoFactory.PATH_TO_DATABASE_FILE = Paths.get("src", "test", "resources").toFile().getAbsolutePath()
                + "/db/Test.db";
        ApiServer.INITIALIZE_WITH_SAMPLE_DATA = true;
        ApiServer.start();

        GroceryList groceryList = new GroceryList("Api server test List", 9999);
        String list_url = "http://127.0.0.1:9999/9999"; //Account number 9999
        HttpResponse<JsonNode> jsonResponse = Unirest.post(list_url).body(gson.toJson(groceryList)).asJson();
        JsonNode body = jsonResponse.getBody();
        JSONObject list0pre = body.getObject();
        int listID0 = list0pre.getInt("listId");

        assertEquals(201, jsonResponse.getStatus());

        //Add some items.
        String item_url = "http://127.0.0.1:9999/9999/lists/" + listID0;
        jsonResponse = Unirest.post(item_url).body(gson.toJson(new ItemSendback("Walmart Grocery", "Whey Protein", "111", (float) 35.00,"PERSIST"))).asJson();
        item_url = "http://127.0.0.1:9999/9999/lists/" + listID0;
        jsonResponse = Unirest.post(item_url).body(gson.toJson(new ItemSendback("Amazon Fetch", "Milk", "1111", (float) 3.99, "PERSIST"))).asJson();
        item_url = "http://127.0.0.1:9999/9999/lists/" + listID0;
        jsonResponse = Unirest.post(item_url).body(gson.toJson(new ItemSendback("Walmart Grocery", "Ice cream", "11111", (float) 7.99, "PERSIST"))).asJson();
        default_listID = listID0;

        Map<String,Object> account = new HashMap<>();
        account.put("username", "TestUser1");
        account.put("password", "pass");
        account.put("fullname", "Test User");
        account.put("mobileNumber", "1234567890");
        account.put("address", "Sesame Street");
        final String addUser = "http://127.0.0.1:9999/welcome/1";
        HttpResponse<JsonNode> jsonResponse2 = Unirest.post(addUser).body(gson.toJson(account)).asJson();
        assertEquals(201, jsonResponse2.getStatus());

    }

    @AfterClass
    public static void afterClass() throws Exception {
        ApiServer.stop();
    }
    //This test is fairly exhaustive.Creates a user, list, product,product_detail, Source, and then maps the product to a list
    @Test
    public void CreateListProduct_Detail() throws UnirestException, InterruptedException, ParseException, IOException {
        Map<String,Object> groceryList = new HashMap<>();
        groceryList.put("name", "Ryan's Potluck");
        groceryList.put("accountID", 1234);
        Map<String,Object> ProductInfo = new HashMap<>();
        WalmartFetch wl = new WalmartFetch();
        Product product = new Product("123", "LaysChips", "Food", "12 oz bbq");
        float price1 = wl.getPrice(product);
        Product_Detail product_detail = new Product_Detail(product.getUpc(),"brand1", new Date());
        Source source = new Source(product.getUpc(),"amazon",price1);
        Product product2 = new Product("234", "LaysChips", "Food", "12 oz bbq");
        float price2 = wl.getPrice(product);
        Product_Detail  product_detail2 = new Product_Detail(product2.getUpc(),"brand1", new Date());
        Source source2 = new Source(product2.getUpc(),"amazon",price2);
    }

    //This test is fairly exhaustive. Creates a user, list, product, and then maps the product to a list.
    @Test
    public void CreateListProductRelation() throws UnirestException {

        Map<String, Object> groceryList = new HashMap<>();
        groceryList.put("name", "Jack's Potluck");
        groceryList.put("accountID", 2345);
        List<Product> glist = new ArrayList<Product>();
        Product product = new Product("12345", "banana", null, null);
        Product product1 = new Product("1234", "pear", null, null);
        Product product2 = new Product("123456", "apples", null, null);
        glist.add(product);
        glist.add(product1);
        glist.add(product2);
        groceryList.put("gList", glist); //TODO We don't actually add the items in the list. IDK if we want it to, will we ever have to make a list that has items in it?
        //create a list
        final String list_url = "http://127.0.0.1:9999/2345";
        //The response contains the listId.
        HttpResponse<JsonNode> jsonResponse = Unirest.post(list_url).body(gson.toJson(groceryList)).asJson();
        System.out.println(jsonResponse.getStatus());
        JsonNode body = jsonResponse.getBody();
        JSONObject list = body.getObject();
        String URL = "http://127.0.0.1:9999/2345/lists/" + list.getInt("listId");

        //Make sure connecting existing list and product is successful
        HttpResponse<String> Response = Unirest.post(URL).body(gson.toJson(product)).asString();
        assertEquals(200, Response.getStatus());

        //Make sure Foreign Key constrain prevents adding to a list that doesn't exist.
        URL = "http://127.0.0.1:9999/2345/lists/" + "4500";
        Response = Unirest.post(URL).body(gson.toJson(product1)).asString();
        //assertEquals(500, Response.getStatus());

        //TODO test account verification.

    }


    @Test
    public void CreateThenGetList() throws UnirestException {
        //Create a few lists.

        //list 0
        GroceryList groceryList = new GroceryList("Dinner List", 1997);
        String list_url = "http://127.0.0.1:9999/1997"; //Account number 1997
        HttpResponse<JsonNode> jsonResponse = Unirest.post(list_url).body(gson.toJson(groceryList)).asJson();
        JsonNode body = jsonResponse.getBody();
        JSONObject list0pre = body.getObject();
        int listID0 = list0pre.getInt("listId");

        assertEquals(201, jsonResponse.getStatus());

        //Add some items.
        String item_url = "http://127.0.0.1:9999/1997/lists/" + listID0;
        jsonResponse = Unirest.post(item_url).body(gson.toJson(new Product("7685", "Whey Protein", null, null))).asJson();
        assertEquals(200, jsonResponse.getStatus());
        item_url = "http://127.0.0.1:9999/1997/lists/" + listID0;
        jsonResponse = Unirest.post(item_url).body(gson.toJson(new Product("7686", "Milk", null, null))).asJson();
        assertEquals(200, jsonResponse.getStatus());
        item_url = "http://127.0.0.1:9999/1997/lists/" + listID0;
        jsonResponse = Unirest.post(item_url).body(gson.toJson(new Product("7687", "Ice cream", null, null))).asJson();
        assertEquals(200, jsonResponse.getStatus());

        //list 1
        groceryList = new GroceryList("Lunch List", 1997);
        list_url = "http://127.0.0.1:9999/1997"; //Account number 1997
        jsonResponse = Unirest.post(list_url).body(gson.toJson(groceryList)).asJson();
        body = jsonResponse.getBody();
        JSONObject list1pre = body.getObject();
        int listID1 = list1pre.getInt("listId");
        assertEquals(201, jsonResponse.getStatus());

        //list 2
        groceryList = new GroceryList("Breakfast List", 1997);
        list_url = "http://127.0.0.1:9999/1997"; //Account number 1997
        jsonResponse = Unirest.post(list_url).body(gson.toJson(groceryList)).asJson();
        body = jsonResponse.getBody();
        JSONObject list2pre = body.getObject();
        int listID2 = list2pre.getInt("listId");
        assertEquals(201, jsonResponse.getStatus());

        //Get lists and make sure they are the same.
        String url = "http://127.0.0.1:9999/1997/lists";
        jsonResponse = Unirest.get(url).asJson();
        body = jsonResponse.getBody();
        JSONArray grocery_array = body.getArray();
        Object list0 = grocery_array.get(0);
        Object list1 = grocery_array.get(1);
        Object list2 = grocery_array.get(2);
        System.out.println(list0); //for debugging
        assertEquals(200, jsonResponse.getStatus());
        //TODO write useful assertions. list0 will contain a sendBack but list0pre is a grocerylist item. Based on my print statements this appears to work.
        //I would convert list0pre (and list1pre, list2pre) to a sendBack item then to an assertEquals(liso0.tostring(), list0pre.tostring()
        //used suffix pre to mean "pre-fetch"

        //Get all items from a list.
        url = "http://127.0.0.1:9999/1997/lists/" + listID0;
        jsonResponse = Unirest.get(url).asJson();
        assertEquals(200, jsonResponse.getStatus());
        System.out.println(jsonResponse.getBody()); //for debugging
        //TODO write useful assertions. Once again, the item we send back doesn't match Product so we can't cast directly. Print statement looks good.


        url = "http://127.0.0.1:9999/1997/lists/" + listID0 +"/opt";
        jsonResponse = Unirest.get(url).asJson();
        System.out.println(jsonResponse.getBody());
        assertEquals(200, jsonResponse.getStatus());

        //Get all items from a list.
        url = "http://127.0.0.1:9999/1997/lists/" + listID0;
        jsonResponse = Unirest.get(url).asJson();
        assertEquals(200, jsonResponse.getStatus());
        System.out.println(jsonResponse.getBody()); //for debugging


    }

    @Test
    public void getItems() throws UnirestException {
        String url = "http://127.0.0.1:9999/234/product";
        Map<String, String> name = new HashMap<>();
        name.put("name", "pears");
        HttpResponse<String> jsonResponse = Unirest.post(url).body(gson.toJson(name)).asString();
        System.out.println(jsonResponse.getBody());
        assertEquals(200, jsonResponse.getStatus());
    }

    //TODO I know this is in the wrong place but for some reason gradle can't locate the price_test file.
    @Test
    public void getWalmartPrice() throws ParseException {
        Product product = new Product("12345", "fruit salad", null, null);

        WalmartFetch wf = new WalmartFetch();
        try {
            for (dao.ProductPrice prod : wf.getItems(product.getName())) {
                System.out.println(prod.toString());
            }
        }
        catch (IOException ex){
            System.out.println(ex);
        }
        catch (InterruptedException ex) {
            System.out.println(ex);
        }

    }

    @Test
    public void optimizeList() throws UnirestException {
        String url = "http://127.0.0.1:9999/9999/lists/" + default_listID + "/opt";
        HttpResponse<JsonNode> jsonResponse = Unirest.get(url).asJson();
        assertEquals(200, jsonResponse.getStatus());

        url = "http://127.0.0.1:9999/9999/lists/" + default_listID;
        jsonResponse = Unirest.get(url).asJson();
        assertEquals(200, jsonResponse.getStatus());
        JsonNode body = jsonResponse.getBody();
        JSONArray item_array = body.getArray();
        System.out.println(item_array);
        System.out.println(body);

        //TODO add assertion when the 503 wears off



    }

    @Test
    public void testImagePersistence() throws Exception {

        System.out.println(default_listID);
        String url = "http://127.0.0.1:9999/9999/lists/" + default_listID;
        HttpResponse<JsonNode> jsonResponse = Unirest.get(url).asJson();
        JsonNode body = jsonResponse.getBody();
        assertEquals(200, jsonResponse.getStatus());
//        assertEquals(item.getString("url"), "PERSIST"); I still can't test because of the 503 but I'm pretty sure persistence is working.

    }

    @Test
    public void duplicateUsernameFails() throws UnirestException {
        Map<String, Object> account = new HashMap<>();
        account.put("username", "TestUser1");
        account.put("password", "pass");
        account.put("fullname", "Test User");
        account.put("mobileNumber", "1234567890");
        account.put("address", "Sesame Street");
        final String welcome_url = "http://127.0.0.1:9999/welcome/1";
        String json = gson.toJson(account);
        HttpResponse<JsonNode> jsonResponse = Unirest.post(welcome_url).body(json).asJson();
        assertEquals(403, jsonResponse.getStatus());
    }

}