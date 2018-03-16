package crawler;
import java.io.IOException;
import java.util.ArrayList;


public class Master
{
    private static final int MAX_TH = 10;
    public static void main(String[] args) throws InterruptedException, IOException
    {
       MyData MyData = new MyData();
       ArrayList<Thread> thread = new ArrayList<>();
       for(int i=0;i<MAX_TH;i++)
       {thread.add(new Spider(MyData));
       thread.get(i).start();
       }
       
      /* Thread collector = new Collector(MyData);
       collector.start();
       collector.join();*/
       for(int i=0;i<MAX_TH;i++)
       {
       thread.get(i).join();
       }
       
    if(MyData.GetNotVisit().isEmpty())
        System.out.println("the NotVisited List Is Empty");
    System.out.println(MyData.GetInCounter());
    System.out.println(MyData.GetInCounter().size());
    
   // Connection obj = new Connection(MyData.GetVisited(),MyData.GetContent());      
   // Collector collector = new Collector(MyData);
   // collector.WriteDB();
        
    }
        
}
