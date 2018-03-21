import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import java.io.*;
import java.util.*;
import static com.mongodb.client.model.Filters.*;

public class indexer extends indexing implements Runnable {
    MongoDatabase db;
    String path;
    int id;
    public indexer( MongoDatabase database, Map<String, Integer> stopWords,String p,int Pid){
                super (stopWords);
                db=database;
                path=p;
                id=Pid;
    }
    public void run() {
        //indexer ind = new indexer();
        Thread thread = Thread.currentThread();
        System.out.println("indexer is being run by " + thread.getName() + " at " + new Date());
        try {
            start(path,id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(String path,Integer curId) throws IOException {
        File input = new File(path);
        Map<String, List<Integer>> mp =new HashMap<>();
        Map<String, Double> tf =  super.Index(input,mp);
        addWordsToDB(mp, curId, tf);
    }

    public void addWordsToDB(Map<String, List<Integer>> mp, Integer docID,Map<String, Double> tf){
        MongoCollection col;
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if (pair.getKey().toString().length()<1){it.remove();  continue;}
            String str=pair.getKey().toString();
            String colName = str.substring(0,1);
            synchronized (db) {
                col = db.getCollection(colName);
                List<Integer> ls = (List<Integer>) pair.getValue();
                BasicDBObject mtch1 = new BasicDBObject("word", pair.getKey().toString());
                BasicDBObject mtch2 = new BasicDBObject("stemmed", super.pa.stripAffixes(pair.getKey().toString()));
                Document doc = new Document("docNum", docID).append("idx", ls).append("tf", tf.get(pair.getKey()));
                BasicDBObject pushID = new BasicDBObject("$push", new BasicDBObject("doc", doc));
                col.updateOne(and(mtch1, mtch2), pushID, new UpdateOptions().upsert(true));
                it.remove(); // avoids a ConcurrentModificationException
            }
        }
    }
}