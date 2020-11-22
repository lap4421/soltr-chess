package soltrchess.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import soltrchess.model.SoltrChessModel;
import soltrchess.model.Observer;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import static java.awt.Color.BLUE;

public class SoltrChessGUI extends Application implements Observer<SoltrChessModel> {
    boolean firstclick = true;


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

    public void updateSquare(int row, int col, Button button) {
        SoltrChessModel.Piece piece = model.getBoard()[row][col];
        System.out.println(piece);
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
                System.out.println("You can't fucking do that you absolute buffoonio");

            }
        }
        firstclick = !firstclick;


    }


    @Override
    public void update(SoltrChessModel soltrChessModel) {
        //model.boardtoString();
        model.setBoard(firstrow, firstcol, SoltrChessModel.Piece.EMPTY);




    }
    public void updaterowcol(int row, int col) {
        model.setBoard(row, col,model.getBoard()[firstrow][firstcol]);





    }



    // initializes board
    private GridPane makeGridPane() {
        ColorArray = new Boolean[4][4];
        GridPane gridPane = new GridPane();
        for (int row = 0; row < DIM; ++row) {
            for (int col = 0; col < DIM; ++col) {
                Square square;
                square = new Square(color);
                ColorArray[row][col] = color;


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

    @Override
    public void init() throws FileNotFoundException {
        params = getParameters();
        List rawArgs = params.getRaw();
        String[] args = (String[]) rawArgs.toArray(new String[1]);

        // Leave the block below in case getting the parameters breaks

        //String[] args = new String[1];
        //args[0] = "data/game43.txt";

        model = new SoltrChessModel(args);
        System.out.println("Instantiated model");
        this.model.addObserver(this);
        System.out.println("Added observer");
        System.out.println("The parameters are: " + params);
    }


    @Override
    public void start(Stage stage) throws Exception {
        this.borderPane = new BorderPane();


        board = makeGridPane();
        board.getColumnConstraints().add(new ColumnConstraints(120));
        board.getRowConstraints().add(new RowConstraints(120));


        borderPane.setCenter(board);
        borderPane.setTop(new Text("asdfasdf"));


        this.scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.setTitle("Solitaire Chess Lev and Lizzy");


        stage.show();
    }
}