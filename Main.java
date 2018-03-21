import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

public class Main {
    public static Map<String, Integer> stopWords;
    public static void readStopWords(){
        stopWords = new HashMap<String, Integer>();
        File file=new File("stopWords.txt");
        FileReader fileReader;
        try{ fileReader=new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            String stopWord;
            try {
                stopWord = br.readLine();
                while (stopWord != null) {
                    stopWords.put(stopWord,1);
                    stopWord = br.readLine();
                }
            }
            catch(IOException e){}
            try{ fileReader.close();}
            catch (IOException e){
            }
        }
        catch (FileNotFoundException x){
            System.out.println("file not found");
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException {
       // MongoClientURI connectionString = new MongoClientURI("mongodb://ghada:ghada@ds163796.mlab.com:63796/search_engine");
        MongoClient mongoClient = new MongoClient("localhost",27017);  //create mongo client
        MongoDatabase db = mongoClient.getDatabase("search___Engine");
        System.out.println("Connected to the database successfully");
        readStopWords();
        String paths[] ={"0.html","1.html","2.html","3.html","4.html","5.html","6.html","7.html","8.html","9.html"};
        ArrayList<Thread>threads=new ArrayList<Thread>();

        for(Integer i=0;i<10;++i){
            threads.add(new Thread( new indexer(db,stopWords,paths[i],i),"T"+i.toString()));
            threads.get(i).start();
        }
        for(Integer i=0;i<10;++i){
            threads.get(i).join();
        }
    }
}


