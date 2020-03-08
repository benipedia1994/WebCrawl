import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserInterface extends JFrame {
    volatile HashSet<String> searched;
    volatile HashSet<String> toSearchHash;
    AtomicBoolean stopCrawling;


    public UserInterface(){




        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);


        JPanel panel = new JPanel();
        setTitle("WebCrawlerWindow");

        JTextField inputURL = new JTextField("input URL here",40);
        inputURL.setName("UrlTextField");

        //JLabel pagesIndicator = new JLabel("Pages Crawled: " +"0");
        Integer[] numbers = {1,2,3,4,5,6,7,8,9,10};
        JComboBox threads = new JComboBox<Integer>(numbers);
        threads.setSelectedIndex(0);

        stopCrawling = new AtomicBoolean(false);






        JLabel numberCrawledText = new JLabel();
        numberCrawledText.setText("Pages crawled: " +0);
        JButton crawlButton = new JButton("Crawl");
        crawlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Crawl.crawl(inputURL.getText());
                Queue<String> toSearch = new LinkedList<String>();
                searched = new HashSet<>();
                toSearchHash = new HashSet<>();
                Connection conn = null;
                PreparedStatement stmt = null;
                Object lock1 = new Object();
                stopCrawling.set(false);

                toSearch.add(inputURL.getText());
                toSearchHash.add(inputURL.getText());



                try {
                    conn = DriverManager.getConnection("jdbc:h2:~/crawlDatabase;AUTO_SERVER=TRUE;INIT=RUNSCRIPT FROM './links.sql'");
                    System.out.println("database connected");
                    stmt = conn.prepareStatement("INSERT INTO LINKS (link,title, links, body) VALUES (?,?,?,?) ");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    return;
                }
                int numberOfThreads = (Integer) threads.getSelectedItem();
                for (int i = 0; i < numberOfThreads; i++) {

                        Thread crawlThread = new Thread(new CrawlThread(toSearch, searched, conn, stmt, i + 1, toSearchHash,lock1,stopCrawling,numberCrawledText),
                                "Thread" + i);
                        crawlThread.start();

                    }
                }



        });
        JButton stopButton = new JButton("Stop Crawling");
        stopButton.addActionListener(new ActionListener() {
                                         @Override
                                         public void actionPerformed(ActionEvent actionEvent) {
                                             stopCrawling.set(true);
                                         }
                                     }
        );
        JTextField inputKeyWord = new JTextField("input key word",40);


        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                ArrayList<LinkObject> linkObjects = new ArrayList<>();
                Connection conn = null;
                Statement stmt = null;

                try {
                    conn = DriverManager.getConnection("jdbc:h2:~/crawlDatabase;AUTO_SERVER=TRUE");
                    System.out.println("database connected");
                    stmt = conn.createStatement();
                    linkObjects = SearchFunctions.createList(conn,stmt,inputKeyWord.getText());
                    for(int i = 0; i < 10; i++){
                        System.out.println(linkObjects.get(i).getTitle());
                    }

                    System.out.println(inputKeyWord.getText());

                    linkObjects = SearchFunctions.rankLinks(linkObjects, inputKeyWord.getText());
                    String[][] data = new String[10][3];
                    for(int i =0; i<data.length;i++){
                        data[i][0]=linkObjects.get(i).getUrl();
                        data[i][1]=linkObjects.get(i).getTitle();
                        data[i][2]=Double.toString(linkObjects.get(i).getRanking());
                    }
                    String [] columnNames = {"Link","Title","Ranking"};
                    JTable jtable = new JTable(data, columnNames);
                    jtable.getColumnModel().getColumn(0).setPreferredWidth(250);
                    jtable.getColumnModel().getColumn(1).setPreferredWidth(250);
                    panel.add(jtable);
                    repaint();
                    revalidate();
                    for( int i = 0; i < 10; i++){
                        System.out.println(linkObjects.get(i).getTitle()+linkObjects.get(i).getRanking());
                    }

                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    return;
                }



            }
        });

        //button.setBounds( 50,10);




        panel.add(inputURL);
        panel.add(threads);
        panel.add(crawlButton);
        panel.add(numberCrawledText);
        panel.add(stopButton);
        //panel.add(pagesIndicator);
        panel.add(inputKeyWord);
        panel.add(searchButton);
        add(panel);

        setVisible(true);

    }
}
