package crawler;
import java.util.LinkedHashSet;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.io.IOException;
import static com.mongodb.client.model.Filters.*;
import java.util.ArrayList;


final class MyData 
{
	//String[] Seeds={"https://yts.am/"};

    private final LinkedHashSet<String> NotVisit;
    private final LinkedHashSet<String>Visited;
    private final LinkedHashSet<String> Content;
    private final LinkedHashSet<String> Processing;
    private final List<Integer> InCounter;
    private static final int MAX_IT =10;
       
    //fetch url from nonvisited and check if it is in visited list or proccessing list
    MyData() throws IOException
    {
    NotVisit = new LinkedHashSet();
    Visited=new LinkedHashSet();
    Content=new LinkedHashSet<>();
    Processing=new LinkedHashSet<>();
    InCounter=new ArrayList<>();
    ReadDB();
    }
        
        
        
        
				  
        //fetch url from nonvisited and check if it is in visited list or proccessing list
    public  synchronized String fetch()
        {
            if(!GetNotVisit().isEmpty())
            {
            String URL;
            URL=this.NotVisit.iterator().next();
            this.NotVisit.remove(this.NotVisit.iterator().next());
            URL=processURL(URL);
            System.out.println("no visited "+GetNotVisit().size());
            if(!this.Visited.contains(URL)&&!this.Processing.contains(URL))
            {   this.Processing.add(URL);
                return URL;
            }
            else if(this.Visited.contains(URL))
            {
               int i = new ArrayList<>(this.Visited).indexOf(URL);
               IncInCounter(i);
            }
            }
            return null;
        }


    public synchronized void InsertNotVisit(Elements l)
        {   int count =0;
        if(Visited.size()+NotVisit.size()<MAX_IT)
        { //System.out.println("insert notvisited from thread "+Thread.currentThread().getId());
            //System.out.println("****inseeeeeeeeeeeeeeeeeeeert*******************************");  
            for(Element link : l)
                {
                    if(link.absUrl("href")!=""&&!this.Visited.contains(link.absUrl("href"))&&!this.NotVisit.contains(link.absUrl("href")))

                    //if(link.absUrl("href")!=""&&!this.Visited.contains(link.absUrl("href"))&&!this.NotVisit.contains(link.absUrl("href"))&&!this.HashCodes.contains(content.hashCode()))
                    { this.NotVisit.add(link.absUrl("href"));
                        count++;
                    }

                }
            System.out.println("the size of inserted pages "+ count);
           // System.out.println("size of notvisited "+this.NotVisit.size());
          //  System.out.println("size of visited "+this.Visited.size());
        }
        }


    public String processURL(String theURL) 
    {
        int endPos;
        if (theURL.indexOf("?") > 0) 
        {
            endPos = theURL.indexOf("?");
        } 
        else if (theURL.indexOf("#") > 0) 
        {
             endPos = theURL.indexOf("#");
        } 
        else 
        {
            endPos = theURL.length();
        }
        return theURL.substring(0, endPos);
    }

    public synchronized boolean InsertVisit(String url,String content,String title)
        {//!this.ContentVisited.contains(content)&&
         //!this.Visited.contains(url)&&
            if(this.Content.add(content))
            {
             System.out.println("the visited url "+url);
             this.Visited.add(url);
             this.InCounter.add(0);
             System.out.println("size of content "+this.Content.size());
             System.out.println("size of visited "+this.Visited.size());
             System.out.println("size of counter "+this.InCounter.size());
             return true;       
            }
            //else if(this.Visited.contains(url))
            //{
            //    System.out.println("*////////////////incereemeeeeeeeent*******************");
                //int i = new ArrayList<>(this.Visited).indexOf(url);
              //  IncInCounter(i);}


            return false;
        }

    public synchronized LinkedHashSet GetContent()
    {
        return Content;
    }

    public synchronized LinkedHashSet GetVisited()
    {
        return Visited;
    }

    public synchronized LinkedHashSet GetNotVisit()
    {
        return NotVisit;
    }
     public synchronized void IncInCounter(int index)
    { 
       InCounter.set(index,(InCounter.get(index)+1));
    }
     public List<Integer> GetInCounter()
    { 
       return InCounter;
    }

     public int MAX_IT()
     {
         return MAX_IT;
     }

    public void ReadDB()
    {
        System.out.println("Read from database");
        MongoClientURI connectionString = new MongoClientURI("mongodb://ghada:ghada@ds247347.mlab.com:47347/search_engine");
        MongoClient mongoClient = new MongoClient(connectionString);  //create mongo client
        System.out.println("Connected to the database successfully"); 
        MongoDatabase  db = mongoClient.getDatabase(connectionString.getDatabase()); //accessing database

        MongoCollection CollectionVisit = db.getCollection("Visit");
        MongoCollection CollectionNotVisit = db.getCollection("NotVisit");
        System.out.println("Collection CollectionVisit selected successfully");
        System.out.println("Collection CollectionNotVisit selected successfully");
        
        //read all info belongs to visits wesites
        FindIterable <Document> DocVisit = CollectionVisit.find();    
        for (Document myDoc : DocVisit) 
        {         	  
            String url = myDoc.get("Url").toString();
            String content=myDoc.get("Content").toString();
            int incounter=Integer.parseInt(myDoc.get("InCounter").toString());
            this.Visited.add(url);
            this.Content.add(content);
            this.InCounter.add(incounter);
            System.out.println(".......... "+url);
            System.out.println(".......... "+content);
            System.out.println(".......... "+incounter);

        }
        
        //read urls which are not visited
        FindIterable <Document> DocNotVisit = CollectionNotVisit.find();      
        for (Document myDoc : DocNotVisit) 
        {   
            String url = myDoc.get("Url").toString();
            this.NotVisit.add(url);
            System.out.println(".......... "+url);

        }
        


    }




}