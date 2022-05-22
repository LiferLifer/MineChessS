import frame.Controller.Game;
import frame.board.BasePiece;
import frame.util.Point2D;

import java.util.ArrayList;

public class Piece extends BasePiece {

    public enum Type {
        Q,R,B,N,K,P
    }
    //classify by color: white, black, and null

    private Type name;
    private final ChessColor chessColor;

    public Piece(int x, int y, Type name, ChessColor chessColor) {
        super(x, y);
        this.name = name;
        this.chessColor = chessColor;
    }

    public ChessColor getColor() {
        return chessColor;
    }
    public Type getName() {
        return name;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public void setName(Type name) {
        this.name = name;
    }

    private ChessColor checkPieceColor(int x, int y) {
        BasePiece piece = Game.getBoard().getGrid(x, y).getOwnedPiece();
        if (piece == null) {
            return ChessColor.NULL;
        }
        return ((Piece) piece).chessColor;
    }

    //check if beyond boundary
    private boolean checkBoundary(int x, int y){
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    public ArrayList<Point2D> canMoveTo() {
        ArrayList<Point2D> result = new ArrayList<>();
        //move array
        int[][] vector = new int[][]{{-1,-1},{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1}};

        if(name== Type.R){
            for(int i=1;i<8;i+=2){
                for(int j=1;j<8;j++){
                    if(checkBoundary(x+vector[i][0]*j, y+vector[i][1]*j)){
                        if (checkPieceColor(x+vector[i][0]*j, y+vector[i][1]*j) == chessColor) break;
                        result.add(new Point2D(x+vector[i][0]*j, y+vector[i][1]*j));
                        if (checkPieceColor(x+vector[i][0]*j, y+vector[i][1]*j) != ChessColor.NULL) break;
                    }
                }
            }
        }else if(name== Type.N){
            result.add(new Point2D(x + 1, y + 2));
            result.add(new Point2D(x + 1, y - 2));
            result.add(new Point2D(x - 1, y + 2));
            result.add(new Point2D(x - 1, y - 2));
            result.add(new Point2D(x + 2, y + 1));
            result.add(new Point2D(x + 2, y - 1));
            result.add(new Point2D(x - 2, y + 1));
            result.add(new Point2D(x - 2, y - 1));
        }else if(name== Type.P){
            if (chessColor == ChessColor.BLACK) {
                if (checkBoundary(x, y - 1) && checkPieceColor(x, y - 1) != ChessColor.WHITE){
                    result.add(new Point2D(x, y - 1));
                }
                if (y == 6) {
                    if (checkBoundary(x, y - 2) && checkPieceColor(x, y - 2) != ChessColor.WHITE){
                        result.add(new Point2D(x, y - 2));
                    }
                }
                if (checkBoundary(x + 1, y - 1) && checkPieceColor(x + 1, y - 1) == ChessColor.WHITE){
                    result.add(new Point2D(x + 1, y - 1));
                }
                if (checkBoundary(x - 1, y - 1) && checkPieceColor(x - 1, y - 1) == ChessColor.WHITE){
                    result.add(new Point2D(x - 1, y - 1));
                }
            }else if(chessColor == ChessColor.WHITE) {
                if (checkBoundary(x, y + 1) && checkPieceColor(x, y + 1) != ChessColor.BLACK){
                    result.add(new Point2D(x, y + 1));
                }
                if (y == 1) {
                    if (checkBoundary(x, y + 2) && checkPieceColor(x, y + 2) != ChessColor.BLACK){
                        result.add(new Point2D(x, y + 2));
                    }
                }
                if (checkBoundary(x + 1, y + 1) && checkPieceColor(x + 1, y + 1) == ChessColor.BLACK){
                    result.add(new Point2D(x + 1, y + 1));
                }
                if (checkBoundary(x - 1, y + 1) && checkPieceColor(x - 1, y + 1) == ChessColor.BLACK){
                    result.add(new Point2D(x - 1, y + 1));
                }
            }
        }else if(name== Type.B){
            for(int i=0;i<8;i+=2){
                for(int j=1;j<8;j++){
                    if(checkBoundary(x+vector[i][0]*j, y+vector[i][1]*j)){
                        if (checkPieceColor(x+vector[i][0]*j, y+vector[i][1]*j) == chessColor) break;
                        result.add(new Point2D(x+vector[i][0]*j, y+vector[i][1]*j));
                        if (checkPieceColor(x+vector[i][0]*j, y+vector[i][1]*j) != ChessColor.NULL) break;
                    }
                }
            }
        }else if(name== Type.K){
            result.add(new Point2D(x + 1, y));
            result.add(new Point2D(x, y - 1));
            result.add(new Point2D(x - 1, y));
            result.add(new Point2D(x, y + 1));
            result.add(new Point2D(x + 1, y + 1));
            result.add(new Point2D(x + 1, y - 1));
            result.add(new Point2D(x - 1, y + 1));
            result.add(new Point2D(x - 1, y - 1));
        }else if(name== Type.Q){
            for(int i=0;i<8;i++){
                for(int j=1;j<8;j++){
                    if(checkBoundary(x+vector[i][0]*j, y+vector[i][1]*j)){
                        if (checkPieceColor(x+vector[i][0]*j, y+vector[i][1]*j) == chessColor) break;
                        result.add(new Point2D(x+vector[i][0]*j, y+vector[i][1]*j));
                        if (checkPieceColor(x+vector[i][0]*j, y+vector[i][1]*j) != ChessColor.NULL) break;
                    }
                }
            }
        }

        result.removeIf(p -> p.x < 0 || p.x >= 8 || p.y < 0 || p.y >= 8);
        result.removeIf(p -> checkPieceColor(p.x, p.y) == chessColor);
        return result;
    }

}
