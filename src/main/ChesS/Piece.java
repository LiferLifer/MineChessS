import frame.Controller.Game;
import frame.board.BasePiece;
import frame.util.Point2D;

import java.util.ArrayList;

public class Piece extends BasePiece {
    
    public enum PieceType {
        Q,R,B,N,K,P
    }
    //classify by color: white, black, and null

    private PieceType name;
    private final Color color;
    
    public Piece(int x, int y, PieceType name, Color color) {
        super(x, y);
        this.name = name;
        this.color = color;
    }
    
    public Color getColor() {
        return color;
    }
    public PieceType getName() {
        return name;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    
    public void setName(PieceType name) {
        this.name = name;
    }

    private Color checkPieceColor(int x, int y) {
        BasePiece piece = Game.getBoard().getGrid(x, y).getOwnedPiece();
        if (piece == null) {
            return Color.NULL;
        }
        return ((Piece) piece).color;
    }

    public ArrayList<Point2D> canMoveTo() {
        ArrayList<Point2D> result = new ArrayList<>();
        switch (name) {
            case R:
                for (int i = y + 1; i < 8; i++) {
                    if (checkPieceColor(x, i) == color) break;
                    result.add(new Point2D(x, i));
                    if (checkPieceColor(x, i) != Color.NULL) break;
                }
                for (int i = y - 1; i >= 0; i--) {
                    if (checkPieceColor(x, i) == color) break;
                    result.add(new Point2D(x, i));
                    if (checkPieceColor(x, i) != Color.NULL) break;
                }
                for (int i = x + 1; i < 8; i++) {
                    if (checkPieceColor(i, y) == color) break;
                    result.add(new Point2D(i, y));
                    if (checkPieceColor(i, y) != Color.NULL) break;
                }
                for (int i = x - 1; i >= 0; i--) {
                    if (checkPieceColor(i, y) == color) break;
                    result.add(new Point2D(i, y));
                    if (checkPieceColor(i, y) != Color.NULL) break;
                }
                break;
            case N:
                result.add(new Point2D(x + 1, y + 2));
                result.add(new Point2D(x + 1, y - 2));
                result.add(new Point2D(x - 1, y + 2));
                result.add(new Point2D(x - 1, y - 2));
                result.add(new Point2D(x + 2, y + 1));
                result.add(new Point2D(x + 2, y - 1));
                result.add(new Point2D(x - 2, y + 1));
                result.add(new Point2D(x - 2, y - 1));
                break;
            case P:
                if (color == Color.BLACK) {
                    result.add(new Point2D(x, y - 1));
                    if (y < 5) {
                        result.add(new Point2D(x - 1, y));
                        result.add(new Point2D(x + 1, y));
                    }
                } else if (color == Color.WHITE) {
                    result.add(new Point2D(x, y + 1));
                    if (y > 4) {
                        result.add(new Point2D(x - 1, y));
                        result.add(new Point2D(x + 1, y));
                    }
                }
                break;
            case B:
                result.add(new Point2D(x + 2, y + 2));
                result.add(new Point2D(x + 2, y - 2));
                result.add(new Point2D(x - 2, y + 2));
                result.add(new Point2D(x - 2, y - 2));
                result.removeIf(p -> {
                    if (color == Color.WHITE) {
                        return p.y > 4;
                    } else {
                        return p.y < 5;
                    }
                });
                break;
            case K:
                result.add(new Point2D(x + 2, y + 2));
                result.add(new Point2D(x + 2, y - 2));
                result.add(new Point2D(x - 2, y + 2));
                result.add(new Point2D(x - 2, y - 2));
                result.removeIf(p -> {
                    if (color == Color.WHITE) {
                        return p.y > 4;
                    } else {
                        return p.y < 5;
                    }
                });
                break;
            case Q:
                result.add(new Point2D(x + 2, y + 2));
                result.add(new Point2D(x + 2, y - 2));
                result.add(new Point2D(x - 2, y + 2));
                result.add(new Point2D(x - 2, y - 2));
                result.removeIf(p -> {
                    if (color == Color.WHITE) {
                        return p.y > 4;
                    } else {
                        return p.y < 5;
                    }
                });
                break;
        }
        result.removeIf(p -> p.x < 0 || p.x >= 9 || p.y < 0 || p.y >= 10);
        result.removeIf(p -> checkPieceColor(p.x, p.y) == color);
        return result;
    }

    
}
