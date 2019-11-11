import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserInterface extends JFrame {
    public UserInterface(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 200);
        int pagesCrawled = 0;

        setTitle("WebCrawlerWindow");

        JTextField inputURL = new JTextField("input URL here",40);
        inputURL.setName("UrlTextField");

        JButton button = new JButton("Crawl");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Crawl.crawl(inputURL.getText());
            }
        });
        //button.setBounds( 50,10);
        JLabel pagesIndicator = new JLabel("Pages Crawled: " + pagesCrawled);


        JPanel panel = new JPanel();
        panel.add(inputURL);
        panel.add(button);
        panel.add(pagesIndicator);

        add(panel);

        setVisible(true);

    }
}
