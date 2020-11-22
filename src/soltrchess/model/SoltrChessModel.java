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
        if (row1 == col1 && row2 == col2) {
            return valid;
        }

        switch (piece1) {
            case KING:
                if (Math.abs(row2 - row1) <= 1 && Math.abs(col2 - col1) <= 1) {
                    valid = true;
                }
            case QUEEN:
                if (row1 != row2 && col1 == col2 || row1 == row2 && col1 != col2 || Math.abs(row2 - row1) == Math.abs(col2 - col1)) {
                    valid = true;
                }
            case ROOK:
                if (row1 != row2 && col1 == col2 || row1 == row2 && col1 != col2) {
                    valid = true;
                }
            case BISHOP:
                if (Math.abs(row2 - row1) == Math.abs(col2 - col1)) {
                    valid = true;
                }
            case KNIGHT:
                int rowdif = Math.abs(row2 - row1);
                int coldif = Math.abs(col2 - col1);
                if (coldif == 2 && rowdif == 1 || coldif == 1 && rowdif == 2) {
                    valid = true;
                }
            case PAWN:
                if (row2 == (row1 + 1) && Math.abs(col2 - col1) == 1) {
                    valid = true;
                }

            default:
                return valid;
        }
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




}