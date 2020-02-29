import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.LinkedList;
import java.util.Queue;

class IOThread implements Runnable {
    Queue<String> toSearch;
    boolean done;
    Object lock1;
    Queue<Document> toProcess;
    Object lock2;
    Document doc;
    public IOThread(Queue<String> toSearch, Queue<Document> toProcess,boolean done, Object lock1,  Object lock2) {
        this.toSearch = toSearch;
        this.lock1=lock1;
        this.done=done;
        this.toProcess = toProcess;
        this.lock2=lock2;
    }

    @Override
    public void run() {

    /*
        String current;
        while(!done){
        synchronized(lock1) {
            while(toSearch.isEmpty()){
                lock1.wait();
            }
            current = toSearch.poll();
        }
        try {

            doc = Jsoup.connect(current).get();
        }catch(java.io.IOException e){
                System.out.println(e.getMessage());
        }

        synchronized(lock2){
            toProcess.add(doc);
        }

    */
    }
}
