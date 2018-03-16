/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.gte;
import org.bson.Document;
import java.util.LinkedHashSet;
import java.util.List;



public class Connection 
{
    static MongoClientURI connectionString = null;
    static MongoClient mongoClient = null;
    static MongoDatabase db = null;
      
    Connection()
    {
       connectionString = new MongoClientURI("mongodb://ghada:ghada@ds247347.mlab.com:47347/search_engine");
       mongoClient = new MongoClient(connectionString);  //create mongo client
       System.out.println("Connected to the database successfully"); 
       db = mongoClient.getDatabase(connectionString.getDatabase()); //accessing database
           
    }
   
    public void ReadDB(LinkedHashSet NotVisit,LinkedHashSet Visited,LinkedHashSet Content,List InCounter)
    {
       
        MongoCollection CollectionVisit = db.getCollection("Visit");
        MongoCollection CollectionNotVisit = db.getCollection("NotVisit");
        System.out.println("Collection CollectionVisit selected successfully");
        System.out.println("Collection CollectionNotVisit selected successfully");
        //read all info belongs to visits wesites
       // int id=0;
        FindIterable <Document> DocVisit = CollectionVisit.find(gte("id", 0));    
        for (Document myDoc : DocVisit) 
        {         	  
            String url = myDoc.get("url").toString();
            String content=myDoc.get("content").toString();
            String incounter=myDoc.get("incounter").toString();
            Visited.add(url);
            Content.add(content);
            InCounter.add(incounter);
            System.out.println(".......... "+url);
            System.out.println(".......... "+content);
            System.out.println(".......... "+incounter);
                	  
        }
        //read urls which are not visited
        FindIterable <Document> DocNotVisit = CollectionVisit.find(gte("id", 0));  
        for (Document myDoc : DocNotVisit) 
        {         	  
            String url = myDoc.get("url").toString();
            NotVisit.add(url);
            System.out.println(".......... "+url);
                           	  
        }
        
       
        
             
    }
}