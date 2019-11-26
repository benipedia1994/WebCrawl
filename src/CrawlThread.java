import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Queue;

class CrawlThread implements Runnable {
    Queue<String> toSearch;
    HashSet<String> searched;
    Connection conn;
    PreparedStatement stmt;
    int threadNumber;

    public CrawlThread(Queue<String> toSearch, HashSet<String> searched, Connection conn, PreparedStatement stmt, int threadNumber) {
        this.toSearch = toSearch;
        this.searched = searched;
        this.conn = conn;
        this.stmt = stmt;
        this.threadNumber = threadNumber;
    }

    @Override
    public void run(){
        String current;
        System.out.println("Thread " +threadNumber+ " run started");
        while (!toSearch.isEmpty() && searched.size() < 10000) {
            synchronized (CrawlThread.class) {
                 current = toSearch.remove();
            }
            if (!searched.contains(current)) {
                try {
                    Document doc = Jsoup.connect(current).get();
                    Elements links = doc.select("a");

                    getLinks(links, doc, current);

                    System.out.println(searched.size());

                } catch (java.io.IOException e) {
                    e.getMessage();
                }
            }
        }
        System.out.println(searched.size());
        System.out.println("Thread " + threadNumber + " run ended");
}

private synchronized void getLinks(Elements links, Document doc, String current) {
    for (Element link : links) {
        String newURL = link.attr("abs:href");
        toSearch.add(newURL);
    }
    try {
        stmt.setString(1, current);
        stmt.setString(2, doc.html());
        stmt.execute();
        searched.add(current);
    }catch(SQLException e) {
        System.out.println(e.getMessage());
    }
}



}
