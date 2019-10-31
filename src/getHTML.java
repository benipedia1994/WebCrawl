import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class getHTML {

    public static void main(String[] args){
        try {
            Document doc = Jsoup.connect("https://jsoup.org/cookbook/extracting-data/attributes-text-html").get();
            String text = doc.html();
            System.out.println(text);
        } catch(java.io.IOException e){
            System.out.println(e.getMessage());
        }
    }

}
