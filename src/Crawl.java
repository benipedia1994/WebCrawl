import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.*;


public class Crawl {

    public static void crawl(String inputURL){
        Queue<String> toSearch = new LinkedList<String>();
        HashSet<String> searched = new HashSet<>();
        HashSet<String> linkList = new HashSet<>();


        toSearch.add(inputURL);
        linkList.add(inputURL);

        Connection conn = null;
        PreparedStatement stmt = null;

        while(!toSearch.isEmpty() && linkList.size() <10000) {
            try {
                conn = DriverManager.getConnection("jdbc:h2:~/crawlDatabase;AUTO_SERVER=TRUE;INIT=RUNSCRIPT FROM './links.sql'");
                System.out.println("database connected");
                stmt = conn.prepareStatement("INSERT INTO LINKS (link)" + "VALUES (?)");
                while (!toSearch.isEmpty() && linkList.size() < 10000) {
                    String current = toSearch.remove();
                    if (!searched.contains(current)) {
                        try {
                            Document doc = Jsoup.connect(current).get();
                            Elements links = doc.select("a");
                            for (Element link : links) {
                                String newURL = link.attr("abs:href");
                                toSearch.add(newURL);
                                if (!linkList.contains(newURL)) {
                                    linkList.add(newURL);
                                    stmt.setString(1, newURL);
                                    stmt.execute();
                                    System.out.println(linkList.size());
                                }
                            }
                            searched.add(current);
                            System.out.println(linkList.size());
                        } catch (java.io.IOException e) {
                            e.getMessage();
                        }
                    }
                }
                System.out.println(searched.size());

            } catch (java.sql.SQLException e) {
                e.getMessage();
            }
        }
    }

}
