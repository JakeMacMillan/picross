import java.util.Scanner;
import java.util.ArrayList;

public class Picross {
    String[][] board;
    boolean[][] hLocked;
    boolean[][] vLocked;
    int length;
    int[] reqRows;
    int[] reqCols;
    ArrayList<ArrayList<Boolean>> completedRows;
    ArrayList<ArrayList<Boolean>> completedCols;
    ArrayList<ArrayList<Integer>> rows;
    ArrayList<ArrayList<Integer>> cols;
    ArrayList<ArrayList<ArrayList<Integer>>> rAvails;
    ArrayList<ArrayList<ArrayList<Integer>>> cAvails;
    int greatestRows;
    int greatestCols;

    /*
    constructor to intialize all of the instance variables for the Class
        also calls helper methods to initialize some of the more complex instance variables
    accepts the ArrayLists of notations for the rows and columns as parameters
    */
    public Picross(ArrayList<ArrayList<Integer>> rows, ArrayList<ArrayList<Integer>> cols) {
        this.rows = rows;
        this.cols = cols;
        this.length = rows.size();
        this.reqRows = new int[length];
        this.reqCols = new int[length];
        this.board = new String[length][length];
        this.hLocked = new boolean[length][length];
        this.vLocked = new boolean[length][length];
        this.completedRows = new ArrayList<ArrayList<Boolean>>();
        this.completedCols = new ArrayList<ArrayList<Boolean>>();
        this.greatestRows = 0;
        this.greatestCols = 0;
        initBoard();
        fillRequirements();
        initCompleted();
        this.rAvails = initAvails(rows);
        this.cAvails = initAvails(cols);
        System.out.println(rAvails);
        System.out.println(cAvails);
    }

    /*
    initBoard() fills in the board object with empty spaces in preparation for the puzzle
    */
    private void initBoard() {
        for(int i = 0; i < length; i++) {
            for(int j = 0; j < length; j++) {
                board[i][j] = " ";
            }
        }
    }

    /*
    fillRequirements fills in the reqRows and reqCols arrays, which are used when determining which rows are easy to fill first
    */
    private void fillRequirements() {
        for(int i = 0; i < length; i++) {
            int rSize = rows.get(i).size(), cSize = cols.get(i).size(), rTotal = 0, cTotal = 0;
            if(rSize > greatestRows) greatestRows = rSize;
            if(cSize > greatestCols) greatestCols = cSize;
            for(int j = 0; j < rSize; j++) {
                rTotal += rows.get(i).get(j);
            }
            for(int j = 0; j < cSize; j++) {
                cTotal += cols.get(i).get(j);
            }
            
            rTotal += rSize - 1;
            cTotal += cSize - 1;

            reqRows[i] = rTotal;
            reqCols[i] = cTotal;
        }
    }

    /*
    initCompleted fills the completedRows and completedCols ArrayLists with the appropriate number of rows, each of which has a number of columns
        corresponding to the number of labels on that row/column
    */
    private void initCompleted() {
        for(int i = 0; i < length; i++) {
            ArrayList<Boolean> row = new ArrayList<Boolean>();
            ArrayList<Boolean> col = new ArrayList<Boolean>();
            for(int j = 0; j < rows.get(i).size(); j++) {
                row.add(false);
            }
            for(int j = 0; j < cols.get(i).size(); j++) {
                col.add(false);
            }
            completedRows.add(row);
            completedCols.add(col);
        }
    }

    /*
    initAvails is used to initialize the two Avails ArrayLists with a full list of available spaces for each number in the puzzle
    */
    private ArrayList<ArrayList<ArrayList<Integer>>> initAvails(ArrayList<ArrayList<Integer>> values) {
        ArrayList<ArrayList<ArrayList<Integer>>> newVals = new ArrayList<ArrayList<ArrayList<Integer>>>();
        for(int i = 0; i < values.size(); i++) {
            ArrayList<ArrayList<Integer>> rowCol = new ArrayList<ArrayList<Integer>>();
            for(int j = 0; j < values.get(i).size(); j++) {
                ArrayList<Integer> avails = new ArrayList<Integer>();
                int len = values.get(i).get(j), beforeLen = 0, afterLen = 0;
                for(int k = j - 1; k >= 0; k--) {
                    beforeLen += 1;
                    beforeLen += values.get(i).get(k);
                }
                for(int k = j + 1; k < values.get(i).size(); k++) {
                    afterLen += 1; // for the gap
                    afterLen += values.get(i).get(k);
                }
                for(int k = beforeLen; k < length - (len + afterLen - 1); k++) {
                    avails.add(k);
                }
                rowCol.add(avails);
            }
            newVals.add(rowCol);
        }
        System.out.println(newVals);
        return newVals;
    }

    /*
    playGame is the primary loop in which the puzzle is solved
    it begins by filling in all of the easy rows/cols, then calls sweep() and other methods until the game is over
    */
    public void playGame() {
        printBoard();
        Scanner in = new Scanner(System.in);
        easyFills();
        // System.out.println();
        // printBoard();
        while(!gameOver()) {
            in.nextLine();
            sweep();
            checkCompleted();
            fillCompletes();
            printBoard();
        }
        in.close();
    }

    /*
    easyFills will take care of any easy rows/cols (either completely full or completely empty)
    */
    private void easyFills() {
        for(int i = 0; i < length; i++) { 
            if(reqRows[i] == length) {
                int rowSize = rows.get(i).size();
                int p = 0;
                for(int j = 0; j < rowSize; j++) {
                    for(int k = 0; k < rows.get(i).get(j); k++, p++) {
                        board[i][p] = "X";
                    }
                    if(p != length) {
                       board[i][p] = "."; 
                       p++;
                    }
                    if(p != length) hLocked[i][p] = true;
                }

                for(int j = 0; j < rowSize; j++) {
                    completedRows.get(i).set(j, true);
                    
                }
            }
            if(reqCols[i] == length) {
                int colSize = cols.get(i).size();
                int p = 0;
                for(int j = 0; j < colSize; j++) {
                    for(int k = 0; k < cols.get(i).get(j); k++, p++) {
                        board[p][i] = "X";
                    }
                    if(p != length) {
                       board[p][i] = "."; 
                       p++;
                    }
                    if(p != length) vLocked[p][i] = true;
                }

                for(int j = 0; j < colSize; j++) {
                    completedCols.get(i).set(j, true);
                }
            }
            if(reqRows[i] == 0) {
                for(int j = 0; j < length; j++) {
                    board[i][j] = ".";
                    hLocked[i][j] = true;
                }
                for(int j = 0; j < completedRows.get(i).size(); j++) {
                    completedRows.get(i).set(0, true);
                }
            }
            if(reqCols[i] == 0) {
                for(int j = 0; j < length; j++) {
                    board[j][i] = ".";
                    vLocked[j][i] = true;
                }
                for(int j = 0; j < completedCols.get(i).size(); j++) {
                    completedCols.get(i).set(0, true);
                }
            }
        }
    }

    /*
    printBoard displays the puzzle board, as well as the labels for the rows and columns
        if a value has been fully placed down, it will be replaced by a checkmark
    */
    public void printBoard() {
        for(int i = greatestCols; i > 0; i--) {
            for(int j = 0; j < greatestRows; j++) {
                System.out.print("  ");
            }
            System.out.print(" |");
            for(int j = 0; j < length; j++) {
                int cSize = cols.get(j).size();
                if(cSize >= i) 
                    if(!completedCols.get(j).get(cSize - i)) System.out.print(cols.get(j).get(cSize - i) + " "); // If the value hasn't been guessed
                    else System.out.print("✓ "); // If the value has been guessed
                else System.out.print("  ");
            }
            System.out.println();
        }
        for(int j = 0; j < greatestRows; j++) {
            System.out.print("  ");
        }
        System.out.print(" |");
        for(int i = 0; i < length * 2; i++) {
            System.out.print("-");
        }
        System.out.println();

        for(int i = 0; i < length; i++) {
            for(int j = greatestRows; j > 0; j--) {
                int rSize = rows.get(i).size();
                if(rSize >= j) // If we have a non-zero value
                    if(!completedRows.get(i).get(rSize - j)) System.out.print(rows.get(i).get(rSize - j) + " ");
                    else System.out.print("✓ "); // If the value has been guessed
                else System.out.print("  ");
            }
            System.out.print(" |");

            for(int j = 0; j < length; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    } 

    /*
    printArr is for debugging occasionally
    */
    private void printArr(boolean[] arr) {
        for(int i = 0; i < length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    /*
    gameOver will return true or false, depending on whether the game is finished
    If there are any numbers that are not marked as completed, the game is not yet over
        and the method will return false
    If all the numbers have been completed then the game is over
        and the method will return true
    */
    private boolean gameOver() {
        for(int i = 0; i < length; i++) {
            if(completedRows.get(i).contains(false) || completedCols.get(i).contains(false)) return false;
        }
        return true;
    }
    
    /*
    sweep is the workhorse method in this Class
    This is the primary method checking for availability for placing parts - for each row/col, for each number on that row/col, see where those values could be placed
    */
    private void sweep() {
        for(int i = 0; i < length; i++) { // Loop over all rows & columns
            int rSize = rows.get(i).size(), cSize = cols.get(i).size();
            ArrayList<Integer> filledOverlaps = new ArrayList<Integer>();
            boolean[] possibles = new boolean[length];
            for(int j = 0; j < rSize; j++) { // Loop over all possible numbers within a row
                if(!completedRows.get(i).get(j)) {           
                    checkAvailability("r", i, j); 
                    
                    // If there's only one spot available, fill it
                    if(rAvails.get(i).get(j).size() == 1) {
                        int pos = rAvails.get(i).get(j).get(0);
                        fill("r", i, j, pos);
                    } else {
                        
                        checkOverlap("r", i, j, rAvails.get(i).get(j), filledOverlaps, possibles);
                    }
                }
            }
            for(int k = 0; k < filledOverlaps.size() - 1; k++) {
                if(filledOverlaps.get(k + 1) - filledOverlaps.get(k) == 2) {
                    board[i][filledOverlaps.get(k) + 1] = ".";
                }
            }
            // System.out.println("r " + i);
            // printArr(possibles);
            for(int k = 0; k < length; k++) {
                if(!possibles[k] && board[i][k].equals(" ")) {
                    board[i][k] = ".";
                }
            }
            filledOverlaps = new ArrayList<Integer>();
            possibles = new boolean[length];
            for(int j = 0; j < cSize; j++) {
                if(!completedCols.get(i).get(j)) {
                    // Check how many spaces are available for the piece to be placed
                    checkAvailability("c", i, j);
                    if(cAvails.get(i).get(j).size() == 1) {
                        System.out.println("FILLING COLUMN " + i + " VALUE " + j);
                        int pos = cAvails.get(i).get(j).get(0);
                        fill("c", i, j, pos);
                    } else {
                        checkOverlap("c", i, j, cAvails.get(i).get(j), filledOverlaps, possibles);
                    }
                }
            }
            for(int k = 0; k < filledOverlaps.size() - 1; k++) {
                if(filledOverlaps.get(k + 1) - filledOverlaps.get(k) == 2) {
                    board[filledOverlaps.get(k) + 1][i] = ".";
                }
            }
            // System.out.println("c " + i);
            // printArr(possibles);
            for(int k = 0; k < length; k++) {
                if(!possibles[k] && board[k][i].equals(" ")) {
                    board[k][i] = ".";
                }
            }
        }
    }

    /*
    enoughSpace will return whether or not there's enough space to place based on the parameters
    Parameters: 
    rC:     Will equal either "r" or "c"
    val:    The value of the row or column
    len:    The number of tiles requested
    pos:    The position to start
    dir:    The direction to move
        0 -> left/up
        1 -> right/down
    */
    private boolean enoughSpace(String rC, int val, int len, int pos, int dir) {
        if(rC.equals("r")) {
            for(int i = 0; i < len; i++) {
                if(pos < 0 || pos >= length || board[val][pos] == ".") return false;
                if(dir == 0) pos--;
                else pos++;
            }
        } else {
            for(int i = 0; i < len; i++) {
                if(pos < 0 || pos >= length || board[pos][val] == ".") return false;
                if(dir == 0) pos--;
                else pos++;
            }
        }
        return true;
    }

    /*
    fillCompletes will fill all empty spaces on a fully completed row with "."s 
    */
    private void fillCompletes() {
        for(int i = 0; i < length; i++) {
            if(!completedRows.get(i).contains(false)) {
                // System.out.println("Filling row " + i);
                for(int j = 0; j < length; j++) {
                    if(board[i][j].equals(" ")) board[i][j] = ".";
                }
            }
            if(!completedCols.get(i).contains(false)) {
                // System.out.println("Filling column " + i);
                for(int j = 0; j < length; j++) {
                    if(board[j][i].equals(" ")) board[j][i] = ".";
                }
            }
        }
    }

    /*
    fill will update the board with "X" values in each position based on the parameters
    Parameters: 
    rC:     Will equal either "r" or "c"
    val:    The value of the row or column
    n:      Which of the labels is being accessed
    pos:    The position to start
    */
    private void fill(String rC, int val, int n, int pos) {
        if(rC.equals("r")) {
            if(pos != 0) board[val][pos -1] = ".";
            for(int j = 0; j < rows.get(val).get(n); j++, pos++) {
                board[val][pos] = "X";
                hLocked[val][pos] = true;
            }
            if(pos != length) board[val][pos] = ".";
            completedRows.get(val).set(n, true);
        } else {
            if(pos != 0) board[pos-1][val] = ".";
            for(int j = 0; j < cols.get(val).get(n); j++, pos++) {
                board[pos][val] = "X";
                vLocked[pos][val] = true;
            }
            if(pos != length) board[pos][val] = ".";
            completedCols.get(val).set(n, true);
        }
    }
    
    /*
    checkAvailability will the list of available positions to place the desired length in the specified row/col
        The return list will contain valid starting positions moving rightward or downward
    Parameters: 
    rC:     Will equal either "r" or "c"
    val:    The value of the row or column
    n:      The index within the arrayList of numbers
    */
    private void checkAvailability(String rC, int val, int n) {
        int len;
        boolean anchor, validAnchor;
        if(rC.equals("r")) {
            len = rows.get(val).get(n);
            anchor = false;
            int lastInd = rAvails.get(val).get(n).size() - 1;
            // if(n == 0 || n == rAvails.get(val).size() - 1) {
            if(rAvails.get(val).size() == 1) {
                anchor = checkAnchor(rC, val, rAvails.get(val).get(n).get(0), rAvails.get(val).get(n).get(lastInd) + len);
            }
            System.out.println(rC + " " + val + " anchor: " + anchor);
            for(int h = 0; h < rAvails.get(val).get(n).size(); h++) {
                int i = rAvails.get(val).get(n).get(h);
                boolean valid = true;
                // Check if value is at start or end of row, fill accordingly
                if(n == 0 && board[val][i].equals("X") && filledLeftUp(rC, val, i-1)) {
                    ArrayList<Integer> newList = new ArrayList<Integer>();
                    newList.add(i);
                    rAvails.get(val).set(n, newList);
                    return;
                }
                if(n == rAvails.get(val).size() - 1 && board[val][i + len - 1].equals("X") && filledRightDown(rC, val, i + len)) {
                    ArrayList<Integer> newList = new ArrayList<Integer>();
                    newList.add(i);
                    rAvails.get(val).set(n, newList);
                    return;
                }

                for(int j = 0; j < len; j++) {
                    // Checks if the current tile placement would go out of bounds, cross a ".", cross a locked tile, or exempt the total before/after from fitting
                    //   Does NOT currently check if the next value in the sequence will fit based on current board state, only if it will go out of bounds (much to my chagrin)
                    if(i+j >= length || board[val][i+j].equals(".") || hLocked[val][i+j]) {
                        valid = false;
                        break;
                    }
                }
                if(anchor && valid) {
                    validAnchor = verifyAnchor(rC, val, i, len);
                    if (!validAnchor) valid = false;
                }
                if(!valid) {
                    rAvails.get(val).get(n).remove(Integer.valueOf(i));
                    h--;
                }
            }
            System.out.println(rC + " " + val + " n " + n + " length " + len + ": " + rAvails.get(val).get(n));
        } else {
            len = cols.get(val).get(n);
            anchor = false;
            if(cAvails.get(val).size() == 1) {
                int lastInd = cAvails.get(val).get(n).size() - 1;
                anchor = checkAnchor(rC, val, cAvails.get(val).get(n).get(0), cAvails.get(val).get(n).get(lastInd) + len);
            }
            System.out.println(rC + " " + val + " anchor: " + anchor);
            for(int h = 0; h < cAvails.get(val).get(n).size(); h++) {
                int i = cAvails.get(val).get(n).get(h);
                boolean valid = true;
                // Check if value is either at start or end of column, fill accordingly
                if(n == 0 && board[i][val].equals("X") && filledLeftUp(rC, val, i-1)) {
                    ArrayList<Integer> newList = new ArrayList<Integer>();
                    newList.add(i);
                    cAvails.get(val).set(n, newList);
                    return;
                }
                if(n == cAvails.get(val).size() - 1 && board[i + len - 1][val].equals("X") && filledRightDown(rC, val, i + len)) {
                    ArrayList<Integer> newList = new ArrayList<Integer>();
                    newList.add(i);
                    cAvails.get(val).set(n, newList);
                    return;
                }

                for(int j = 0; j < len; j++) {
                    // Checks if the current tile placement would go out of bounds, cross a ".", cross a locked tile, or exempt the total before/after from fitting
                    //   Does NOT currently check if the next value in the sequence will fit based on current board state, only if it will go out of bounds (much to my chagrin)
                    if(i+j >= length || board[i+j][val].equals(".") || vLocked[i+j][val]) {
                        valid = false;
                        break;
                    }
                }
                if(anchor && valid) {
                    validAnchor = verifyAnchor(rC, val, i, len);
                    if (!validAnchor) valid = false;
                }
                if(!valid) {
                    cAvails.get(val).get(n).remove(Integer.valueOf(i));
                    h--;
                }
            }
            System.out.println(rC + " " + val + " n " + n + " length " + len + ": " + cAvails.get(val).get(n));
        }
    }

    /*
    checkOverlap will determine if there are overlapping spaces as a result of the available placements
        It will place an "X" on any space that is overlapped by ALL projections
    Parameters: 
    rC:             Will equal either "r" or "c"
    val:            The value of the row or column
    n:              The index within the arrayList of numbers
    avails:         The list of potential starting places
    filledOverlaps: The list of which spaces were filled via overlap
    possibles:      The array of which positions are possible/impossible to be filled with that number
    */
    private void checkOverlap(String rC, int val, int n, ArrayList<Integer> avails, ArrayList<Integer> filledOverlaps, boolean[] possibles) {
        boolean[] overlaps = new boolean[length];
        for(int i = 0; i < length; i++) {
            overlaps[i] = true;
        }
        for(int i = 0; i < avails.size(); i++) {
            int pos = avails.get(i);
            
            for(int j = 0; j < length; j++) {
                // System.out.println("j: " + j);
                // System.out.println(" j < pos" + (j < pos));
                if(rC.equals("r")){
                    // System.out.println(" j >= pos + rows.get(val).get(n)" + (j >= (pos + rows.get(val).get(n))));
                    if(j < pos || j >= (pos + rows.get(val).get(n))) overlaps[j] = false;
                    else possibles[j] = true;
                }
                else{
                    // System.out.println(" j >= pos + cols.get(val).get(n)" + (j >= (pos + cols.get(val).get(n))));
                    if(j < pos || j >= (pos + cols.get(val).get(n))) overlaps[j] = false;
                    else possibles[j] = true;
                }
            }
        }
        // System.out.println(rC + " " + val);
        // printArr(possibles);
        if(rC.equals("r")) {
            for(int i = 0; i < length; i++) {
                if(overlaps[i]) {
                    board[val][i] = "X";
                    filledOverlaps.add(i);
                }
            }
        } else {
            for(int i = 0; i < length; i++) {
                if(overlaps[i]) {
                    board[i][val] = "X";
                    filledOverlaps.add(i);
                }
            }
        }
    }

    /*
    checkCompleted will confirm whether the contents of a given row/column completely match its label(s). 
        If the contents match the label(s), that row/column's labels are all marked as completed
    */
    private void checkCompleted() {
        for(int i = 0; i < length; i++) {
            ArrayList<Integer> rSequences = new ArrayList<Integer>(), cSequences = new ArrayList<Integer>();
            int curRLen = 0, curCLen = 0;
            for(int j = 0; j < length; j++) {
                if(board[i][j].equals("X")) curRLen++;
                else {
                    if(curRLen > 0) {
                        rSequences.add(curRLen);
                        curRLen = 0;
                    } 
                }
                if(board[j][i].equals("X")) curCLen++;
                else {
                    if(curCLen > 0) {
                        cSequences.add(curCLen);
                        curCLen = 0;
                    }
                }
            }
            if(curRLen > 0) rSequences.add(curRLen);
            if(curCLen > 0) cSequences.add(curCLen);
            // System.out.println("r/c " + i);
            // System.out.println("rSeq: " + rSequences);
            // System.out.println("cSeq: " + cSequences);
            if(rSequences.size() == rows.get(i).size()) {
                boolean match = true;
                for(int k = 0; k < rSequences.size(); k++) {
                    if(rows.get(i).get(k) != rSequences.get(k)) {
                        match = false;
                        break;
                    }
                }
                if(match) {
                    for(int k = 0; k < rSequences.size(); k++) {
                        completedRows.get(i).set(k, true);
                    }
                }
            }
            if(cSequences.size() == cols.get(i).size()) {
                boolean match = true;
                for(int k = 0; k < cSequences.size(); k++) {
                    if(cols.get(i).get(k) != cSequences.get(k)) {
                        match = false;
                        break;
                    }
                }
                if(match) {
                    for(int k = 0; k < cSequences.size(); k++) {
                        completedCols.get(i).set(k, true);
                    }
                }
            }
        }
    }

    /*
    verifyAnchor will determine whether the requested row/column has an anchor point on it
        If there are any "X" characters on the row/col, return true
    Parameters: 
    rC:     Will equal either "r" or "c"
    val:    The value of the row or column
    start:  Which index to start looking
    end:    Which index to stop looking
    */
    private boolean checkAnchor(String rC, int val, int start, int end) {
        for(int i = start; i < end; i++) {
            if(rC.equals("r") && board[val][i].equals("X")) return true;
            if(rC.equals("c") && board[i][val].equals("X")) return true;
        }
        return false;
    }

    /*
    verifyAnchor will confirm whether the current "valid" placement hits an anchor point
        If any point on the placement hits an "X" return true
    Parameters: 
    rC:     Will equal either "r" or "c"
    val:    The value of the row or column
    n:      The index on that row/column to start
    l:      The length of the tile to be placed
    */
    private boolean verifyAnchor(String rC, int val, int n, int l) {
        // System.out.println("Len: " + l);
        for(int i = 0; i < l; i++) {
            // System.out.println(rC + " " + val + " ind " + i + " ");
            if(rC.equals("r") && board[val][i+n].equals("X")) return true;
            if(rC.equals("c") && board[i+n][val].equals("X")) return true;
        }
        return false;
    }

    /*
    filledLeftUp will confirm whether all spaces to the left/up from position n in the row/col
        indicated by val are filled by "."s
    Parameters:
    rC:     Will equal either "r" or "c"
    val:    The value of the row or column
    n:      The index on that row/column to start
    */
    private boolean filledLeftUp(String rC, int val, int n) {
        for(int i = n; i >= 0; i--) {
            if(rC.equals("r") && board[val][i] != ".") return false;
            if(rC.equals("c") && board[i][val] != ".") return false;
        }
        return true;
    }

    /*
    filledRightDown will confirm whether all spaces to the right/down from position n in the row/col
        indicated by val are filled by "."s
    Parameters:
    rC:     Will equal either "r" or "c"
    val:    The value of the row or column
    n:      The index on that row/column to start
    */
    private boolean filledRightDown(String rC, int val, int n) {
        for(int i = n; i < length; i++) {
            if(rC.equals("r") && board[val][i] != ".") return false;
            if(rC.equals("c") && board[i][val] != ".") return false;
        }
        return true;
    }
}
