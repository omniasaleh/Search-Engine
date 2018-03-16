package crawler;
import java.io.IOException;
import java.net.SocketException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


class Spider extends Thread
{  
    
    private final MyData MyData;
    Spider(MyData MyData)
    {
        this.MyData=MyData;
    }
    
    @Override
    public void run ()
    { 
       
        while(MyData.GetVisited().size()<MyData.MAX_IT()&&!MyData.GetNotVisit().isEmpty())
        { 
            String url;    
            url = MyData.fetch();
            if(url!=null)
            SimpleCraw(url);  
        }
       
           
    }
    public  void SimpleCraw(String url)
    {
        
        boolean InsertContent;
       try
        {  
            Connection connection = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0").timeout(0).followRedirects(true);
            Document htmlDocument = connection.get();
        
            int response =connection.response().statusCode();
            if(response == 200) //http
            {
                
            System.out.println("the response =200 request page " + url);
            if(htmlDocument.body()!=null&&htmlDocument!=null)
            {
            String text = htmlDocument.body().text();
            String title= htmlDocument.title();
            System.out.println("*******************"+title+"**********************");
            //System.out.println("download from "+Thread.currentThread().getId());
            InsertContent=MyData.InsertVisit(url,text,title);   
            //if(MyData.GetVisited()+MyData.GetNotVisit()<MAX_IT)
            if(InsertContent)
                {   
                 
                Elements linksOnPage = htmlDocument.select("a[href]");
                System.out.println(" size of retrive "+linksOnPage.size());
                MyData.InsertNotVisit(linksOnPage);
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
            //SimpleCraw(url);
            
        }
       catch (IOException exception)
       {
       System.out.println("\n We were not successful in our HTTP request " + url);
       }
       
    }
    
  
    

}


