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
import java.awt.*;
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
    public static ArrayList<Point2D> allCanMovePositions = new ArrayList<>();

    public static Piece.Type LastEatenType;
    public static Piece LastEatenPiece;

    public Chess() throws IOException {}

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
        allCanMovePositions = new ArrayList<>();


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
//        MenuStage.instance().settings.setVisible(true);
        MenuStage.instance().settings.setText("     Settings    ");
        MenuStage.instance().settings.setFont(new Font("INK Free",Font.PLAIN,20));
        MenuStage.instance().settings.setBackground(new java.awt.Color(60, 63, 65));

        MenuStage.instance().quit.setForeground(new Color(169, 183, 198));
        MenuStage.instance().load.setForeground(new Color(169, 183, 198));
        MenuStage.instance().newGame.setForeground(new Color(169, 183, 198));
        MenuStage.instance().rank.setForeground(new Color(169, 183, 198));
        MenuStage.instance().settings.setForeground(new java.awt.Color(169, 183, 198));

        Settings.instance().setRoomBG.setForeground(new Color(169, 183, 198));
        Settings.instance().setLoadBG.setForeground(new Color(169, 183, 198));
        Settings.instance().setPiecePicture.setForeground(new Color(169, 183, 198));
        Settings.instance().setMenuBG.setForeground(new Color(169, 183, 198));
        Settings.instance().setBGM.setForeground(new Color(169, 183, 198));
        Settings.instance().setBoardBG.setForeground(new Color(169, 183, 198));

        RoomStage.instance().textHeight.setVisible(false);
        RoomStage.instance().textWidth.setVisible(false);

        LoadStage.instance().title.setText("{ LOAD }");
        LoadStage.instance().title.setFont(new Font("INK Free",Font.BOLD,70));
        LoadStage.instance().title.setForeground(new Color(251, 251, 251));
        LoadStage.instance().fileChooserButton.setText("Select Files");
        LoadStage.instance().fileChooserButton.setBackground(new Color(169, 183, 198));
        LoadStage.instance().fileChooserButton.setForeground(new Color(60, 63, 65));
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
        RankingStage.instance().title.setForeground(new Color(169, 183, 198));
        RankingStage.instance().back.setBackground(new Color(169, 183, 198));
        RankingStage.instance().back.setForeground(new Color(60, 63, 65));

        RankingStage.instance().back.setForeground(new Color(169, 183, 198));
        RankingStage.instance().back.setBackground(new Color(60, 63, 65));
        RankingStage.instance().back.setForeground(new Color(169, 183, 198));
        RankingStage.instance().back.setForeground(new Color(60, 63, 65));
        RankingStage.instance().back.setBackground(new Color(169, 183, 198));


        GameStage.instance().menuButton.setBackground(new Color(248, 248, 248));
        GameStage.instance().undoButton.setBackground(new Color(248, 248, 248));
        GameStage.instance().saveButton.setBackground(new Color(248, 248, 248));

        //set the bgm
        MusicPlayer bgm = new MusicPlayer("src/main/resources/eS=S - 8bit Faith.mp3");
        bgm.start();

        //change the background
        try {
            Image chessBackground = ImageIO.read(new File("src/main/resources/Snipaste_2022-05-22_14-17-21.png"));
            Image menuBackground = ImageIO.read(new File("src/main/resources/menu.png"));
            Image roomBackground = ImageIO.read(new File("src/main/resources/room.png"));
            Image loadBackground = ImageIO.read(new File("src/main/resources/game.png"));
            View.setBoardViewPattern(() -> new BoardView(chessBackground) {});
            MenuStage.instance().setBackgroundImage(menuBackground);
            RoomStage.instance().setBackgroundImage(roomBackground);
            LoadStage.instance().setBackgroundImage(loadBackground);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //register board
        Game.registerBoard(Board.class);
        Settings.instance().setVisible(true);
        View.addStage("Settings", Settings.instance());

        JButton resetGame = new JButton("Reset Game");
        resetGame.setBackground(new java.awt.Color(248, 248, 248));;
        resetGame.addActionListener((e) -> {
            Game.init();
            clear();
        });

        JButton settings = new JButton("Settings");
        settings.addActionListener((e) -> {
            View.changeStage("Settings");
        });

        BackgroundImagePanel leftPanel = new BackgroundImagePanel();
        BackgroundImagePanel rightPanel = new BackgroundImagePanel();

//        rightPanel.add(settings);

        JButton q = new JButton("level up to Queen");
        JButton r = new JButton("level up to Rook");
        JButton b = new JButton("level up to Bishop");
        JButton n = new JButton("level up to Knight");
        q.addActionListener((e) -> {
            fourCases.forP(Pz,0);
            q.setVisible(false);
            r.setVisible(false);
            b.setVisible(false);
            n.setVisible(false);
        });
        r.addActionListener((e) -> {
            fourCases.forP(Pz,1);
            q.setVisible(false);
            r.setVisible(false);
            b.setVisible(false);
            n.setVisible(false);
        });
        b.addActionListener((e) -> {
            fourCases.forP(Pz,2);
            q.setVisible(false);
            r.setVisible(false);
            b.setVisible(false);
            n.setVisible(false);
        });
        n.addActionListener((e) -> {
            fourCases.forP(Pz,3);
            q.setVisible(false);
            r.setVisible(false);
            b.setVisible(false);
            n.setVisible(false);
        });

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

                int copyLastX = selectedX;
                int copyLastY = selectedY;

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
                                    AudioPlayer.playSound("src/main/resources/8位视频游戏声音 _ 硬币1 - Freesound.wav");
                                    canMovePositions = piece.canMoveTo();
                                    selectedPiece = piece;
                                    isSelecting = true;
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
                                    AudioPlayer.playSound("src/main/resources/pman - Freesound.wav");
                                    //bottom change
                                    if((selectedPiece.getName() == Piece.Type.P && selectedPiece.getY() == 7) || (selectedPiece.getName() == Piece.Type.P && selectedPiece.getY() == 0)){
                                        Pz = selectedPiece;
                                        q.setVisible(true);
                                        r.setVisible(true);
                                        b.setVisible(true);
                                        n.setVisible(true);
                                    }
                                    selectedPiece = null;
                                    canMovePositions.clear();



                                    return ActionPerformType.SUCCESS;
                                }
                            }

                            selectedPiece = null;
                            canMovePositions.clear();
                            EventCenter.publish(new BoardChangeEvent(this));
                            return ActionPerformType.FAIL;
                        }
                    }

                    @Override
                    public void undo() {
                        Game.getBoard().movePiece(x, y, copyLastX, copyLastY);
                        if (removedPiece != null) {
                            Game.getBoard().getGrid(x, y).setOwnedPiece(removedPiece);
                        }
                    }

                    @Override
                    public void removePending() {
                        selectedPiece = null;
                        canMovePositions.clear();
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

            @Override
            public void init() {
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
                boolean flag = true;
                for (Point2D point : canMovePositions) {
                    if (point.x == grid.x && point.y == grid.y) {
                        flag = false;
                        isHighLighted = true;
                        setBackground(new Color(102, 192, 175));
                        setOpaque(true);
                        break;
                    }
                }

                if (flag) {
                    isHighLighted = false;
                    if (!hasMouseEntered) {
                        setOpaque(false);
                    }
                }

                revalidate();
                repaint();

                if (grid.hasPiece()) {
                    Piece piece = (Piece) grid.getOwnedPiece();
                    BufferedImage bufferedImage;
                    String m = piece.getName().name() + piece.getColor();
                    String location = "";
                    ImageIcon x;
                    switch (m) {
                        case "BBLACK" -> {
                            x = new ImageIcon("src/main/resources/pieces/BBLACK.png");
                            location = x.getDescription();
                        }
                        case "BWHITE" -> {
                            x = new ImageIcon("src/main/resources/pieces/BWHITE.png");
                            location = x.getDescription();
                        }
                        case "KBLACK" -> {
                            x = new ImageIcon("src/main/resources/pieces/KBLACK.png");
                            location = x.getDescription();
                        }
                        case "KWHITE" -> {
                            x = new ImageIcon("src/main/resources/pieces/KWHITE.png");
                            location = x.getDescription();
                        }
                        case "NBLACK" -> {
                            x = new ImageIcon("src/main/resources/pieces/NBLACK.png");
                            location = x.getDescription();
                        }
                        case "NWHITE" -> {
                            x = new ImageIcon("src/main/resources/pieces/NWHITE.png");
                            location = x.getDescription();
                        }
                        case "PBLACK" -> {
                            x = new ImageIcon("src/main/resources/pieces/PBLACK.png");
                            location = x.getDescription();
                        }
                        case "PWHITE" -> {
                            x = new ImageIcon("src/main/resources/pieces/PWHITE.png");
                            location = x.getDescription();
                        }
                        case "QBLACK" -> {
                            x = new ImageIcon("src/main/resources/pieces/QBLACK.png");
                            location = x.getDescription();
                        }
                        case "QWHITE" -> {
                            x = new ImageIcon("src/main/resources/pieces/QWHITE.png");
                            location = x.getDescription();
                        }
                        case "RBLACK" -> {
                            x = new ImageIcon("src/main/resources/pieces/RBLACK.png");
                            location = x.getDescription();
                        }
                        case "RWHITE" -> {
                            x = new ImageIcon("src/main/resources/pieces/RWHITE.png");
                            location = x.getDescription();
                        }
                    }
                    try {
                        bufferedImage = ImageIO.read(new File(String.format("%s",location)));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
//                    bufferedImage = bufferedImage.getSubimage(12,12,65,65);
//                    bufferedImage.getScaledInstance(6,6,SCALE_SMOOTH);
                    this.label.setIcon(new ImageIcon(bufferedImage.getScaledInstance(60,60,SCALE_SMOOTH)));
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
                JOptionPane.showMessageDialog(GameStage.instance(), "Congratulations! "+player.getName() + " Win!\n"+"Game end!")));

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
                    allCanMovePositions = canMove(ChessColor.values()[Game.getCurrentPlayerIndex()]);
                    ArrayList<Piece> myPieces = allPieces(ChessColor.values()[Game.getCurrentPlayerIndex()]);
                    while(true){
                        for(int i=0;i<allCanMovePositions.size();i++){
                            for(int j=0;j<canEat.size();j++){
                                if(allCanMovePositions.get(i).x == canEat.get(j).x && allCanMovePositions.get(i).y == canEat.get(j).y){
                                    pointCross.add(allCanMovePositions.get(i));
                                }
                            }
                        }
                        canEat = new ArrayList<>();
                        Random random = new Random();
                        if(pointCross.size()!=0){
                            for(int k=0;k<pointCross.size();k++) {
                                for (int l = 0; l < myPieces.size(); l++) {
                                    if (this.performGridAction(myPieces.get(l).getX(), myPieces.get(l).getY(), 1)) {
                                        if (canMovePositions.contains(pointCross.get(k))) {
                                            if (this.performGridAction(pointCross.get(k).x, pointCross.get(k).y, 1)) {
                                                allCanMovePositions = new ArrayList<>();
                                                pointCross = new ArrayList<>();
                                                return true;
                                            }
                                        } else {
                                            this.performGridAction(pointCross.get(k).x, pointCross.get(k).y, 1);
                                        }
                                    }
                                }
                            }
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

                    }
                }
            };
        });

//dixianshengbian
//        BackgroundImagePanel sidePanel = new BackgroundImagePanel();
//        JButton someButton = new JButton("Promotion");
//        someButton.addActionListener((e) -> { // 手动写一个按钮·1，按下时调用Game.performAction，然后继承一个Action传进去
//            Game.performAction(new Action(true) {
//                Piece changedPiece = null; // 记录被升变的棋子
//                @Override
//                public ActionPerformType perform() {
//                    if (!isSelecting) return ActionPerformType.FAIL; // 没选中或不是兵返回FAIL
//                    if (selectedPiece.getName() != Piece.Type.P) {
//                        selectedPiece = null; // 清理全局变量
//                        availablePositions.clear();
//                        return ActionPerformType.FAIL;
//                    }
//                    changedPiece = selectedPiece; // 记录改变的棋子，方便撤回
//                    selectedPiece.setName(Piece.Type.N); // 改变type
//                    selectedPiece = null; // 清理全局变量
//                    availablePositions.clear();
//                    return ActionPerformType.SUCCESS;
//                }
//
//                @Override
//                public void undo() {
//                    changedPiece.setName(Piece.Type.P); // 把记下来的棋子改回兵
//                }
//            });
//        });
//        sidePanel.add(someButton);
//        GameStage.instance().add("East", sidePanel); // GameStage的布局管理器是BorderPanel，可以在东西南北添加Panel。框架在南北提供了两个，这里是在东边添加。

        View.start();

        View.addStage("Settings", Settings.instance());

        for(int i=0;i<Game.saver.getSlotNumber();i++) {
            LoadStage.instance().saveButtons[i].setBackground(new Color(169, 183, 198));
            LoadStage.instance().saveButtons[i].setForeground(new Color(60, 63, 65));
        }
        //the dream begins
    }
}
