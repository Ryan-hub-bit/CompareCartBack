package api;

import dao.DaoFactory;
import fetch.AmazonFetch;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Paths;

public class RunApiServer {
    public static void main(String[] args) throws InterruptedException, ParseException, IOException {
        DaoFactory.PATH_TO_DATABASE_FILE = Paths.get("src", "main", "resources").toFile().getAbsolutePath()
                + "/db/Store.db";
        ApiServer.start();
    }
}
