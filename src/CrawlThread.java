import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

class CrawlThread implements Runnable {
    Queue<String> toSearch;
    HashSet<String> searched;
    HashSet<String> toSearchHash;
    Connection conn;
    PreparedStatement stmt;
    int threadNumber;
    Object lock1;
    JLabel numberCrawledText;
    AtomicBoolean stopCrawling;



    public CrawlThread(Queue<String> toSearch, HashSet<String> searched, Connection conn, PreparedStatement stmt, int threadNumber,
                       HashSet<String> toSearchHash, Object lock1, AtomicBoolean stopCrawling, JLabel numberCrawledText) {
        this.toSearch = toSearch;
        this.searched = searched;
        this.conn = conn;
        this.stmt = stmt;
        this.threadNumber = threadNumber;
        this.toSearchHash = toSearchHash;
        this.lock1=lock1;
        this.stopCrawling=stopCrawling;
        this.numberCrawledText=numberCrawledText;
    }

    @Override
    public void run(){

        String current;


        System.out.println("Thread " +threadNumber+ " run started");

        while (searched.size() < 10000&&!stopCrawling.get()) {
            synchronized(lock1){
                while(toSearch.isEmpty()) {
                    try {
                        lock1.wait();
                    }catch(InterruptedException e ){
                        System.out.println(e.getMessage());
                        continue;
                    }
                }
                current = toSearch.poll();
            }


            if (!searched.contains(current)) {
                try {
                    Document doc = Jsoup.connect(current).get();
                    Elements links = doc.select("a");

                        //System.out.println("before getlinks");
                        getLinks(links, doc, current);
                        //System.out.println("after getlinks");

                    System.out.println(searched.size());
                    System.out.println(stopCrawling);
                    if(searched.size()%100==0){
                        numberCrawledText.setText("Pages crawled: "+searched.size());
                    }

                    //System.out.println("Thread " +threadNumber);

                } catch (java.io.IOException | IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    System.out.println(Thread.currentThread().getName());
                    continue;
                }
            }
        }
        System.out.println(searched.size());

        System.out.println("Thread " + threadNumber + " run ended");
}

private void getLinks(Elements links, Document doc, String current) {

        StringBuilder linksSerial = new StringBuilder();
    synchronized (lock1) {
        for (Element link : links) {
            String newURL = link.attr("abs:href");
            if (!toSearchHash.contains(newURL)) {
                toSearch.add(newURL);
                toSearchHash.add(newURL);
               // System.out.println(newURL);

            }

            linksSerial.append(newURL + ",");

            searched.add(current);
        }
        lock1.notifyAll();
    }
    if(linksSerial.length()>1) {
        linksSerial.deleteCharAt(linksSerial.length() - 1);
    }
    try {
        stmt.setString(1, current);
        try {
            stmt.setString(2, doc.select("title").first().text());
        }catch(NullPointerException e){
            stmt.setString(2,"No title Provided");
        }
        stmt.setString(3,linksSerial.toString());
        try {
            stmt.setString(4, doc.body().text());
        }catch(NullPointerException e){
            stmt.setString(4,"No Body Provided");
        }
        stmt.execute();

    }catch(SQLException e) {
        System.out.println(e.getMessage());
    }
}



}
