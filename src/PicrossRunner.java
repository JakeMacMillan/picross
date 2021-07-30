import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PicrossRunner {

    public static String print2DArr(ArrayList<ArrayList<Integer>> arr) {
        String myStr = "";
        for(ArrayList<Integer> row : arr) {
            for(Integer e : row) {
                myStr += e + " ";
            }
            myStr += "\n";
        }
        return myStr;
    }

    public static void main(String[] args) throws IOException{
        Scanner inFile = new Scanner(new File("inputFiles/hanjie_big_3.txt"));
        ArrayList<String> allRows = new ArrayList<String>();
        while(inFile.hasNext()) {
            allRows.add(inFile.nextLine().strip());
        }
        int size = allRows.size() / 2;
        ArrayList<ArrayList<Integer>> rows = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> cols = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i < size; i++) {
            ArrayList<Integer> row = new ArrayList<Integer>();
            ArrayList<Integer> col = new ArrayList<Integer>();
            String[] c = allRows.get(i).split(",");
            String[] r = allRows.get(size + i).split(",");
            
            for(int j = 0; j < c.length; j++) {
                int cV = Integer.valueOf(c[j]);
                if(cV != 0) col.add(cV);
            }
            for(int j = 0; j < r.length; j++) {
                int rV = Integer.valueOf(r[j]);
                if(rV != 0) row.add(rV);
            }
            if(row.size() == 0) row.add(0);
            if(col.size() == 0) col.add(0);
            rows.add(row);
            cols.add(col);
        }

        // System.out.println("Rows:\n" + print2DArr(rows));
        // System.out.println("Cols:\n" + print2DArr(cols));
        Picross myGame = new Picross(rows, cols);
        myGame.playGame();
    }
}
