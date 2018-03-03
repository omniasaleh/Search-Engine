import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;
import java.net.UnknownHostException;
import java.util.Collection;

public class main {

    public static void main(String[] args) throws UnknownHostException{


        // Standard URI format: mongodb://[dbuser:dbpassword@]host:port/dbname
        MongoClientURI connectionString = new MongoClientURI("mongodb://ghada:ghada@ds247347.mlab.com:47347/search_engine");
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase db = mongoClient.getDatabase(connectionString.getDatabase());
        MongoCollection words = db.getCollection("Words");
        Document doc =new Document("name","commuication");
        words.insertOne(doc);
    }
}