package soltrchess.ptui;

import soltrchess.model.Observer;
import soltrchess.model.SoltrChessModel;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class SoltrChessPTUI implements Observer<SoltrChessModel> {

    private SoltrChessModel board;
    private String currentFileName;

    /**
     * Construct the PTUI
     *
     * I think this is how it works:
     * - Should connect to the model, be an observer of it
     * - Then when running it loops until game is over; sends move to model and then prints out new board state
     * - Plus basic error handling
     * 11/25 @ 12:45 (Lev) - Finished except for hint and solve
     * just make sure that toString() is implemented in SoltrChessModel
     *
     * @param filename the file name
     */
    public SoltrChessPTUI(String filename) throws FileNotFoundException {
        System.out.println("Game file: " + filename);
        initializeView(filename);
    }

    @Override
    public void update(SoltrChessModel soltrChessModel) {
        System.out.println(this.board);
    }

    private void initializeView(String filename) throws FileNotFoundException {
        String[] args = new String[1];
        args[0] = filename;
        currentFileName = filename;
        board = new SoltrChessModel(args);
        board.addObserver(this);
    }

    /**
     * The main command loop.
     */
    public void run() throws FileNotFoundException {
        update(board);
        while (true) {
            System.out.print("[move, new, restart, quit]: "); // TODO --> add hint, solve
            Scanner in = new Scanner(System.in);
            String s = in.next();
            if (s.equals("move")) {
                System.out.print("source row? ");
                int r1 = Integer.parseInt(in.next());
                System.out.print("source col? ");
                int c1 = Integer.parseInt(in.next());
                System.out.print("dest row? ");
                int r2 = Integer.parseInt(in.next());
                System.out.print("dest col? ");
                int c2 = Integer.parseInt(in.next());
                if (board.isValid(r1, c1, r2, c2, board.getBoard()[r1][c1], board.getBoard()[r2][c2])) {
                    System.out.println(board.getBoard()[r1][c1] + " to (" + r2 + ", " + c2 + ")");
                    // here update the board for the move that was made, and then check if won the game
                    board.makeMove(r1, c1, r2, c2, board.getBoard()[r1][c1]);

                    // if won, go back to beginning of loop; user should either restart or new game
                    if (board.hasWonGame()) {
                        System.out.println("You won! Good job");
                    }
                    // if not won, go back to the top of while loop for the next turn
                }
                else {
                    System.out.println("You can't do that...");
                    // go back to top of while loop
                }
            }
            else if (s.equals("new")) {
                System.out.print("game filename: ");
                String filename = in.next();
                System.out.println("New Game " + filename);
                initializeView(filename);
                update(board);
                // TODO --> nothing left here, but make sure the toString() method is implemented in SoltrChessModel
                continue;
            }
            else if (s.equals("restart")) {
                initializeView(currentFileName);
                update(board);
                // TODO --> nothing left here, but make sure the toString() method is implemented in SoltrChessModel
                continue;
            }
            else if (s.equals("quit")) {
                break;
            }
            else {
                System.out.println("Command invalid");
            }
        }
        System.out.println("Goodbye");
    }
}