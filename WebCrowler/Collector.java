
package crawler;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.Iterator;
import org.bson.Document;


/**
 *
 * @author menna
 */
public class Collector 
{
    private final MyData MyData;
    static MongoClientURI connectionString;
    static MongoClient mongoClient;
    static MongoDatabase db ;
    MongoCollection CollectionVisit ;
    MongoCollection CollectionNotVisit; 
    Collector(MyData MyData)
    {
        this.MyData=MyData;
        connectionString = new MongoClientURI("mongodb://ghada:ghada@ds247347.mlab.com:47347/search_engine");
        mongoClient = new MongoClient(connectionString);
        System.out.println("Connected to the database successfully"); 
        db = mongoClient.getDatabase(connectionString.getDatabase()); //accessing database
        CollectionVisit = db.getCollection("Visit");
        CollectionNotVisit=db.getCollection("NotVisit");
       
    }
    
   /* @Override
    public void run ()
    { 
        /*while(GetCount()<10&&!MyData.GetNotVisit().isEmpty())
        {
            System.out.println("#####&&&&&&&****** i am writing... *******&&&&&######  ");
            try 
            {
                WriteFile(MyData.GetNotVisit(), MyData.GetVisited(), MyData.GetContent(), MyData.GetInCounter());
                sleep(10000);
                
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(Collector.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (InterruptedException ex) 
            {
                Logger.getLogger(Collector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
      //  System.out.println("finished to write");
        
  //  }
    
    public void WriteDB()
    {
     
        //insert all info belongs to visited url
        for(int i=GetCount();i<new ArrayList(MyData.GetVisited()).size();i++) 
        {
            String url=(String) new ArrayList(MyData.GetVisited()).get(i); 
            String con = (String) new ArrayList(MyData.GetContent()).get(i);
            int in = MyData.GetInCounter().get(i);
            Document document = new Document("Id",i) 
            .append("Url", url)
            .append("Content",con)
            .append("InCounter",in);       
            CollectionVisit.insertOne(document); 
            System.out.println("Document inserted successfully"); 
            
             
         }
         
        //insert all not visit ursl
        Iterator<String> notvisit = MyData.GetNotVisit().iterator();
          while(notvisit.hasNext())
         {
            Document document = new Document("Url",notvisit.next());  
            
           if(CollectionNotVisit.count(Filters.eq("Url",document))>0)
           { CollectionNotVisit.insertOne(document); 
            System.out.println("Document inserted successfully"); 
           }
             
         }
         
         
     
    }
            
    public int GetCount(){ return (int)CollectionVisit.count(); }
               
    
}
