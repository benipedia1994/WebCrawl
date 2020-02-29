import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class SearchFunctions {
    public static ArrayList<LinkObject> createList(Connection conn, Statement stmt, String target){
        String url, title, linksString, body;
        ArrayList<LinkObject> linkObjects = new ArrayList<>();
        int numlinks = 0;
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM LINKS");
            while(rs.next()){
                if(rs.getString(5).contains(target)) {
                    url = rs.getString(2);
                    title = rs.getString(3);
                    linksString = rs.getString(4);
                    body = rs.getString(5);
                    LinkObject newEntry = new LinkObject(url, title, linksString, body);
                    linkObjects.add(newEntry);
                    numlinks++;
                    System.out.println(numlinks);
                }


            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return linkObjects;
    }


    public static ArrayList<LinkObject> rankLinks(ArrayList<LinkObject> linkObjects, String target){
        ArrayList<LinkObject> searchedList = new ArrayList<>();
        HashSet<String> targetLinks = new HashSet<>();
        for(int i = 0; i < linkObjects.size(); i++){
            if(linkObjects.get(i).getBody().contains(target)){
                searchedList.add(linkObjects.get(i));
                targetLinks.add(linkObjects.get(i).url);
            }
        }
        for(int i = 0; i< searchedList.size(); i++){
            HashSet<String> newSet = new HashSet<>();

            for(String temp: targetLinks){
                if(searchedList.get(i).getLinks().contains(temp)){
                    newSet.add(temp);
                }
            }
            searchedList.get(i).setLinks(newSet);
        }


        int size = searchedList.size();

        double [][] rankVector = new double [size][1];
        System.out.println(1.0/size);
        for( int i = 0; i<size; i++){
            rankVector[i][0]=1.0/size;
        }

        double[][] linkMatrix = createLinkMatrix(searchedList);

        rankVector = applyRankingAlgo(linkMatrix, rankVector, .80, 10);
        System.out.println("rank vector obtained");

        for(int i = 0; i< rankVector.length; i++){
            searchedList.get(i).setRanking(rankVector[i][0]);
        }
        System.out.println("ranks set");

        Collections.sort(searchedList, Collections.reverseOrder());
        System.out.println("arrayLists sorted");

        return searchedList;

    }


    public static double[][] applyRankingAlgo(double[][] linkMatrix, double[][] rankVector, double damper, int iterations){
        int rows = linkMatrix.length;
        int cols = linkMatrix[0].length;

        double[][] dampingVector = new double[rows][1];

        for(int i = 0; i < rows; i++){
            dampingVector[i][0]=(1-damper)/rows;
        }
        for(int i =0; i<iterations; i++){
            rankVector = matrixMult(linkMatrix,rankVector);
            rankVector = scalarMult(rankVector,damper);
            rankVector = matrixAdd(rankVector,dampingVector);
            System.out.println("iteration" + i + "done");
        }
        return rankVector;
    }


    public static double[][] createLinkMatrix(ArrayList<LinkObject> searchedList){
        int size = searchedList.size();
        double[][] linkMatrix = new double[size][size];
        for(int i = 0; i < size; i++){
            String currentURL = searchedList.get(i).getUrl();
            for(int  j = 0; j< size; j++){
                HashSet<String> currentLinks = searchedList.get(j).getLinks();
                if(currentLinks.contains(currentURL)){
                    linkMatrix[i][j]=1/currentLinks.size();
                }
            }

        }
        return linkMatrix;
    }




    public static double[][] matrixMult(double[][] A, double[][] B){
        int Arow = A.length;
        int Acol = A[0].length;
        int Brow = B.length;
        int Bcol = B[0].length;
        double[][] result = new double[Arow][Bcol];
        for(int i = 0; i<Arow; i++){
            for(int j = 0; j<Bcol; j++){
                result[i][j]=dotProduct(A,B, i,j);
            }
        }
        return result;
    }
    public static double dotProduct(double[][] A, double [][] B, int row, int col){
        double result = 0;
        for(int i = 0; i < A.length; i++){
            result = result + A[row][i]*B[i][col];
        }
        return result;
    }

    public static double[][] scalarMult(double [][] A, double scalar){
        double[][] result = A;
        for(int i = 0; i < result.length; i++){
            for(int j = 0; j < result[0].length; j++){
                result[i][j] = scalar*result[i][j];
            }
        }
        return result;
    }
    public static double[][] matrixAdd(double [][] A, double[][] B) {
        if (A.length != B.length || A[0].length != B[0].length){
            System.out.println("Matrices do not have matching dimension");
            return null;
        }
        double[][] C = new double[A.length][A[0].length];
        for(int i =0; i<A.length;i++){
            for(int j = 0; j<A[0].length; j++){
                C[i][j]=A[i][j]+B[i][j];
            }
        }
        return C;
    }

}
