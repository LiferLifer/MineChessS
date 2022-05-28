public class fourCases {
    public static void forP(Piece piece, int m){
//        int m = 1;
//        JButton q = new JButton("level up to Queen");
//        JButton r = new JButton("level up to Rook");
//        JButton b = new JButton("level up to Bishop");
//        JButton n = new JButton("level up to Knight");
//        JTabbedPane chooseToWhichPiece = new JTabbedPane(JTabbedPane.TOP);
//        chooseToWhichPiece.add(q);
//        chooseToWhichPiece.add(r);
//        chooseToWhichPiece.add(b);
//        chooseToWhichPiece.add(n);
//        chooseToWhichPiece.setVisible(true
//        );

        switch (m){
            case 0:
                piece.setName(Piece.Type.Q);
                break;
            case 1:
                piece.setName(Piece.Type.R);
                break;
            case 2:
                piece.setName(Piece.Type.B);
                break;
            case 3:
                piece.setName(Piece.Type.N);
                break;
        }
    }
}
