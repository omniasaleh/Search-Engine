/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import static crawler.Spider.md5Hash;
import java.io.IOException;
import java.net.SocketException;
import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author menna
 */
public class Respider extends Thread
{
    private final MyData MyData;
    Respider(MyData MyData)
    {
        this.MyData=MyData;
    }
    
    
    @Override
    public void run ()
    {
        while(MyData.GetVisited().size()<MyData.MAX_RE)
        {
            Pair<String, String> url; 
            url = MyData.fetch(); 
            if(url!=null)   
            SimpleReCrawl(url); 
              
        }
        
    }
    
    
    
    public  void SimpleReCrawl(Pair<String, String> url)
    { String parent;
      String in;    
      String id;
       try
        {  
            org.jsoup.Connection connection = Jsoup.connect(url.getKey()).timeout(0).followRedirects(true);
            Document htmlDocument = connection.get();
            int response =connection.response().statusCode();
            if(response == 200) //http
            {   
            System.out.println("the response =200 request page " + url.getKey());
            if(htmlDocument.body()!=null)
            {
                
            String text = htmlDocument.body().text();
            String hashword=md5Hash(text);    
            MongoCollection collection = MyData.db.getCollection("Visit");
            BasicDBObject query = new BasicDBObject("Url", new BasicDBObject("$eq",url.getKey()));
            FindIterable <org.bson.Document> doc = collection.find(query);
            org.bson.Document mydoc=doc.first();
            if(mydoc!=null) //the url exist in my db
                { 
                    in = mydoc.get("InCounter").toString();
                    String con= mydoc.get("Content").toString();
                    id =mydoc.get("Id").toString();
                    if(!hashword.equals(con))
                    { 
                        System.out.println("there is a modified document");
                        //collection.updateOne(eq(new BasicDBObject("Url",url.getKey())),new BasicDBObject("Content",new BasicDBObject("$set",hashword)));
                        collection.updateOne(eq("Url", url.getKey()), new org.bson.Document("$set", new org.bson.Document("Content", hashword)));
                        MyData.writer(htmlDocument, Integer.parseInt(mydoc.get("Id").toString()),"C:\\Users\\menna\\Desktop\\html_new\\");
                        Elements linksOnPage = htmlDocument.select("a[href]");        
                        MyData.InsertNotVisit(linksOnPage,id);  
                        con=hashword;
                    }
                    MyData.InsertRec(id,url,in,con);
                     
                }
            else
                {
                      
                     MyData.InsertVisit(url,null,hashword,htmlDocument);
            
                }
            }
            }
              
           else 
            {
                System.out.println("Error retreiving page: " + connection.response().statusCode());
            }
           
                
        }
        catch(SocketException exception)
        {
            System.out.println("\n connection reset " + url);
            //SimpleReCrawl(url);
            
        }
       catch (IOException exception)
       {
       System.out.println("\n We were not successful in our HTTP request " + url);
       }
       
    }
    
    
    
}
