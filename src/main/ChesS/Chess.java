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
import frame.view.sound.AudioPlayer;
import frame.view.stage.GameStage;
import frame.view.stage.MenuStage;
import frame.view.stage.RoomStage;
import frame.view.stage.LoadStage;
import frame.view.stage.RankingStage;

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
    public static ArrayList<Point2D> canMovePositions = new ArrayList<>();

    public static Piece.Type LastEatenType;
    public static Piece LastEatenPiece;

    public Chess() throws IOException {}

    private static void initialize(){
        View.setName("\n");
        View.window.setSize(950, 600);
        Game.setBoardSize(8, 8);
        Game.saver.checkSize(true);
        Game.saver.setSlotNumber(5);
        Game.setMaximumPlayer(2);
    }

    //reset component
    public static void clear(){
        isSelecting = false;
        selectedPiece = null;
        canMovePositions = new ArrayList<>();
        LastEatenType = null;
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
                break;
        }
        return result;
    }

    public static void main(String[] args) {

        //GUI Begin
        initialize();

        MenuStage.instance().rank.setText("     Rank    ");
        MenuStage.instance().rank.setFont(new Font("INK Free",Font.PLAIN,20));
        MenuStage.instance().rank.setBackground(new java.awt.Color(60, 63, 65));
        MenuStage.instance().newGame.setText("     New     ");
        MenuStage.instance().newGame.setFont(new Font("INK Free",Font.PLAIN,22));
        MenuStage.instance().newGame.setBackground(new java.awt.Color(60, 63, 65));
        MenuStage.instance().load.setText("     Load     ");
        MenuStage.instance().load.setFont(new Font("INK Free",Font.PLAIN,20));
        MenuStage.instance().quit.setText("     Quit     ");
        MenuStage.instance().quit.setFont(new Font("INK Free",Font.PLAIN,20));
        MenuStage.instance().load.setBackground(new java.awt.Color(60, 63, 65));
        MenuStage.instance().quit.setBackground(new java.awt.Color(60, 63, 65));

        MenuStage.instance().quit.setForeground(new java.awt.Color(169, 183, 198));
        MenuStage.instance().load.setForeground(new java.awt.Color(169, 183, 198));
        MenuStage.instance().newGame.setForeground(new java.awt.Color(169, 183, 198));
        MenuStage.instance().rank.setForeground(new java.awt.Color(169, 183, 198));

        RoomStage.instance().textHeight.setVisible(false);
        RoomStage.instance().textWidth.setVisible(false);

        LoadStage.instance().title.setText("{ LOAD }");
        LoadStage.instance().title.setFont(new Font("INK Free",Font.BOLD,70));
        LoadStage.instance().title.setForeground(new java.awt.Color(251, 251, 251));
        LoadStage.instance().fileChooserButton.setText("Select Files");
        LoadStage.instance().fileChooserButton.setBackground(new java.awt.Color(169, 183, 198));
        LoadStage.instance().fileChooserButton.setForeground(new java.awt.Color(60, 63, 65));
        LoadStage.instance().back.setText("Back  Menu");
        LoadStage.instance().back.setBackground(new java.awt.Color(60, 63, 65));
        LoadStage.instance().back.setForeground(new java.awt.Color(169, 183, 198));

        RoomStage.instance().back.setText("Menu ");
        RoomStage.instance().back.setFont(new Font("INK Free",Font.PLAIN,20));
        RoomStage.instance().back.setBackground(new java.awt.Color(169, 183, 198));
        RoomStage.instance().back.setForeground(new java.awt.Color(60, 63, 65));
        RoomStage.instance().start.setFont(new Font("INK Free",Font.PLAIN,20));
        RoomStage.instance().start.setBackground(new java.awt.Color(169, 183, 198));
        RoomStage.instance().start.setForeground(new java.awt.Color(60, 63, 65));

        RankingStage.instance().title.setText("{ RANK }");
        RankingStage.instance().title.setFont(new Font("INK Free",Font.BOLD,70));
        RankingStage.instance().title.setForeground(new java.awt.Color(169, 183, 198));
        RankingStage.instance().back.setBackground(new java.awt.Color(169, 183, 198));
        RankingStage.instance().back.setForeground(new java.awt.Color(60, 63, 65));

        RankingStage.instance().back.setForeground(new java.awt.Color(169, 183, 198));
        RankingStage.instance().back.setBackground(new java.awt.Color(60, 63, 65));
        RankingStage.instance().back.setForeground(new java.awt.Color(169, 183, 198));
        RankingStage.instance().back.setForeground(new java.awt.Color(60, 63, 65));
        RankingStage.instance().back.setBackground(new java.awt.Color(169, 183, 198));


        GameStage.instance().menuButton.setBackground(new java.awt.Color(248, 248, 248));
        GameStage.instance().undoButton.setBackground(new java.awt.Color(248, 248, 248));
        GameStage.instance().saveButton.setBackground(new java.awt.Color(248, 248, 248));

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

        JButton resetGame = new JButton("Reset Game");
        resetGame.setBackground(new java.awt.Color(248, 248, 248));;
        resetGame.addActionListener((e) -> {
            Game.init();
            clear();
        });

        JLabel currentPlayerLabel = new JLabel();
        EventCenter.subscribe(BoardChangeEvent.class, e -> currentPlayerLabel.setText((ChessColor.values()[Game.getCurrentPlayerIndex()].name())+"'s turn"));
        currentPlayerLabel.setFont(new Font("INK Free",Font.BOLD,25));

        JButton loadButton = new JButton("Load Game");
        loadButton.setBackground(new java.awt.Color(248, 248, 248));
        loadButton.addActionListener((e) -> {
            View.changeStage("MenuStage");
            View.changeStage("LoadStage");
        });

        MenuStage.instance().quit.addActionListener((e) -> {
            System.exit(0);
        });

        JButton MusicOn = new JButton("Music On");
        MusicOn.setForeground(new java.awt.Color(169, 183, 198));
        MusicOn.setBackground(new java.awt.Color(60, 63, 65));
        MusicOn.addActionListener((e) -> {
            bgm.start();
        });

        JButton MusicOff = new JButton("Music Off");
        MusicOff.setForeground(new java.awt.Color(169, 183, 198));
        MusicOff.setBackground(new java.awt.Color(60, 63, 65));
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
                            if (!selectedGrid.hasPiece())
                                return ActionPerformType.FAIL;
                            Piece piece = (Piece) selectedGrid.getOwnedPiece();
                            if (piece.getColor() != ChessColor.values()[Game.getCurrentPlayerIndex()]) {
                                return ActionPerformType.FAIL;
                            }
                            canMovePositions = piece.canMoveTo();
                            selectedPiece = piece; // 全局变量存被选中的棋子
                            isSelecting = true;
                            AudioPlayer.playSound("src/main/resources/8位视频游戏声音 _ 硬币1 - Freesound.wav");
                            return ActionPerformType.PENDING; // 执行结果为PENDING，玩家这一步对棋盘没有更改，需要之后的Action
                        } else {
                            isSelecting = false;
                            for (Point2D point : canMovePositions) {
                                if (point.x == x && point.y == y) {
                                    this.removedPiece = (Piece) Game.getBoard().movePiece(selectedPiece.getX(), selectedPiece.getY(), x, y);
                                    if (this.removedPiece != null) {
                                        LastEatenType = this.removedPiece.getName();
                                    }
                                    selectedPiece = null;
                                    canMovePositions.clear();
                                    AudioPlayer.playSound("src/main/resources/pman - Freesound.wav");
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
                        setBackground(new java.awt.Color(2, 158, 143));
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
                            setBackground(new java.awt.Color(102, 192, 175));
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
                        setBackground(new java.awt.Color(102, 192, 175));
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
            if(canMove(ChessColor.values()[Game.getNextPlayerIndex()]) != null){
                return false;
            }
            return true;
        });

        View.setPlayerWinView((player -> JOptionPane.showMessageDialog(GameStage.instance(), "Congratulations!"+player.getName() + " Win!")));

        View.setGameEndView(withdraw -> {
            if (withdraw) {
                JOptionPane.showMessageDialog(GameStage.instance(), "The Game Withdraw!");
            }
        });

        //AI Player
        AIPlayer.addAIType("Easy", (id) -> {
            return new AIPlayer(id, "Easy", 200) {
                protected boolean calculateNextMove() {
                    Random random = new Random();

                    for(int i = 0; i < 100; ++i) {
                        int x = Math.abs(random.nextInt()) % Game.getWidth();
                        int y = Math.abs(random.nextInt()) % Game.getHeight();
                        if (this.performGridAction(x, y, 1)) {
                            return true;
                        }
                    }

                    return false;
                }
            };
        });
        AIPlayer.addAIType("Normal", (id) -> {
            return new AIPlayer(id, "Normal", 200) {
                protected boolean calculateNextMove() {
                    this.surrender();
                    return true;
                }
            };
        });


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

        for(int i=0;i<Game.saver.getSlotNumber();i++) {
            LoadStage.instance().saveButtons[i].setBackground(new java.awt.Color(169, 183, 198));
            LoadStage.instance().saveButtons[i].setForeground(new java.awt.Color(60, 63, 65));
        }
        //the dream begins
    }
}
