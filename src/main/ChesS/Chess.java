import frame.Controller.Game;
import frame.action.Action;
import frame.action.ActionPerformType;
import frame.board.BaseGrid;
import frame.event.BoardChangeEvent;
import frame.event.EventCenter;
import frame.player.AIPlayer;
import frame.player.PlayerManager;
import frame.util.Point2D;
import frame.view.View;
import frame.view.board.BoardView;
import frame.view.board.GridPanelView;
import frame.view.components.BackgroundImagePanel;
import frame.view.sound.AudioPlayer;
import frame.view.stage.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static java.awt.Image.SCALE_SMOOTH;


public class Chess {

    public static boolean isSelecting = false;
    public static Piece selectedPiece = null;
    public static Piece Pz = null;
    public static ArrayList<Point2D> canMovePositions = new ArrayList<>();
    public static ArrayList<Point2D> warnPositions = new ArrayList<>();

    public static Piece.Type LastEatenType;
    public static Piece LastEatenPiece;
    public static boolean Jiang;

    private static void initialize(){
        View.setName("\n");
        View.window.setSize(950, 600);
        Game.setBoardSize(8, 8);
        Game.saver.checkSize(true);
        Game.saver.setSlotNumber(7);
        Game.setMaximumPlayer(2);
    }

    //reset component variable
    public static void clear(){
        isSelecting = false;
        selectedPiece = null;
        canMovePositions = new ArrayList<>();

        LastEatenType = null;
        LastEatenPiece = null;
    }

    //where are your pieces
    public static ArrayList<Piece> allPieces(ChessColor n){
        ArrayList<Piece> result = new ArrayList<>();
        for (int i = 0; i < Game.getWidth(); i++) {
            for (int j = 0; j < Game.getHeight(); j++) {
                Grid grid = (Grid) Game.getBoard().getGrid(i, j);
                if (grid.hasPiece()) {
                    Piece piece = (Piece) grid.getOwnedPiece();
                    if (piece.getColor() == n) {
                        result.add(piece);
                    }
                }
            }
        }
        return result;
    }

    //know which pieces will be eaten by the other player
    public static ArrayList<Point2D> canBeEatenPieces(ChessColor n){
        ArrayList<Point2D> result = new ArrayList<>();
        switch (n){
            case WHITE:
                for (int i = 0; i < Game.getWidth(); i++) {
                    for (int j = 0; j < Game.getHeight(); j++) {
                        Grid grid = (Grid) Game.getBoard().getGrid(i, j);
                        if (grid.hasPiece()) {
                            Piece piece = (Piece) grid.getOwnedPiece();
                            if (piece.getColor() == ChessColor.BLACK) {
                                result.addAll(piece.canMoveTo());
                                }
                            }
                        }
                    }
                result.removeIf(p -> Piece.checkPieceColor(p.x, p.y) != ChessColor.WHITE);
                break;
            case BLACK:
                for (int i = 0; i < Game.getWidth(); i++) {
                    for (int j = 0; j < Game.getHeight(); j++) {
                        Grid grid = (Grid) Game.getBoard().getGrid(i, j);
                        if (grid.hasPiece()) {
                            Piece piece = (Piece) grid.getOwnedPiece();
                            if (piece.getColor() == ChessColor.WHITE) {
                                result.addAll(piece.canMoveTo());
                            }
                        }
                    }
                }
                result.removeIf(p -> Piece.checkPieceColor(p.x, p.y) != ChessColor.BLACK);
                break;
            case NULL:
                break;
        }
        return result;
    }

    //know where can move to
    public static ArrayList<Point2D> canMove(ChessColor n){
        ArrayList<Point2D> result = new ArrayList<>();
        switch (n){
            case WHITE:
                for (int i = 0; i < Game.getWidth(); i++) {
                    for (int j = 0; j < Game.getHeight(); j++) {
                        Grid grid = (Grid) Game.getBoard().getGrid(i, j);
                        if (grid.hasPiece()) {
                            Piece piece = (Piece) grid.getOwnedPiece();
                            if (piece.getColor() == ChessColor.WHITE) {
                                result.addAll(piece.canMoveTo());
                            }
                        }
                    }
                }
                break;
            case BLACK:
                for (int i = 0; i < Game.getWidth(); i++) {
                    for (int j = 0; j < Game.getHeight(); j++) {
                        Grid grid = (Grid) Game.getBoard().getGrid(i, j);
                        if (grid.hasPiece()) {
                            Piece piece = (Piece) grid.getOwnedPiece();
                            if (piece.getColor() == ChessColor.BLACK) {
                                result.addAll(piece.canMoveTo());
                            }
                        }
                    }
                }
                break;
            case NULL:
                result = null;
                break;
        }
        return result;
    }

    public static ArrayList<Point2D> canMoveP(ChessColor n){
        ArrayList<Point2D> result = new ArrayList<>();
        switch (n){
            case WHITE:
                for (int i = 0; i < Game.getWidth(); i++) {
                    for (int j = 0; j < Game.getHeight(); j++) {
                        Grid grid = (Grid) Game.getBoard().getGrid(i, j);
                        if (grid.hasPiece()) {
                            Piece piece = (Piece) grid.getOwnedPiece();
                            if (piece.getColor() == ChessColor.WHITE && piece.getName() == Piece.Type.P) {
                                result.add(new Point2D(piece.getX()-1, piece.getY()+1 ));
                                result.add(new Point2D(piece.getX()+1, piece.getY()+1 ));
                            }
                        }
                    }
                }
                break;
            case BLACK:
                for (int i = 0; i < Game.getWidth(); i++) {
                    for (int j = 0; j < Game.getHeight(); j++) {
                        Grid grid = (Grid) Game.getBoard().getGrid(i, j);
                        if (grid.hasPiece()) {
                            Piece piece = (Piece) grid.getOwnedPiece();
                            if (piece.getColor() == ChessColor.BLACK && piece.getName() == Piece.Type.P) {
                                result.add(new Point2D(piece.getX()-1, piece.getY()-1 ));
                                result.add(new Point2D(piece.getX()+1, piece.getY()-1 ));
                            }
                        }
                    }
                }
                break;
            case NULL:
                result = null;
                break;
        }
        if(result !=null && result.size()!=0) result.removeIf(p -> p.x < 0 || p.x >= 8 || p.y < 0 || p.y >= 8);
        return result;
    }

    public static ArrayList<Point2D> cantMoveP(ChessColor n){
        ArrayList<Point2D> result = new ArrayList<>();
        switch (n){
            case WHITE:
                for (int i = 0; i < Game.getWidth(); i++) {
                    for (int j = 0; j < Game.getHeight(); j++) {
                        Grid grid = (Grid) Game.getBoard().getGrid(i, j);
                        if (grid.hasPiece()) {
                            Piece piece = (Piece) grid.getOwnedPiece();
                            if (piece.getColor() == ChessColor.WHITE && piece.getName() == Piece.Type.P) {
                                result.add(new Point2D(piece.getX(), piece.getY()+1 ));
                            }
                        }
                    }
                }
                break;
            case BLACK:
                for (int i = 0; i < Game.getWidth(); i++) {
                    for (int j = 0; j < Game.getHeight(); j++) {
                        Grid grid = (Grid) Game.getBoard().getGrid(i, j);
                        if (grid.hasPiece()) {
                            Piece piece = (Piece) grid.getOwnedPiece();
                            if (piece.getColor() == ChessColor.BLACK && piece.getName() == Piece.Type.P) {
                                result.add(new Point2D(piece.getX(), piece.getY()-1 ));
                            }
                        }
                    }
                }
                break;
            case NULL:
                result = null;
                break;
        }
        if(result !=null && result.size()!=0) result.removeIf(p -> p.x < 0 || p.x >= 8 || p.y < 0 || p.y >= 8);
        return result;
    }



    public static void main(String[] args) {

        //GUI Begin
        initialize();

        MenuStage.instance().rank.setText("     Rank    ");
        MenuStage.instance().rank.setFont(new Font("INK Free",Font.PLAIN,20));
        MenuStage.instance().rank.setBackground(new Color(60, 63, 65));
        MenuStage.instance().newGame.setText("     New     ");
        MenuStage.instance().newGame.setFont(new Font("INK Free",Font.PLAIN,22));
        MenuStage.instance().newGame.setBackground(new Color(60, 63, 65));
        MenuStage.instance().load.setText("     Load     ");
        MenuStage.instance().load.setFont(new Font("INK Free",Font.PLAIN,20));
        MenuStage.instance().quit.setText("     Quit     ");
        MenuStage.instance().quit.setFont(new Font("INK Free",Font.PLAIN,20));
        MenuStage.instance().load.setBackground(new Color(60, 63, 65));
        MenuStage.instance().quit.setBackground(new Color(60, 63, 65));
        MenuStage.instance().settings.setVisible(true);
        MenuStage.instance().settings.setText("     Setting    ");
        MenuStage.instance().settings.setFont(new Font("INK Free",Font.PLAIN,20));
        MenuStage.instance().settings.setBackground(new java.awt.Color(60, 63, 65));

        MenuStage.instance().quit.setForeground(new Color(169, 183, 198));
        MenuStage.instance().load.setForeground(new Color(169, 183, 198));
        MenuStage.instance().newGame.setForeground(new Color(169, 183, 198));
        MenuStage.instance().rank.setForeground(new Color(169, 183, 198));
        MenuStage.instance().settings.setForeground(new java.awt.Color(169, 183, 198));


        Settings.instance().title.setText("{ Set }");
        Settings.instance().title.setFont(new Font("INK Free",Font.BOLD,70));
        Settings.instance().title.setForeground(new Color(169, 183, 198));
        Settings.instance().setRoomBG.setForeground(new Color(169, 183, 198));
        Settings.instance().setLoadBG.setForeground(new Color(169, 183, 198));
        Settings.instance().setPiecePicture.setForeground(new Color(169, 183, 198));
        Settings.instance().setMenuBG.setForeground(new Color(169, 183, 198));
        Settings.instance().setGameBG.setForeground(new Color(169, 183, 198));
        Settings.instance().setBoardBG.setForeground(new Color(169, 183, 198));
        Settings.instance().back.setForeground(new Color(169, 183, 198));

        Settings.instance().setGameBG.setBackground(new java.awt.Color(60, 63, 65));
        Settings.instance().setBoardBG.setBackground(new java.awt.Color(60, 63, 65));
        Settings.instance().setMenuBG.setBackground(new java.awt.Color(60, 63, 65));
        Settings.instance().setPiecePicture.setBackground(new java.awt.Color(60, 63, 65));
        Settings.instance().setLoadBG.setBackground(new java.awt.Color(60, 63, 65));
        Settings.instance().setRoomBG.setBackground(new java.awt.Color(60, 63, 65));
        Settings.instance().back.setBackground(new java.awt.Color(60, 63, 65));

        RoomStage.instance().textHeight.setVisible(false);
        RoomStage.instance().textWidth.setVisible(false);

        LoadStage.instance().title.setText("{ LOAD }");
        LoadStage.instance().title.setFont(new Font("INK Free",Font.BOLD,70));
        LoadStage.instance().title.setForeground(new Color(251, 251, 251));
        LoadStage.instance().fileChooserButton.setText("Select Files");
        LoadStage.instance().fileChooserButton.setBackground(new Color(60, 63, 65));
        LoadStage.instance().fileChooserButton.setForeground(new Color(169, 183, 198));
        LoadStage.instance().back.setText("Back  Menu");
        LoadStage.instance().back.setBackground(new Color(60, 63, 65));
        LoadStage.instance().back.setForeground(new Color(169, 183, 198));


        RoomStage.instance().back.setText("Menu ");
        RoomStage.instance().back.setFont(new Font("INK Free",Font.PLAIN,20));
        RoomStage.instance().back.setBackground(new Color(169, 183, 198));
        RoomStage.instance().back.setForeground(new Color(60, 63, 65));
        RoomStage.instance().start.setFont(new Font("INK Free",Font.PLAIN,20));
        RoomStage.instance().start.setBackground(new Color(169, 183, 198));
        RoomStage.instance().start.setForeground(new Color(60, 63, 65));

        RankingStage.instance().title.setText("{ RANK }");
        RankingStage.instance().title.setFont(new Font("INK Free",Font.BOLD,70));
        RankingStage.instance().title.setForeground(new Color(17, 17, 19));
        RankingStage.instance().back.setBackground(new Color(169, 183, 198));
        RankingStage.instance().back.setForeground(new Color(60, 63, 65));

        RankingStage.instance().back.setForeground(new Color(169, 183, 198));
        RankingStage.instance().back.setBackground(new Color(60, 63, 65));
        RankingStage.instance().back.setForeground(new Color(169, 183, 198));
        RankingStage.instance().back.setForeground(new Color(60, 63, 65));
        RankingStage.instance().back.setBackground(new Color(169, 183, 198));


//        GameStage.instance().add("Set",MenuStage.instance().settings);
        GameStage.instance().menuButton.setBackground(new Color(248, 248, 248));
        GameStage.instance().undoButton.setBackground(new Color(248, 248, 248));
        GameStage.instance().saveButton.setBackground(new Color(248, 248, 248));

        //set the bgm
        MusicPlayer bgm = new MusicPlayer("C:/MineChessS/src/main/resources/eS=S - 8bit Faith.mp3");
        bgm.start();

        //change the background
        try {
            Image chessBackground = ImageIO.read(new File("C:/MineChessS/src/main/resources/Snipaste_2022-05-22_14-17-21.png"));
            Image menuBackground = ImageIO.read(new File("C:/MineChessS/src/main/resources/menu.png"));
            Image roomBackground = ImageIO.read(new File("C:/MineChessS/src/main/resources/room.png"));
            Image rankBG = ImageIO.read(new File("C:/MineChessS/src/main/resources/game.png"));
            Image loadBackground = ImageIO.read(new File("C:/MineChessS/src/main/resources/game.png"));
            Image setBG = ImageIO.read(new File("C:/MineChessS/src/main/resources/game.png"));
            Image gameBG = ImageIO.read(new File("C:/MineChessS/src/main/resources/game.png"));
            View.setBoardViewPattern(() -> new BoardView(chessBackground) {});
            MenuStage.instance().setBackgroundImage(menuBackground);
            RoomStage.instance().setBackgroundImage(roomBackground);
            LoadStage.instance().setBackgroundImage(loadBackground);
            RankingStage.instance().setBackgroundImage(rankBG);
            GameStage.instance().setBackgroundImage(gameBG);
            Settings.instance().setBackgroundImage(setBG);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //register board
        Game.registerBoard(Board.class);
        Settings.instance().setVisible(true);
        View.addStage("SettingStage", Settings.instance());

        JButton resetGame = new JButton("Reset Game");
        resetGame.setBackground(new java.awt.Color(248, 248, 248));
        resetGame.addActionListener((e) -> {
            Game.init();
            clear();
        });

        JButton settings = new JButton("Settings");
        settings.addActionListener((e) -> {
            View.changeStage("SettingStage");
        });

        BackgroundImagePanel leftPanel = new BackgroundImagePanel();
        BackgroundImagePanel rightPanel = new BackgroundImagePanel();

//        rightPanel.add(settings);

        JButton q = new JButton("Q");
        JButton r = new JButton("R");
        JButton b = new JButton("B");
        JButton n = new JButton("N");

        leftPanel.add(q);
        leftPanel.add(r);
        leftPanel.add(b);
        leftPanel.add(n);
        q.setVisible(false);
        r.setVisible(false);
        b.setVisible(false);
        n.setVisible(false);

        GameStage.instance().add("East", rightPanel);
        GameStage.instance().add("West", leftPanel);

        JLabel currentPlayerLabel = new JLabel();
        EventCenter.subscribe(BoardChangeEvent.class, e ->
                currentPlayerLabel.setText((ChessColor.values()[Game.getCurrentPlayerIndex()].name())+"'s turn"));
        currentPlayerLabel.setFont(new Font("INK Free",Font.BOLD,25));

        JButton loadButton = new JButton("Load Game");
        loadButton.setBackground(new Color(248, 248, 248));
        loadButton.addActionListener((e) -> {
            View.changeStage("MenuStage");
            View.changeStage("LoadStage");
        });

        MenuStage.instance().quit.addActionListener((e) -> {
            System.exit(0);
        });

        JButton MusicOn = new JButton("Music On");
        MusicOn.setForeground(new Color(169, 183, 198));
        MusicOn.setBackground(new Color(60, 63, 65));
        MusicOn.addActionListener((e) -> {
            bgm.start();
        });

        JButton MusicOff = new JButton("Music Off");
        MusicOff.setForeground(new Color(169, 183, 198));
        MusicOff.setBackground(new Color(60, 63, 65));
        MusicOn.addActionListener((e) -> {
            MusicPlayer end = new MusicPlayer(null);
            end.start();
        });

        RankingStage.instance().rankingTable.setOpaque(false);
        RankingStage.instance().rankingPanel.setOpaque(false);
        RankingStage.instance().rankingPanel.getViewport().setOpaque(false);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setOpaque(false);
        RankingStage.instance().rankingTable.setDefaultRenderer(Object.class,renderer);
        Dimension viewSize = new Dimension();
        viewSize.setSize(RankingStage.instance().rankingTable.getSize());
//        RankingStage.instance().rankingTable.setPreferredSize(viewSize);
        RankingStage.instance().rankingTable.setPreferredScrollableViewportSize(viewSize);

//        MenuStage.instance().setCustomDrawMethod(() -> {
//            MenuStage music = MenuStage.instance();
//            music.buttonPanel.add(MusicOn);
//            music.buttonPanel.add(MusicOff);
//        });

        GameStage.instance().setCustomDrawMethod(() -> {
            GameStage stage = GameStage.instance();

            stage.menuBar.add(resetGame);
            stage.menuBar.add(stage.saveButton);
            stage.menuBar.add(stage.undoButton);
            stage.menuBar.add(stage.menuButton);
            stage.menuBar.add(loadButton);
            stage.scoreBoard.add(currentPlayerLabel);
            stage.add("South", stage.scoreBoard);
            stage.add("North", stage.menuBar);
        });


        //GUI End

        /*------------------------------------------------------------------------------------------*/

        //bottom logic

        //chess piece action
        Game.registerGridAction((x, y) -> true, (x, y, mouseButton) -> {
            if (mouseButton == 1) {

                int selectedX = 0;
                int selectedY = 0;

                if (selectedPiece != null) {
                    selectedX = selectedPiece.getX();
                    selectedY = selectedPiece.getY();
                }

                warnPositions = null;

                int spaceX = selectedX;
                int spaceY = selectedY;

                return new Action(true) {

                    Piece removedPiece = null;

                    @Override
                    public ActionPerformType perform() {
                        if (!isSelecting) {
                            BaseGrid selectedGrid = Game.getBoard().getGrid(x, y);
                            if (!selectedGrid.hasPiece()){
                                return ActionPerformType.FAIL;
                            }else{
                                Piece piece = (Piece) selectedGrid.getOwnedPiece();
                                if (piece.getColor() != ChessColor.values()[Game.getCurrentPlayerIndex()]) {
                                    return ActionPerformType.FAIL;
                                }else{
                                    AudioPlayer.playSound("C:/MineChessS/src/main/resources/8????????????????????? _ ??????1 - Freesound.wav");

                                    selectedPiece = piece;
                                    isSelecting = true;
                                    canMovePositions = piece.canMoveTo();

                                    if(Piece.checkPieceType(piece.getX(),piece.getY()) == Piece.Type.K){
//                                        warnPositions.addAll(canMove(ChessColor.values()[Game.getNextPlayerIndex()]));
//                                        warnPositions.addAll(canMoveP(ChessColor.values()[Game.getNextPlayerIndex()]));
                                        canMovePositions.removeIf(p -> canMove(ChessColor.values()[Game.getNextPlayerIndex()]).contains(p) || canMoveP(ChessColor.values()[Game.getNextPlayerIndex()]).contains(p));
//                                        canMovePositions.addAll(p -> cantMoveP(ChessColor.values()[Game.getNextPlayerIndex()]).contains(p));
                                    }

                                    return ActionPerformType.PENDING;
                                }
                            }
                        } else {
                            isSelecting = false;
                            for (Point2D point : canMovePositions) {
                                if (point.x == x && point.y == y) {
                                    this.removedPiece = (Piece) Game.getBoard().movePiece(selectedPiece.getX(), selectedPiece.getY(), x, y);
                                    if (this.removedPiece != null) {
                                        LastEatenType = this.removedPiece.getName();
                                    }

                                    AudioPlayer.playSound("C:/MineChessS/src/main/resources/pman - Freesound.wav");

                                    //bottom change
                                    if((selectedPiece.getName() == Piece.Type.P && selectedPiece.getY() == 7) || (selectedPiece.getName() == Piece.Type.P && selectedPiece.getY() == 0)){
                                        Pz = selectedPiece;
                                        q.setVisible(true);
                                        r.setVisible(true);
                                        b.setVisible(true);
                                        n.setVisible(true);
                                        q.addActionListener((e) -> {
                                            FourCases.forP(Pz,0);
                                            q.setVisible(false);
                                            r.setVisible(false);
                                            b.setVisible(false);
                                            n.setVisible(false);
                                            EventCenter.publish(new BoardChangeEvent(e));
                                        });
                                        r.addActionListener((e) -> {
                                            FourCases.forP(Pz,1);
                                            q.setVisible(false);
                                            r.setVisible(false);
                                            b.setVisible(false);
                                            n.setVisible(false);
                                            EventCenter.publish(new BoardChangeEvent(e));
                                        });
                                        b.addActionListener((e) -> {
                                            FourCases.forP(Pz,2);
                                            q.setVisible(false);
                                            r.setVisible(false);
                                            b.setVisible(false);
                                            n.setVisible(false);
                                            EventCenter.publish(new BoardChangeEvent(e));
                                        });
                                        n.addActionListener((e) -> {
                                            FourCases.forP(Pz,3);
                                            q.setVisible(false);
                                            r.setVisible(false);
                                            b.setVisible(false);
                                            n.setVisible(false);
                                            EventCenter.publish(new BoardChangeEvent(e));
                                        });

                                    }
                                    selectedPiece = null;
                                    canMovePositions.clear();

                                    for (Point2D thePoint:canBeEatenPieces(ChessColor.values()[Game.getNextPlayerIndex()])) {
                                        if(Piece.checkPieceType(thePoint.x,thePoint.y)== Piece.Type.K){
                                            Jiang = true;
                                            break;
                                        }
                                    }
                                    if(Jiang){
                                        AudioPlayer.playSound("C:/MineChessS/src/main/resources/0705.??????.wav");
                                        JOptionPane.showMessageDialog(GameStage.instance(), "Jiang Jun La!");
                                        Jiang = false;
                                    }

                                    return ActionPerformType.SUCCESS;
                                }
                            }

                            selectedPiece = null;
                            canMovePositions.clear();
                            warnPositions.clear();
                            EventCenter.publish(new BoardChangeEvent(this));
                            return ActionPerformType.FAIL;
                        }
                    }

                    @Override
                    public void undo() {
                        Game.getBoard().movePiece(x, y, spaceX, spaceY);
                        if (removedPiece != null) {
                            Game.getBoard().getGrid(x, y).setOwnedPiece(removedPiece);
                        }
                    }

                    @Override
                    public void removePending() {
                        selectedPiece = null;
                        canMovePositions.clear();
                        warnPositions.clear();
                    }

                };
            }else {
                return null;
            }
        });


        //board display
        View.setGridViewPattern(() -> new GridPanelView() {
            boolean isHighLighted = false;
            boolean hasMouseEntered = false;


            BufferedImage bufferedImage;
            GridPanelView that = this;

            @Override
            public void init() {


                this.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        super.componentResized(e);
                        if (bufferedImage == null){
                            return;
                        }
                        int w = that.getWidth() == 0 ? 60 : that.getWidth();
                        int h = that.getHeight() == 0 ? 60 : that.getHeight();
                        that.label.setIcon(new ImageIcon(bufferedImage.getScaledInstance(w, h, SCALE_SMOOTH)));
                        EventCenter.publish(new BoardChangeEvent(e));
                    }
                });
                //highlight
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        super.mouseEntered(e);
                        setBackground(new Color(2, 158, 143));
                        setOpaque(true);

                        revalidate();
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        super.mouseExited(e);
                        hasMouseEntered = false;
                        if (!isHighLighted) {
                            setOpaque(false);
                        } else {
                            setBackground(new Color(102, 192, 175));
                        }

                        revalidate();
                        repaint();
                    }
                });
            }

            //redraw pieces
            @Override
            public void redraw(BaseGrid grid) {
                boolean key = true;
                for (Point2D point : canMovePositions) {
                    if (point.x == grid.x && point.y == grid.y) {
                        key = false;
                        isHighLighted = true;
                        setBackground(new Color(102, 192, 175));
                        setOpaque(true);
                        break;
                    }
                }


                if (key) {
                    isHighLighted = false;
                    if (!hasMouseEntered) {
                        setOpaque(false);
                    }
                }

                revalidate();
                repaint();

                if (grid.hasPiece()) {
                    Piece piece = (Piece) grid.getOwnedPiece();
                    String m = piece.getName().name() + piece.getColor();
                    String location = "";
                    ImageIcon x;
                    switch (m) {
                        case "BBLACK" -> {
                            x = new ImageIcon("C:/MineChessS/src/main/resources/pieces/BBLACK.png");
                            location = x.getDescription();
                        }
                        case "BWHITE" -> {
                            x = new ImageIcon("C:/MineChessS/src/main/resources/pieces/BWHITE.png");
                            location = x.getDescription();
                        }
                        case "KBLACK" -> {
                            x = new ImageIcon("C:/MineChessS/src/main/resources/pieces/KBLACK.png");
                            location = x.getDescription();
                        }
                        case "KWHITE" -> {
                            x = new ImageIcon("C:/MineChessS/src/main/resources/pieces/KWHITE.png");
                            location = x.getDescription();
                        }
                        case "NBLACK" -> {
                            x = new ImageIcon("C:/MineChessS/src/main/resources/pieces/NBLACK.png");
                            location = x.getDescription();
                        }
                        case "NWHITE" -> {
                            x = new ImageIcon("C:/MineChessS/src/main/resources/pieces/NWHITE.png");
                            location = x.getDescription();
                        }
                        case "PBLACK" -> {
                            x = new ImageIcon("C:/MineChessS/src/main/resources/pieces/PBLACK.png");
                            location = x.getDescription();
                        }
                        case "PWHITE" -> {
                            x = new ImageIcon("C:/MineChessS/src/main/resources/pieces/PWHITE.png");
                            location = x.getDescription();
                        }
                        case "QBLACK" -> {
                            x = new ImageIcon("C:/MineChessS/src/main/resources/pieces/QBLACK.png");
                            location = x.getDescription();
                        }
                        case "QWHITE" -> {
                            x = new ImageIcon("C:/MineChessS/src/main/resources/pieces/QWHITE.png");
                            location = x.getDescription();
                        }
                        case "RBLACK" -> {
                            x = new ImageIcon("C:/MineChessS/src/main/resources/pieces/RBLACK.png");
                            location = x.getDescription();
                        }
                        case "RWHITE" -> {
                            x = new ImageIcon("C:/MineChessS/src/main/resources/pieces/RWHITE.png");
                            location = x.getDescription();
                        }
                        default -> {
                            x = new ImageIcon();
                        }
                    }
                    try {
                        bufferedImage = ImageIO.read(new File(String.format("%s", location)));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    int w = that.getWidth() == 0 ? 60 : that.getWidth();
                    int h = that.getHeight() == 0 ? 60 : that.getHeight();
                    that.label.setIcon(new ImageIcon(bufferedImage.getScaledInstance(w, h, SCALE_SMOOTH)));


//                    this.label.setIcon(x);
                } else {
                    this.label.setIcon(new ImageIcon());
                }
            }
            //redraw pieces end

        });


        //win judge
        Game.setPlayerWinningJudge((player -> LastEatenType == Piece.Type.K
                && Game.getCurrentPlayerIndex() == player.getId()));

        //withdraw judge
        Game.setGameEndingJudge(() -> {
            if (PlayerManager.isOnePlayerRemains()){
                return true;
            }
            if (canMove(ChessColor.values()[Game.getNextPlayerIndex()]) != null
                    && canMove(ChessColor.values()[Game.getNextPlayerIndex()]).size()!=0){
                return false;
            }
            return true;
        });

        //message
        View.setPlayerWinView((player ->
                JOptionPane.showMessageDialog(GameStage.instance(), "Congratulations! "+player.getName() + " Win!\n"+"  --Game end--")));

        View.setGameEndView(withdraw -> {
            if (withdraw) {
                JOptionPane.showMessageDialog(GameStage.instance(), "The Game Withdraw!");
            }
        });

        //AI Player
        AIPlayer.addAIType("AI-Easy", (id) -> {
            return new AIPlayer(id, "AI-Easy", 777) {
                protected boolean calculateNextMove() {
                    Random random = new Random();
                    while(true){
                        int x = Math.abs(random.nextInt()) % Game.getWidth();
                        int y = Math.abs(random.nextInt()) % Game.getHeight();
                        if (this.performGridAction(x, y, 1)) {
                            if(canMovePositions.size()>0) {
                                int n = random.nextInt(0, canMovePositions.size());
                                if (this.performGridAction(canMovePositions.get(n).x, canMovePositions.get(n).y, 1)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            };
        });
        AIPlayer.addAIType("AI-Normal", (id) -> {
            return new AIPlayer(id, "AI-Normal", 666) {
                protected boolean calculateNextMove() {
                    ArrayList<Point2D> canEat = canBeEatenPieces(ChessColor.values()[Game.getNextPlayerIndex()]);
                    ArrayList<Point2D> pointCross = new ArrayList<>();
                    ArrayList<Point2D> allCanMovePositions = canMove(ChessColor.values()[Game.getCurrentPlayerIndex()]);
                    ArrayList<Piece> myPieces = allPieces(ChessColor.values()[Game.getCurrentPlayerIndex()]);

                        for(int i=0;i<allCanMovePositions.size();i++){
                            for(int j=0;j<canEat.size();j++){
                                if(allCanMovePositions.get(i).x == canEat.get(j).x && allCanMovePositions.get(i).y == canEat.get(j).y){
                                    pointCross.add(allCanMovePositions.get(i));
                                }
                            }
                        }

                        canEat = new ArrayList<>();
                        Random random = new Random();
                    int betterX = 0;
                    int betterY = 0;

                        if(pointCross.size()!=0){

                            ArrayList<Piece> betterPiece = new ArrayList<>();
                            for (Piece piece : myPieces) {
                                for (Point2D point : pointCross) {
                                    for (Point2D pointTo : piece.canMoveTo()) {
                                        if (pointTo.x == point.x && pointTo.y == point.y) {
                                            betterPiece.add(piece);
                                            betterX = point.x;
                                            betterY = point.y;
                                        }
                                    }
                                }
                            }

                            if(betterPiece.size()!=0){
                                this.performGridAction(betterPiece.get(0).getX(), betterPiece.get(0).getY(), 1);
                                this.performGridAction(betterX, betterY, 1);
                                return true;
                            }


//                            for(int k=0;k<pointCross.size();k++) {
//                                for (int l = 0; l < myPieces.size(); l++) {
//                                    if (this.performGridAction(myPieces.get(l).getX(), myPieces.get(l).getY(), 1)) {
//                                        if (canMovePositions.contains(pointCross.get(k))) {
//                                            if (this.performGridAction(pointCross.get(k).x, pointCross.get(k).y, 1)) {
//                                                return true;
//                                            }
//                                        } else {
//                                            this.performGridAction(pointCross.get(k).x, pointCross.get(k).y, 1);
//                                        }
//                                    }
//                                }
//                            }
//                            return false;

                        }else{
                            while(true){
                                int x = Math.abs(random.nextInt()) % Game.getWidth();
                                int y = Math.abs(random.nextInt()) % Game.getHeight();
                                if (this.performGridAction(x, y, 1)) {
                                    if(canMovePositions.size()>0) {
                                        int n = random.nextInt(0, canMovePositions.size());
                                        if (this.performGridAction(canMovePositions.get(n).x, canMovePositions.get(n).y, 1)) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    return false;
                }
            };
        });

        View.start();

        View.addStage("SettingStage", Settings.instance());

        for(int i=0;i<Game.saver.getSlotNumber();i++) {
            LoadStage.instance().saveButtons[i].setBackground(new Color(169, 183, 198));
            LoadStage.instance().saveButtons[i].setForeground(new Color(60, 63, 65));
        }

        JLabel currentPlayerLabel2 = new JLabel();
        EventCenter.subscribe(BoardChangeEvent.class, e ->
                currentPlayerLabel2.setText((ChessColor.values()[Game.getCurrentPlayerIndex()].name())+"'s turn"));
        currentPlayerLabel2.setFont(new Font("INK Free",Font.BOLD,25));

        //the dream begins
    }
}
