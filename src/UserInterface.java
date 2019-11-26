import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class UserInterface extends JFrame {
    public UserInterface(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 200);
        int pagesCrawled = 0;

        setTitle("WebCrawlerWindow");

        JTextField inputURL = new JTextField("input URL here",40);
        inputURL.setName("UrlTextField");

        JLabel pagesIndicator = new JLabel("Pages Crawled: " +"0");
        Integer[] numbers = {1,2,3,4,5,6,7,8,9,10};
        JComboBox threads = new JComboBox<Integer>(numbers);
        threads.setSelectedIndex(0);


        JButton button = new JButton("Crawl");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Crawl.crawl(inputURL.getText());
                Queue<String> toSearch = new LinkedList<String>();
                HashSet<String> searched = new HashSet<>();
                Connection conn = null;
                PreparedStatement stmt = null;

                toSearch.add(inputURL.getText());

                try {
                    conn = DriverManager.getConnection("jdbc:h2:~/crawlDatabase;AUTO_SERVER=TRUE;INIT=RUNSCRIPT FROM './links.sql'");
                    System.out.println("database connected");
                    stmt = conn.prepareStatement("INSERT INTO LINKS (link, content)" + "VALUES (?,?)");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    return;
                }
                int numberOfThreads = (Integer) threads.getSelectedItem();
                for (int i = 0; i < numberOfThreads; i++) {
                    if (toSearch.isEmpty()) {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }
                    } else {
                        Thread crawlThread = new Thread(new CrawlThread(toSearch, searched, conn, stmt, i + 1), "Thread" + i);
                        crawlThread.start();
                    }
                }
            }


        });
        //button.setBounds( 50,10);



        JPanel panel = new JPanel();
        panel.add(inputURL);
        panel.add(threads);
        panel.add(button);
        panel.add(pagesIndicator);

        add(panel);

        setVisible(true);

    }
}
