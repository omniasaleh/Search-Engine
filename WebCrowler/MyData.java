package crawler;
import com.mongodb.BasicDBObject;
import java.util.LinkedHashSet;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.BufferedReader;
import org.bson.Document;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.util.Pair;



final class MyData 
{

    private final Map<String,String> NotVisit;; 
    private final Map<String,String> Visited; 
    private final LinkedHashSet<String> Content;
    private final Map<String,String> Processing;
    private final List<Integer> InCounter;
    public static final int MAX_IT =2000;  //hy3ml crawling 5000 visit
    public static final int MAX_RE =200;   //hy3ml recrawling l7d 2000 visit
    MongoCollection CollectionVisit ;
    MongoCollection CollectionNotVisit; 
    MongoCollection CollectionRVisit ;
    MongoCollection CollectionRNotVisit; 
    public MongoDatabase db;
    private final String Type; 
    //fetch url from nonvisited and check if it is in visited list or proccessing list
    MyData(MongoDatabase db,String Type) 
    {
    this.Type=Type;
    this.CollectionNotVisit = db.getCollection("NotVisit");
    this.CollectionVisit = db.getCollection("Visit");
    this.CollectionRNotVisit = db.getCollection("RNotVisit");
    this.CollectionRVisit = db.getCollection("RVisit");
    this.NotVisit = Collections.synchronizedMap(new LinkedHashMap());
    this.Visited = Collections.synchronizedMap(new LinkedHashMap());
    this.Content=new LinkedHashSet<>();
    this.Processing=Collections.synchronizedMap(new LinkedHashMap());
    this.InCounter=new ArrayList<>();
    this.db=db;
   // System.out.println("Read from database");
    ReadDBCrawl();
    }
        
        
        
        
				  
        //fetch url from nonvisited and check if it is in visited list or proccessing list
    public synchronized  Pair<String, String> fetch()
        {
            if(!GetNotVisit().isEmpty())
            {
            String URL;
            String parent;
            URL=NotVisit.entrySet().iterator().next().getKey();
            parent=NotVisit.entrySet().iterator().next().getValue();
            this.NotVisit.remove(this.NotVisit.keySet().iterator().next());
            BasicDBObject document = new BasicDBObject();
            document.put("Url",URL);
            if(this.Type.equals("Recrawling"))
                CollectionRNotVisit.deleteOne(document);
            else    
                CollectionNotVisit.deleteOne(document);
            URL=processURL(URL);
            //System.out.println("no visited "+GetNotVisit().size());
          //  System.out.println(URL);
            //System.out.println(Robot(URL));
            if(!this.Visited.containsKey(URL)&&!this.Processing.containsKey(URL)&&Robot(URL))
            {    Pair<String, String> p=new Pair(URL,parent);
                this.Processing.put(URL,parent);
                return p;
            }
            //if(this.Visited.containsKey(URL))
            else if(this.Visited.containsKey(URL))
            {
               //int i = new ArrayList<>(this.Visited).indexOf(URL);
                int i = new ArrayList<>(this.Visited.keySet()).indexOf(URL);
                IncInCounter(i);
                this.Visited.put(URL,new ArrayList<>(this.Visited.values()).get(i)+" "+parent);
                
            }
            }
            return null;
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

    public synchronized void InsertVisit(Pair<String, String> url, Elements linksOnPage,String content,org.jsoup.nodes.Document doc) throws IOException
        {
            if(this.Content.add(content))
            {
            // System.out.println("the visited url "+url.getKey());
             this.Visited.put(url.getKey(), url.getValue());
             this.InCounter.add(1);
             int id =(int)CollectionVisit.count();
             String parent=Integer.toString(id);
             String path;
             
           //  System.out.println("size of content "+this.Content.size());
            // System.out.println("size of visited "+this.Visited.size());
            // System.out.println("size of counter "+this.InCounter.size());  
             //System.out.println("size of notvisit "+this.NotVisit.size());
             
            Document document = new Document("Id",id) 
            .append("Url",url.getKey())
            .append("Parent",url.getValue()) 
            .append("Content",content)
            .append("InCounter",1);       
            CollectionVisit.insertOne(document);
            
            if(this.Type.equals("Crawling"))
                 path="C:\\Users\\menna\\Desktop\\html_old\\";
             else
            {
                path="C:\\Users\\menna\\Desktop\\html_new\\";
                CollectionRVisit.insertOne(document);
            }
                 
             writer(doc,id,path);
            
          //  System.out.println("Document inserted successfully");
            if(Visited.size()+NotVisit.size()<MAX_IT&&linksOnPage!=null)
                {     
                   InsertNotVisit(linksOnPage,parent);
                }
                
            }
           
        }
    
    
    
    public synchronized void InsertNotVisit(Elements linksOnPage,String parent)
    { //System.out.println("size of retrive "+linksOnPage.size());
         for(Element link : linksOnPage)
                { String Link=link.absUrl("href");
                    if(Link!=""&&!this.Visited.containsKey(Link)&&!this.NotVisit.containsKey(Link))
                    { this.NotVisit.put(Link,parent);
                      Document docnotvisit = new Document("Url",Link)
                      .append("Parent",parent);
                     if(this.Type.equals("Recrawling"))
                        CollectionRNotVisit.insertOne(docnotvisit);    
                     else
                        CollectionNotVisit.insertOne(docnotvisit);
                      //System.out.println("Document Notvisit inserted successfully");
                    }

                }  
    }
    
    
    public synchronized void InsertRec(String id,Pair<String,String> url,String in,String con)
    {
        Visited.put(url.getKey(), url.getValue());
        InCounter.add(Integer.parseInt(in));
        Content.add(con);
        Document document = new Document("Id",id) 
            .append("Url",url.getKey())
            .append("Parent",url.getValue()) 
            .append("Content",con)
            .append("InCounter",in);       
            CollectionRVisit.insertOne(document);
    }

    public synchronized LinkedHashSet GetContent()
    {
        return Content;
    }

    public synchronized Map GetVisited()
    {
        return Visited;
    }

    public synchronized Map GetNotVisit()
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

     
    

    public synchronized void ReadDBCrawl()
    {  
        FindIterable <Document> DocVisit;
        FindIterable <Document> DocNotVisit;
         if(this.Type.equals("Recrawling"))
         {DocNotVisit = CollectionRNotVisit.find();
         DocVisit = CollectionRVisit.find();}   
            else
         {DocNotVisit=CollectionNotVisit.find();
         DocVisit = CollectionVisit.find();}
                
        //read all info belongs to visits wesites
       // System.out.println("read all info belongs to visits");          
            for (Document myDoc : DocVisit) 
            {   	  
                String url = myDoc.get("Url").toString();
                String content=myDoc.get("Content").toString();
                String parent =myDoc.get("Parent").toString();
                int incounter=Integer.parseInt(myDoc.get("InCounter").toString());
                this.Visited.put(url, parent);
                this.Content.add(content);
                this.InCounter.add(incounter);

            }
           
            
            //read urls which are not visited
        //    System.out.println("read urls which are not visited");  
            
            for (Document myDoc : DocNotVisit) 
            {    
                String url = myDoc.get("Url").toString();
                String parent = myDoc.get("Parent").toString();
                this.NotVisit.put(url,parent);
                
            }
            
        //    System.out.println("the number of initial visited "+this.Visited.size());
        //    System.out.println("the number of intitial notvisited "+this.NotVisit.size());
   
    }
    
    
  /*  public void ReadDBRecrawl()
    {
        
        System.out.println("Start Read DB Recrawling");
        int NumOfSeeds = 100;
        FindIterable<Document> doc = CollectionVisit.find();
        BasicDBObject query =new BasicDBObject("InCounter", -1);
        doc.sort(query).limit(NumOfSeeds);
        //read data in map of not visited 
         for (Document myDoc : doc) 
         {
         String Url = (String) myDoc.get( "Url");
         String Parent = (String) myDoc.get( "Parent");
         this.NotVisit.put(Url, Parent);
         System.out.println("Parent is: "+ Parent + " & Url is: "+Url);
         }
        
        
  
    }
    */
    //writing html files in the folder
    public void writer(org.jsoup.nodes.Document htmldoc,int id,String path) throws IOException
    {   
      //  System.out.println("hktbbbb");
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path+id+".html"), "utf-8"));       
        writer.write(htmldoc.toString());      
        writer.close();
    
    }
    
    
    
    /*
     
     //writing in the DB each iteration after constant time
    public synchronized  void  WriteDBAfterTime()
    {   
        System.out.println("writing in db");
        //insert all info belongs to visited url 
        for(int j=(int)CollectionVisit.count();j<new ArrayList<>(Visited.values()).size();j++)  
        { 
            int in = InCounter.get(j);
            Document document = new Document("Id",j) 
            .append("Url", new ArrayList<>(Visited.keySet()).get(j))
            .append("Parent",new ArrayList<>(Visited.values()).get(j)) 
            .append("Content",new ArrayList<>(Content).get(j))
            .append("InCounter",in);       
            CollectionVisit.insertOne(document); 
            System.out.println("Document inserted successfully"); 
         }
         
        //insert all not visit ursl
        CollectionNotVisit.drop(); 
        db.createCollection("NotVisit");
        CollectionNotVisit= db.getCollection("NotVisit"); 
        for (Map.Entry<String,String> entry : NotVisit.entrySet()) 
        {
            Document document = new Document("Url",entry.getKey())
                    .append("Parent",entry.getValue());
            CollectionNotVisit.insertOne(document);
            System.out.println("Document Notvisit inserted successfully");
        }
    }
*/
 
     /* public synchronized  void InsertNotVisit(Elements l,String parent)
        {   
            int count =0;
        if(Visited.size()+NotVisit.size()<MAX_IT)
        { //System.out.println("insert notvisited from thread "+Thread.currentThread().getId());
            //System.out.println("****inseeeeeeeeeeeeeeeeeeeert*******************************");  
            for(Element link : l)
                {
                    if(link.absUrl("href")!=""&&!this.Visited.containsKey(link.absUrl("href"))&&!this.NotVisit.containsKey(link.absUrl("href")))
                    //if(link.absUrl("href")!=""&&!this.Visited.contains(link.absUrl("href"))&&!this.NotVisit.contains(link.absUrl("href"))&&!this.HashCodes.contains(content.hashCode()))
                    { this.NotVisit.put(link.absUrl("href"),parent);
                        count++;
                    }

                }
            System.out.println("the size of inserted pages "+ count);
           // System.out.println("size of notvisited "+this.NotVisit.size());
          //  System.out.println("size of visited "+this.Visited.size());
        }
        }*/

	    
public static Boolean Robot(String Url){
	List<String> allMatches = new ArrayList<>();
	     List<String> allMatches_Allow = new ArrayList<>();
		 String[] parts = Url.split("(?<=/)");
		 String SubUrl=parts[0]+parts[1]+parts[2];
		SubUrl=SubUrl.substring(0, SubUrl.length());
		if (SubUrl.equals(Url)||SubUrl.equals(Url+"/")){
                    return true;
                }
                
		 try(BufferedReader in = new BufferedReader(new InputStreamReader(new URL(SubUrl+"/robots.txt").openStream())))
		    {
		        String line;
		        boolean found_user=false;
		        while((line = in.readLine()) != null) 
		        {  
		          int index1 = line.indexOf("User-agent: *");
		          int index2 = line.indexOf("User-Agent: *");          
		          if (index1 !=-1 ||index2 !=-1 )
		            {  found_user=true; 
		               continue;
		            }
				  if (found_user) 
				    {	  int m = line.indexOf("Disallow: ");
					   int Allow = line.indexOf("Allow: ");						 
					   if (m != -1)
					   { String disallow;
                                               disallow=line.substring(8,line.length());
						    
							 
							 allMatches.add(disallow.replaceAll(" ", ""));		   
					   }
					   else if (Allow != -1)
					   {
						 String allow;
                                                 allow=line.substring(8,line.length());
						// System.out.println(allow);
						 allMatches_Allow.add(allow.replaceAll(" ",""));		
							  
					  }
						  
					   
		        }}
		    } 
		    catch (IOException e) 
		    {
		    }
		 
		 int size_disallow=0;
		 int size_allow=0;
		 for (int counter = 0; counter < allMatches.size(); counter++)
		 { 		       
			 boolean found_disallow=Url.contains(allMatches.get(counter));
			 if (found_disallow)
			 {
				 size_disallow= allMatches.get(counter).length();				
				 break;
				 
			 }
	      }   		
		 for (int counter = 0; counter < allMatches_Allow.size(); counter++)
		 { 	
			 boolean found_allow=Url.contains(allMatches_Allow.get(counter));
			 if (found_allow)
			 {
				 size_allow= allMatches_Allow.get(counter).length();				
				 break;
			 }
	      }
	
            return size_disallow <= size_allow;
	}
    
    
}