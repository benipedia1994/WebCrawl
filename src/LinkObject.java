import java.util.HashSet;

public class LinkObject implements Comparable<LinkObject>{
    String url;
    String title;
    HashSet<String> links;
    String body;
    Double ranking = 0.0;
    public LinkObject(){
        this.links = new HashSet<String>();
    }
    public LinkObject(String url, String title, String linksString, String body){
        this.url=url;
        this.title=title;
        this.links= parseLinks(linksString);
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HashSet<String> getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = parseLinks(links);
    }
    public void setLinks(HashSet<String> links) {
        this.links = links;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    public double getRanking(){
        return ranking;
    }
    public void setRanking(double ranking){
        this.ranking = ranking;
    }

    public static HashSet<String> parseLinks(String linksString){
        HashSet<String> links = new HashSet<>();
        String[] str = linksString.split(",");
        for(int i = 0; i < str.length; i++){
            links.add(str[i]);
        }
        return links;
    }
    public int compareTo(LinkObject other){
        return ranking.compareTo(other.ranking);
    }

}
