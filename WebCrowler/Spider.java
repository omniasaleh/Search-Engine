package crawler;
import java.io.IOException;
import java.math.BigInteger;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javafx.util.Pair;
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
       
           while(MyData.GetVisited().size()<MyData.MAX_IT)
            { 
               Pair<String, String> url;    
                url = MyData.fetch();
                if(url!=null)
                {
                    SimpleCrawl(url);  
                }
            }
           
        System.out.println("finished crawling");
    }     
    public  void SimpleCrawl(Pair<String, String> url)
    {
        
      
       try
        {  
            if(url.getKey().contains("http://")||url.getKey().contains("https://"))
            {
                Connection connection = Jsoup.connect(url.getKey()).timeout(0).followRedirects(true);
                Document htmlDocument = connection.get();

            
            
                int response =connection.response().statusCode();
                if(response == 200) //http
                {   
              //  System.out.println("the response = 200 request page " + url.getKey());
                if(htmlDocument.body()!=null)
                {
                String text = htmlDocument.body().text();
                String hashword=md5Hash(text);
                Elements linksOnPage = htmlDocument.select("a[href]"); 
                MyData.InsertVisit(url,linksOnPage,hashword,htmlDocument);   

                }
                }

               else 
                {
          //          System.out.println("Error retreiving page: " + connection.response().statusCode());
                }
            }           
        }
        catch(SocketException exception)
        {
        //    System.out.println("\n connection reset " + url);
                   
        }
       catch (IOException exception)
       {
    //   System.out.println("\n We were not successful in our HTTP request " + url);
       } 
       
       
    }
    
    
    
     public static String md5Hash(String message) 
     {
        String md5 = "";
        if(null == message) 
        	return null;
        
        try {
	        MessageDigest digest = MessageDigest.getInstance("MD5");//Create MessageDigest object for MD5
	        digest.update(message.getBytes(), 0, message.length());//Update input string in message digest
	        md5 = new BigInteger(1, digest.digest()).toString(16);//Converts message digest value in base 16 (hex)
 
        } catch (NoSuchAlgorithmException e) 
        {
            e.printStackTrace();
        }
        return md5;
    }
    
   
    
  
    

}


