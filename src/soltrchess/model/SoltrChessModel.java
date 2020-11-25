package soltrchess.model;

import soltrchess.gui.SoltrChessGUI;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * I am getting rid of data for now
 */


public class SoltrChessModel {
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


    public boolean isValid(int row1, int col1, int row2, int col2, Piece piece1, Piece piece2) {
        boolean valid = false;
        if (piece1.equals(Piece.EMPTY) || piece2.equals(Piece.EMPTY)) {
            return valid;
        }

        // this is supposed to be so that you can't move to the same square, right?
        if (row1 == row2 && col1 == col2) {
            return valid;
        }

        if (piece1.equals(Piece.KING)) {
            System.out.println("Checking for king");
            if (Math.abs(row2 - row1) <= 1 && Math.abs(col2 - col1) <= 1) {
                valid = true;
            }
        }
        else if (piece1.equals(Piece.QUEEN)) {
            System.out.println("Checking for queen");
            if (row1 != row2 && col1 == col2 || row1 == row2 && col1 != col2 || Math.abs(row2 - row1) == Math.abs(col2 - col1)) {
                // TODO --> need to check the queen for jumping over anybody too
                valid = true;
            }
        }
        else if (piece1.equals(Piece.ROOK)) {
            System.out.println("Checking for rook");
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
            System.out.println("Checking for bishop");
            if (Math.abs(row2 - row1) == Math.abs(col2 - col1)) {
                // check if the bishop is jumping over anybody
                // distinguish between "upward" and "downward" diagonal
                // TODO --> fix the bishop
                valid = true;
            }
        }
        else if (piece1.equals(Piece.KNIGHT)) {
            System.out.println("Checking for knight");
            int rowdif = Math.abs(row2 - row1);
            int coldif = Math.abs(col2 - col1);
            if (coldif == 2 && rowdif == 1 || coldif == 1 && rowdif == 2) {
                valid = true;
            }
        }
        else if (piece1.equals(Piece.PAWN)) {
            System.out.println("Checking for pawn");
            if (row2 == (row1 - 1) && Math.abs(col2 - col1) == 1) {
                valid = true;
            }
        }
        else {
            System.out.println("Returning default, should be empty");
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
        String result = "This text is here in place of the actual board herp derp";
        return result;
    }


}