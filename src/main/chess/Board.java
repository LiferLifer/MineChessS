import frame.board.BaseBoard;

public class Board extends BaseBoard {

    public Board(int width, int height) {
        super(width, height);
    }

    @Override
    public void init() {
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                grids[i][j] = new Grid(i, j);
            }
        }

        Piece.PieceType[] bottomLine = new Piece.PieceType[]{
                Piece.PieceType.R,
                Piece.PieceType.N,
                Piece.PieceType.B,
                Piece.PieceType.Q,
                Piece.PieceType.K,
                Piece.PieceType.B,
                Piece.PieceType.N,
                Piece.PieceType.R
        };
        for (int i = 0; i < 8; i++) {
            grids[i][0].setOwnedPiece(new Piece(i, 0, bottomLine[i], Color.WHITE));
        }
        for (int i = 7; i >=0 ; i--) {
            grids[i][7].setOwnedPiece(new Piece(i, 7, bottomLine[i], Color.BLACK));
        }

        for (int i = 0; i < 8; i ++) {
            grids[i][1].setOwnedPiece(new Piece(i, 1, Piece.PieceType.P, Color.WHITE));
        }
        for (int i = 0; i < 8; i ++) {
            grids[i][6].setOwnedPiece(new Piece(i, 6, Piece.PieceType.P, Color.BLACK));
        }
    }
}
