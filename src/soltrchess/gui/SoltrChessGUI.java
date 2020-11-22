package soltrchess.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import soltrchess.model.SoltrChessModel;
import soltrchess.model.Observer;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import static java.awt.Color.BLUE;

public class SoltrChessGUI extends Application implements Observer<SoltrChessModel> {
    boolean firstclick = true;

    FileChooser fileChooser;
    String status = "Moves: ";

    private String fileName = "";
    private boolean color = false;
    private int firstrow;
    private int firstcol;
    private Button firstbutton;
    private Boolean[][] ColorArray;
    private boolean setupfinished = false;


    private SoltrChessModel model;
    private Parameters params;

    private BorderPane borderPane;
    private GridPane board;
    private Scene scene;
    private Image pawn = new Image(getClass().getResourceAsStream("resources/pawn.png"));
    private Image bishop = new Image(getClass().getResourceAsStream("resources/bishop.png"));
    private Image king = new Image(getClass().getResourceAsStream("resources/king.png"));
    private Image knight = new Image(getClass().getResourceAsStream("resources/knight.png"));
    private Image queen = new Image(getClass().getResourceAsStream("resources/queen.png"));
    private Image rook = new Image(getClass().getResourceAsStream("resources/rook.png"));
    private Image dark = new Image(getClass().getResourceAsStream("resources/dark.png"));
    private Image light = new Image(getClass().getResourceAsStream("resources/light.png"));




    private final static int DIM = 4;


    // TODO --> need to make this compatible with model code
    public SoltrChessGUI() {
        System.out.println("Instantiating GUI");
    }




    // don't think this is needed
    public void buildBoard() {
        for (int row = 0; row < DIM; ++row) {
            for (int col = 0; col < DIM; ++col) {

            }
        }
    }

    /**
     * 11/21/2020 22:31 -- there is some bug going on here that I haven't been able to figure out
     * If you do a new game WITHOUT making a single move, then everything is fine
     * But, if you do a new game after making any moves, then all EMPTY DARK squares are colored light in the new
     * game board
     * For some reason, this bug doesn't happen if you capture FROM a dark square TO a light square on any board...
     * If that is your most recently played move, then when you do new game everything is fine and dandy
     * @param row
     * @param col
     * @param button
     */

    public void updateSquare(int row, int col, Button button) {
        SoltrChessModel.Piece piece = model.getBoard()[row][col];
        System.out.println(piece);
        System.out.println("Color: " + color);
        System.out.println("Setup finished: " + setupfinished);
        System.out.println("Color Array stuff: " + firstrow + ", " + firstcol + ", " + ColorArray[firstrow][firstcol]);
        if (piece.equals(SoltrChessModel.Piece.PAWN)) {
            button.setGraphic(new ImageView(pawn));
        } else if (piece.equals(SoltrChessModel.Piece.ROOK)) {
            button.setGraphic(new ImageView(rook));
        } else if (piece.equals(SoltrChessModel.Piece.KING)) {
            button.setGraphic(new ImageView(king));
        } else if (piece.equals(SoltrChessModel.Piece.BISHOP)) {
            button.setGraphic(new ImageView(bishop));
        } else if (piece.equals(SoltrChessModel.Piece.QUEEN)) {
            button.setGraphic(new ImageView(queen));
        } else if (piece.equals(SoltrChessModel.Piece.KNIGHT)) {
            button.setGraphic(new ImageView(knight));
        } else if (color &&!setupfinished||ColorArray[firstrow][firstcol]) {
            button.setGraphic(new ImageView(light));
        } else if (!color &&!setupfinished|| !ColorArray[firstrow][firstcol]){
            button.setGraphic(new ImageView(dark));
        }
    }




    private class Square extends Button {
        private boolean color;

        private Image white = new Image(getClass().getResourceAsStream("resources/light.png"));
        private BackgroundImage WHITE = new BackgroundImage(white, null, null, null, null);
        private Background bw = new Background(WHITE);

        private Image blue = new Image(getClass().getResourceAsStream("resources/dark.png"));
        private BackgroundImage BLUE = new BackgroundImage(blue, null, null, null, null);
        private Background bb = new Background(BLUE);




        public Square(boolean color) {
            this.color = color;
            if (color) {
                this.setBackground(bw);
            }
            if (!color) {
                this.setBackground(bb);
            }
        }
    }

    public void click(int row, int col, Button button){
        if (firstclick) {
            firstrow = row;
            firstcol = col;
            firstbutton = button;
        }
        else{
            if (model.isValid(firstrow, firstcol, row, col, model.getBoard()[firstrow][firstcol], model.getBoard()[row][col])) {
                updaterowcol(row, col);
                update(model);
                updateSquare(firstrow, firstcol, firstbutton);
                updateSquare(row, col, button);
            }
            else{
                illegalMove();
            }
        }
        firstclick = !firstclick;


    }


    public void illegalMove() {
        status = "Moves: " + (int) model.getMoves() + "\tYou can't fucking do that you absolute buffoon";
        borderPane.setBottom(new Text(status));
    }


    @Override
    public void update(SoltrChessModel soltrChessModel) {
        //model.boardtoString();
        model.setBoard(firstrow, firstcol, SoltrChessModel.Piece.EMPTY);
        status = "Moves: " + (int) model.getMoves();
        if (model.hasWonGame()) {
            status += "\tYou Won!! Good job";
        }
        borderPane.setBottom(new Text(status));
    }


    public void updaterowcol(int row, int col) {
        model.setBoard(row, col,model.getBoard()[firstrow][firstcol]);
    }



    // initializes board
    private GridPane makeGridPane() {
        //ColorArray = new Boolean[4][4];
        GridPane gridPane = new GridPane();
        for (int row = 0; row < DIM; ++row) {
            for (int col = 0; col < DIM; ++col) {
                Square square;
                square = new Square(color);
                //ColorArray[row][col] = color;
                //System.out.println("Added color: " + color + " to ColorArray[" + row + "][" + col + "]");


                updateSquare(row, col, square);
                int finalRow = row;

                int finalRow1 = row;
                int finalCol = col;
                square.setOnAction((event) -> click(finalRow1, finalCol, square));

                gridPane.add(square, col, row);
                if (col != DIM - 1) {
                    color = !color;
                }
            }
        }

        setupfinished = true;
        return gridPane;

    }

    public void startNewgame(String filename) throws FileNotFoundException {
        // to fix a weird bug, we need to "click" any dark square --> I choose (0, 0)
        click(0, 0, (Button) board.getChildren().get(0));
        fileName = filename;
        borderPane.setTop(new Text("Game File: " + fileName));
        String[] args = new String[1];
        args[0] = filename;
        model = new SoltrChessModel(args);
        System.out.println("Changed model to new game");
        this.model.addObserver(this);
        System.out.println("Added observer to model again");
        System.out.println("About to re-make the gridpane...");
        System.out.println("Using file: " + filename);
        color = false;
        setupfinished = false;
        firstclick = true;
        GridPane newBoard = makeGridPane();
        borderPane.setCenter(newBoard);
        System.out.println("Gridpane done");
    }

    @Override
    public void init() throws FileNotFoundException {
        params = getParameters();
        List rawArgs = params.getRaw();
        String[] args = (String[]) rawArgs.toArray(new String[1]);

        // Leave the block below in case getting the parameters breaks

        //String[] args = new String[1];
        //args[0] = "data/game43.txt";

        /**
         * Need to generate the ColorArray only once, so doing it here instead of the GridPane maker
         */
        ColorArray = new Boolean[4][4];
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                ColorArray[i][j] = color;
                if (j != DIM - 1) {
                    color = !color;
                }
            }
        }


        fileName = args[0];
        model = new SoltrChessModel(args);
        System.out.println("Instantiated model");
        this.model.addObserver(this);
        System.out.println("Added observer");
        System.out.println("The parameters are: " + params);
    }


    @Override
    public void start(Stage stage) throws Exception {
        this.borderPane = new BorderPane();
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("data"));
        Button newGame = new Button("New Game");
        newGame.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(stage);
            System.out.println("Selecting new game...");
            try {
                startNewgame(String.valueOf(selectedFile));
            } catch (FileNotFoundException fileNotFoundException) {
                System.err.println("That filename ain't right");
            }
        });

        Button restart = new Button("Start Over :(");
        restart.setOnAction(e -> {
            try {
                startNewgame(fileName);
            }
            catch (FileNotFoundException fileNotFoundException) {
                System.err.println("That filename ain't right");
            }
        });


        VBox vBox = new VBox(newGame, restart);
        borderPane.setRight(vBox);


        // This is where we actually set up the board
        System.out.println("Setting up the board now...");
        board = makeGridPane();
        System.out.println("Board setup complete");
        //System.out.println("ColorArray contents");
        //for (int i = 0; i < DIM; i++) {
        //    for (int j = 0; j < DIM; j++) {
        //        System.out.println(ColorArray[i][j]);
        //    }
        //}

        board.getColumnConstraints().add(new ColumnConstraints(120));
        board.getRowConstraints().add(new RowConstraints(120));


        borderPane.setCenter(board);
        borderPane.setTop(new Text("Game File: " + fileName));
        status += (int) model.getMoves();
        borderPane.setBottom(new Text(status));

        Insets insets = new Insets(10);
        BorderPane.setMargin(board, insets);
        BorderPane.setMargin(borderPane.getTop(), insets);


        this.scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.setTitle("Solitaire Chess Lev & Lizzy");


        stage.show();
    }
}