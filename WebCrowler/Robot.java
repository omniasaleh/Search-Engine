package crawler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import com.trigonic.jrobotx.RobotExclusion;

public class Robot {
 
    public static void main(String[] args) throws MalformedURLException
    {
        String s="https://web.archive.org/web/20080517201804/http://www.cs.wm.edu/~coppit/csci690-spring2004/papers/selep_main.pdf";
        RobotExclusion robotExclusion = new RobotExclusion();   	 
        URL url = new URL(s);
        boolean isrobotallowed;
        if(is_robottxt_exist(url))
        {
        isrobotallowed=robotExclusion.allows(url, "*");
        System.out.println(isrobotallowed);
        System.out.println("okaay");
        }
        else 
        {
        isrobotallowed=true;
        System.out.println("sorry");
        }

    }
	    
    public static boolean is_robottxt_exist(URL url)
    { boolean b;
     try 
     {
            //String host = url.getHost().toLowerCase();
            URL robotsFileUrl =new URL("https://web.archive.org"+ "/robots.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader( robotsFileUrl.openStream()));
            b=true;
     }
     catch(Exception e)
     {
        System.out.println(url.toString()+" do not have robot.txt"); b=false;
     }
    return b;
    }
		
		
	}
