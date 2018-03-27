
//max threads
//max iteration crawling
//max iteration recrawling
//nom of seeds of recrawling

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import org.bson.Document;

/**
 *
 * @author menna
 */
public class Seeds {
    public static void main(String args[]) throws IOException {  

    //MongoClientURI connectionString = new MongoClientURI("mongodb://ghada:ghada@ds163796.mlab.com:63796/search_engine");
    MongoClient mongoClient = new MongoClient( "localhost" , 27017 );  //create mongo client   
    MongoDatabase db = mongoClient.getDatabase("searchengine"); //accessing database 
    MongoCollection collection = db.getCollection("NotVisit"); 
    MongoCollection collectionvisit = db.getCollection("Visit"); 
    MongoCollection collectionstate = db.getCollection("State"); 
    MongoCollection collectionRNotVisit = db.getCollection("RNotVisit");
    MongoCollection collectionRvisit = db.getCollection("RVisit");
    collectionRNotVisit.drop();
    collectionRvisit.drop();
    db.createCollection("RNotVisit");
    db.createCollection("RVisit");
    collectionvisit.drop();
    collection.drop();
    collectionstate.drop();
    db.createCollection("NotVisit");
    db.createCollection("Visit");
    db.createCollection("State");
    collection = db.getCollection("NotVisit"); 
    Document doc = new Document("Id",1).append("Crawling", "False");
    collectionstate.insertOne(doc);
    LinkedHashSet<String> NotVisit=new LinkedHashSet();
    
     File Notvisit = new File("C:\\Users\\menna\\OneDrive\\Documents\\NetBeansProjects\\Crawler\\src\\crawler\\seeds.txt");
            try
            {
                String st;
                BufferedReader bn = new BufferedReader(new FileReader(Notvisit));
                while ((st = bn.readLine()) != null&&!st.equals(""))
                {
                   System.out.println("ana areeet notvisit ****** "+st);
                   NotVisit.add(st);
                }
            }
            catch (FileNotFoundException ex) 
            {
               Notvisit.createNewFile();

            } 
     Iterator<String> notvisit = NotVisit.iterator();
          while(notvisit.hasNext())
         {
            Document document = new Document("Url",notvisit.next())
            .append("Parent","");
            collection.insertOne(document); 
            System.out.println("Document Notvisit inserted successfully");      
         }
          
          
          
        String[] pathes={"C:\\Users\\menna\\Desktop\\html_old","C:\\Users\\menna\\Desktop\\html_new","C:\\Users\\menna\\Desktop\\html_mod"}; 
        for(int i=0;i<3;i++)
        {
        File file = new File(pathes[i]);      
        String[] myFiles;    
        if(file.isDirectory()){
           myFiles = file.list();
        for (String myFile1 : myFiles) 
            {
                File myFile = new File(file, myFile1);
                myFile.delete();
            }
        }
        }
        
        
        
    }
    
}
