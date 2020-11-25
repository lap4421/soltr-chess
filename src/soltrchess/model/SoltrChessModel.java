package soltrchess.model;

import soltrchess.backtracking.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * I am getting rid of data for now
 */


public class SoltrChessModel implements Configuration {
    /**
     * the observers of this model
     */
    private List<Observer<SoltrChessModel>> observers;

    private double moves;
    private Piece[][] board;



    public enum Piece {
        KNIGHT,
        KING,
        ROOK,
        BISHOP,
        PAWN,
        QUEEN,
        EMPTY;
    }


    public SoltrChessModel(String[] args) throws FileNotFoundException {
        this.observers = new LinkedList<>();
        moves = 0;
        board = new Piece[4][4];
        String filename = args[0];
        Scanner in = new Scanner(new File(filename));
        for (int i = 0; i < 4; i++) {
            String[] line = in.nextLine().split(" ");
            Piece[] newline = new Piece[4];
            for (int j = 0; j < 4; j++) {
                if (line[j].equals("K")) {
                    newline[j] = Piece.KING;
                } else if (line[j].equals("Q")) {
                    newline[j] = Piece.QUEEN;
                } else if (line[j].equals("B")) {
                    newline[j] = Piece.BISHOP;
                } else if (line[j].equals("N")) {
                    newline[j] = Piece.KNIGHT;
                } else if (line[j].equals("R")) {
                    newline[j] = Piece.ROOK;
                } else if (line[j].equals("P")) {
                    newline[j] = Piece.PAWN;
                } else if (line[j].equals("-")) {
                    newline[j] = Piece.EMPTY;
                } else {
                    // TODO --> what do we have to do here?? This is for bad config file
                }


            }
            board[i] = newline;

        }
    }

    /**
     * The view calls this method to add themselves as an observer of the model.
     *
     * @param observer the observer
     */
    public void addObserver(Observer<SoltrChessModel> observer) {
        this.observers.add(observer);
        System.out.println("The number of observers is: " + this.observers.size());
    }

    /**
     * When the model changes, the observers are notified via their update() method
     */
    private void notifyObservers() {
        for (Observer<SoltrChessModel> obs : this.observers) {
            obs.update(this);
        }
    }


    /**
     * This section is for backtracking and solving
     */
    @Override
    public Collection<Configuration> getSuccessors() {
        Collection<Configuration> successors = new ArrayList<Configuration>();
        // iterate through each square
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (board[row][col].equals(Piece.EMPTY)) {
                    // if that square is empty, then skip it
                    continue;
                }
                else { // we found a piece
                    // OK, this isn't the most efficient way to do this, but should work well enough
                    // when you find a piece, check the built in isValid(...params...) for every other square on the board
                    // i.e. it will have to operate 15 times per piece on the board
                    // probably adds a few orders of magnitude to execution time, but hopefully that is negligible on
                    // such a small board
                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 4; j++) {
                            if (i == row && j == col) {
                                // do nothing --> piece can never move to its own place
                                continue;
                            }
                            else if (isValid(row, col, i, j, board[row][col], board[i][j])) {
                                // now we have a valid move, and need to create a new successor for it
                                SoltrChessModel successor = new SoltrChessModel(this);
                                successor.makeMove(row, col, i, j, board[row][col]);
                                // TODO --> helpful to have a sanity check to make sure its fully copied w/ new refs
                                // and not just reference copied so change to one will change the other; i dont trust you java
                                // add successor to list
                                successors.add(successor);
                            }
                        }
                    }
                }
            }
        }
        return successors;
    }

    @Override
    public boolean isGoal() {
        return this.hasWonGame();
    }


    // This can be as involved as we want -- for pruning
    // mind that this overloads the other isValid() function in this class, but that is OK since they have diff params
    @Override
    public boolean isValid() {
        // FIRST CHECK IF IS GOAL, then automatically valid
        if (this.isGoal()) {
            return true;
        }
        // easy thing to check is are there any available captures --> if no captures available, config invalid
        // should be able to get this info from the getSuccessors() function
        System.out.println("================CHECKING VALIDITY OF=================");
        System.out.print(this);
        if (getSuccessors().size() == 0) {
            System.out.println("Returned false");
            System.out.println("=====================================================\n");
            return false;
        }
        else {
            System.out.println("Returned true");
            System.out.println("=====================================================\n");
            return true;
        }
        // TODO? --> hopefully this is good enough for pruning and nothing else has to be done
    }


    // copy constructor
    public SoltrChessModel(SoltrChessModel copy) {
        this.board = new Piece[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                this.board[row][col] = copy.board[row][col];
            }
        }
    }







    public boolean isValid(int row1, int col1, int row2, int col2, Piece piece1, Piece piece2) {
        boolean valid = false;
        if (piece1.equals(Piece.EMPTY) || piece2.equals(Piece.EMPTY)) {
            return valid;
        }

        // this is supposed to be so that you can't move to the same square, right >> yes
        if (row1 == row2 && col1 == col2) {
            return valid;
        }

        if (piece1.equals(Piece.KING)) {
            //System.out.println("Checking for king");
            if (Math.abs(row2 - row1) <= 1 && Math.abs(col2 - col1) <= 1) {
                valid = true;
            }
        }
        else if (piece1.equals(Piece.QUEEN)) {
            //System.out.println("Checking for queen");
            if (row1 != row2 && col1 == col2 || row1 == row2 && col1 != col2 || Math.abs(row2 - row1) == Math.abs(col2 - col1)) {
                valid = true;
            }
            //if she be moving straight, not diagonally
            if (row1 == row2 || col1 == col2) {
                if (col1 == col2 && Math.abs(row2 - row1) > 1) { // vert
                    for (int i = Math.min(row1, row2) + 1; i < Math.max(row1, row2); i++) {
                        if (!(board[i][col1].equals(Piece.EMPTY))) {
                            valid = false;
                            return valid;
                        }
                    }
                } else if (row1 == row2 && Math.abs(col2 - col1) > 1) { // horizontal
                    for (int j = Math.min(col1, col2) + 1; j < Math.max(col1, col2); j++) {
                        if (!(board[row1][j].equals(Piece.EMPTY))) {
                            valid = false;
                            return valid;
                        }
                    }
                }
                valid = true; // default state if neither of above two loops returns false

            } else {
                if (row2 > row1 && col2 > col1) {
                    //System.out.println("dick prime");
                    for (int i = row1 + 1; i < row2; i++) {
                        if (board[i][col1 + (i - row1)] != Piece.EMPTY) {
                            //System.out.println("DICK" + i);
                            return false;

                        }

                    }
                }
                if (row2 < row1 && col2 > col1) {
                    //System.out.println("cock prime");
                    for (int i = row1 - 1; i > row2; i--) {
                        if (board[i][col1 + (row1 - i)] != Piece.EMPTY) {
                            //System.out.println("coCK" + i);
                            return false;

                        }

                    }
                }
                if (row2 > row1 && col2 < col1) {
                    //System.out.println("cum prime");
                    for (int i = row1 + 1; i < row2; i++) {
                        if (board[i][col1 - (i - row1)] != Piece.EMPTY) {
                            //System.out.println("CUMMM" + i);
                            return false;

                        }

                    }
                }
                if (row2 < row1 && col2 < col1) {
                    //System.out.println("puss prime");
                    for (int i = row1 - 1; i > row2; i--) {
                        if (board[i][col1 - (row1 - i)] != Piece.EMPTY) {
                            //System.out.println("PUSS" + i);
                            return false;

                        }

                    }
                }
                valid = true;
            }

        }
        else if (piece1.equals(Piece.ROOK)) {
            //System.out.println("Checking for rook");
            if (row1 != row2 && col1 == col2 || row1 == row2 && col1 != col2) {
                // now right here, we need to make sure he isn't jumping over anybody
                // There might be a more efficient way to do this, but it's 12:25 AM and I am kinda lazy right now
                // split into horizontal and vertical movement
                if (col1 == col2 && Math.abs(row2 - row1) > 1) { // vert
                    for (int i = Math.min(row1, row2)+1; i < Math.max(row1, row2); i++) {
                        if (!(board[i][col1].equals(Piece.EMPTY))) {
                            valid = false;
                            return valid;
                        }
                    }
                }
                else if (row1 == row2 && Math.abs(col2 - col1) > 1) { // horizontal
                    for (int j = Math.min(col1, col2) + 1; j < Math.max(col1, col2); j++) {
                        if (!(board[row1][j].equals(Piece.EMPTY))) {
                            valid = false;
                            return valid;
                        }
                    }
                }
                valid = true; // default state if neither of above two loops returns false
            }
        }
        else if (piece1.equals(Piece.BISHOP)) {
            //System.out.println("Checking for bishop");
            if (Math.abs(row2 - row1) == Math.abs(col2 - col1)) {
                if (row2 > row1 && col2 > col1) {
                    //System.out.println("dick prime");
                    for (int i = row1 + 1; i < row2; i++) {
                        if (board[i][col1 + (i - row1)] != Piece.EMPTY) {
                            //System.out.println("DICK" + i);
                            return false;

                        }

                    }
                }
                if (row2 < row1 && col2 > col1) {
                    //System.out.println("cock prime");
                    for (int i = row1 - 1; i > row2; i--) {
                        if (board[i][col1 + (row1 - i)] != Piece.EMPTY) {
                            //System.out.println("coCK" + i);
                            return false;

                        }

                    }
                }
                if (row2 > row1 && col2 < col1) {
                    //System.out.println("cum prime");
                    for (int i = row1 + 1; i < row2; i++) {
                        if (board[i][col1 - (i - row1)] != Piece.EMPTY) {
                            //System.out.println("CUMMM" + i);
                            return false;

                        }

                    }
                }
                if (row2 < row1 && col2 < col1) {
                    //System.out.println("puss prime");
                    for (int i = row1 - 1; i > row2; i--) {
                        if (board[i][col1 - (row1 - i)] != Piece.EMPTY) {
                            //System.out.println("PUSS" + i);
                            return false;

                        }

                    }
                }
                valid = true;
            }
        }
        else if (piece1.equals(Piece.KNIGHT)) {
            //System.out.println("Checking for knight");
            int rowdif = Math.abs(row2 - row1);
            int coldif = Math.abs(col2 - col1);
            if (coldif == 2 && rowdif == 1 || coldif == 1 && rowdif == 2) {
                valid = true;
            }
        }
        else if (piece1.equals(Piece.PAWN)) {
            //System.out.println("Checking for pawn");
            if (row2 == (row1 - 1) && Math.abs(col2 - col1) == 1) {
                valid = true;
            }
        }
        else {
            //System.out.println("Returning default, should be empty");
        }
        return valid;
    }

    public boolean hasWonGame() {
        int counter = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (getBoard()[i][j] != Piece.EMPTY) {
                    counter++;
                }
            }
        }
        if (counter > 1) {
            return false;
        } else {
            return true;
        }
    }


    public void makeMove(int row1, int col1, int row2, int col2, Piece piece){
        setBoard(row1, col1, Piece.EMPTY);
        setBoard(row2, col2, piece);
    }




    public double getMoves() {
        return moves;
    }

    public Piece[][] getBoard() {
        return board;
    }

    public void setBoard(int row, int col, Piece piece){
        board[row][col] = piece;
        moves+= 0.5; // doing this because this gets executed twice per move
    }


    public void boardtoString(){
        for (int i = 0; i < 4; i++){
            StringBuilder line = new StringBuilder();
            for(int j = 0; j < 4; j ++){
                line.append(board[i][j]);

            }
            System.out.println(line);
        }
    }


    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (board[i][j].equals(Piece.KING)) {
                    result += "K";
                } else if (board[i][j].equals(Piece.QUEEN)) {
                    result += "Q";
                } else if (board[i][j].equals(Piece.BISHOP)) {
                    result += "B";
                } else if (board[i][j].equals(Piece.KNIGHT)) {
                    result += "N";
                } else if (board[i][j].equals(Piece.ROOK)) {
                    result += "R";
                } else if (board[i][j].equals(Piece.PAWN)) {
                    result += "P";
                } else { // EMPTY
                    result += "-";
                }
                if (j == 3) {
                    result += "\n";
                } else {
                    result += " ";
                }
            }
        }
        return result;
    }



}