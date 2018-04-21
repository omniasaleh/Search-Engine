import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.io.*;
import java.util.*;

public class main {
    public static int cnt;
    public static double alpha=.85;
    public static void main(String[] args) throws IOException {
        // Creating a Mongo client
        MongoClient mongo = new MongoClient("localhost", 27017);
        MongoDatabase database = mongo.getDatabase("searchengine");
        // Retieving a collection
        MongoCollection<Document> collection = database.getCollection("Visit");
        BasicDBObject projection = new BasicDBObject();
        projection.put("Parent", 1);
        projection.put("Id", 1);
        long len = collection.count();
        cnt = (int) len;
        double[][] H = new double[cnt+1][cnt+1];
        int  [] cnts=new int [cnt];
        System.out.println(len);
        FindIterable<Document> iterDoc = collection.find().projection(projection);
        Iterator<Document> iterator = iterDoc.iterator();
        List<Map<String, Object>> list = new ArrayList<>();
        while (iterator.hasNext()) {
            Document document = iterator.next();
            document.remove("_id");
            Map<String, Object> map = new HashMap<>(document);
            Object p = map.get("Parent");
            Object idO = map.get("Id");
            String ss=idO.toString();
            int id= Integer.parseInt(idO.toString());
            String par = p.toString();
            String[] parents = par.split(" ");
            for(int i=0;i<parents.length;++i){
                String tmp=parents[i];
                if(tmp.equals(""))
                    continue;
                int l=Integer.parseInt(tmp);
                H[id][l]=1;
                cnts[l]++;
            }
        }
        Boolean dangling;
        for(int j=0;j<cnt;++j){
            if(cnts[j]==0)
                dangling=true;
            else
                dangling =false;
            for(int i=0;i<cnt;++i) {
                if(dangling)
                    H[i][j]=(1.0/cnt);
                else{
                    if(H[i][j]==1)
                        H[i][j]=(1.0/cnts[j]);
                }
                H[i][j]*=alpha;
                H[i][j]+=(1-alpha)/cnt;
            }
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("mat.txt"), "utf-8"))) {
                    for (int i = 0; i < cnt; ++i) {
                        for (int j = 0; j < cnt; ++j) {
                            writer.write(String.valueOf(H[i][j]));
                            writer.write(" ");
                        }
                        writer.write("\n");
                    }
        }
    }
}

