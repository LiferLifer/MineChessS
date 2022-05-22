import frame.Controller.Game;
import frame.action.Action;
import frame.action.ActionPerformType;
import frame.board.BaseGrid;
import frame.event.BoardChangeEvent;
import frame.event.EventCenter;
import frame.player.PlayerManager;
import frame.util.Point2D;
import frame.view.View;
import frame.view.board.BoardView;
import frame.view.board.GridPanelView;
import frame.view.sound.AudioPlayer;
import frame.view.stage.GameStage;
import frame.view.stage.MenuStage;
import frame.view.stage.RoomStage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Chess {

    public static boolean isSelecting = false;
    public static Piece selectedPiece = null;
    public static ArrayList<Point2D> canMovePositions = new ArrayList<>();
    public static Piece.Type lastRemovedType;

    public Chess() throws IOException {}

    public static void main(String[] args) {
        View.window.setSize(850, 600);
        Game.setMaximumPlayer(2);
//        View.setName("Let's play the chesS");
        View.setName("\n");
        Game.setBoardSize(8, 8);
        Game.saver.checkSize(true); // 读档时检查存档棋盘大小
        Game.saver.setSlotNumber(5); // 存档数量

        MusicPlayer bgm = new MusicPlayer("src/main/resources/坂本龍一 - Merry Christmas Mr. Lawrence.mp3");
//        bgm.start();
//        bgm.stop();

//        MenuStage.instance().setBgm("src/main/resources/bgm1.mp3");
//        AudioPlayer.playBgm("src/main/resources/bgm1.mp3");
//        GameStage.instance().setBgm("src/main/resources/坂本龍一 - Merry Christmas Mr. Lawrence.mp3");

        //register board
        Game.registerBoard(Board.class);

        Game.registerGridAction((x, y) -> true, (x, y, mouseButton) -> {
            if (mouseButton == 1) {
                int selectedX = 0, selectedY = 0;
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
                        if (piece.getColor() != Color.values()[Game.getCurrentPlayerIndex()]) {
                            return ActionPerformType.FAIL;
                        }
                        canMovePositions = piece.canMoveTo();
                        selectedPiece = piece; // 全局变量存被选中的棋子
                        isSelecting = true;
                        AudioPlayer.playSound("src/main/resources/Click on the sound effects.wav");
                        return ActionPerformType.PENDING; // 执行结果为PENDING，玩家这一步对棋盘没有更改，需要之后的Action
                        // 撤销或者FAIL时会把之前所有的PENDING都撤掉，详见文档
                    } else {
                        isSelecting = false;
                        for (Point2D point : canMovePositions) { // 判断点击的格子是否能走
                            if (point.x == x && point.y == y) {
                                // 获取被吃掉的棋子，存到Action对象里
                                this.removedPiece = (Piece) Game.getBoard().movePiece(selectedPiece.getX(), selectedPiece.getY(), x, y);
                                if (this.removedPiece != null) {
                                    // 如果吃了子，记录最近一个被吃的子的类型（判断被吃的是不是将或者帅）
                                    lastRemovedType = this.removedPiece.getName();
                                }
                                selectedPiece = null;
                                canMovePositions.clear();
                                AudioPlayer.playSound("src/main/resources/martial arts striking.wav");
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
                        if (removedPiece != null) { // 如果这个Action吃了子，把被吃的子放回去
                            Game.getBoard().getGrid(x, y).setOwnedPiece(removedPiece);
                        }
                    }

                    @Override
                    public void removePending() {
                        // 撤销返回PENDING的Action的时候会调用。
                        // 比如说，刚才高亮的时候记录了全局变量。
                        // 如果是在选中时撤销，由于撤销PENDING的Action不会调用undo，所以需要在这里清理全局变量。
                        selectedPiece = null;
                        canMovePositions.clear();
                    }

                };
            }
            return null;
        });


//        BackgroundImagePanel sidePanel = new BackgroundImagePanel();
//        JButton someButton = new JButton("Promotion");
//        someButton.addActionListener((e) -> { // 手动写一个按钮，按下时调用Game.performAction，然后继承一个Action传进去
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


        // 胜利条件：刚才被吃的是将/帅，则吃子的玩家赢
        Game.setPlayerWinningJudge((player -> {
            return lastRemovedType == Piece.Type.K
                    && Game.getCurrentPlayerIndex() == player.getId();
        }));

        // 判断游戏结束条件。默认条件是任意一方胜利，但由于和棋规则，这里多判断了当前玩家无棋可走。
        // 判断方式很暴力，遍历了棋盘，找到下一名玩家的所有棋子，判断棋子是不是全都动不了。
        // 这里用的是getNextPlayer，因为游戏结束是在当前玩家回合结束，还没进入下一名玩家的回合时判断。
        Game.setGameEndingJudge(() -> {
            if (PlayerManager.isOnePlayerRemains()) return true; // 先判断是不是有人赢了或者投降
            for (int i = 0; i < Game.getWidth(); i++) { // 遍历棋盘
                for (int j = 0; j < Game.getHeight(); j++) {
                    Grid grid = (Grid) Game.getBoard().getGrid(i, j);
                    if (grid.hasPiece()) {
                        Piece piece = (Piece) grid.getOwnedPiece(); // 如果格子上有子，并且和当前玩家颜色不一样：
                        if (piece.getColor() == Color.values()[Game.getNextPlayerIndex()]) {
                            if (!piece.canMoveTo().isEmpty()) { // 判断是不是能走。如果能走则返回false，不平局。
                                return false;
                            }
                        }
                    }
                }
            }
            // 如果都不能走则平局。
            return true;
        });

        //change the background
        try {
            Image chessBackground = ImageIO.read(new File("src/main/resources/Snipaste_2022-05-22_14-17-21.png"));
            Image menuBackground = ImageIO.read(new File("src/main/resources/menu.png"));
            Image roomBackground = ImageIO.read(new File("src/main/resources/room.png"));
//            Image gameBackground = ImageIO.read(new File("src/main/resources/game.png"));
            View.setBoardViewPattern(() -> new BoardView(chessBackground) {});
            MenuStage.instance().setBackgroundImage(menuBackground);
            RoomStage.instance().setBackgroundImage(roomBackground);
//            GameStage.instance().setBackgroundImage(gameBackground);
        } catch (IOException e) {
            e.printStackTrace();
        }

        View.setGridViewPattern(() -> new GridPanelView() {
            boolean isHighLighted = false, hasMouseEntered = false;

            @Override
            public void init() {
                // 这里是鼠标移动到格子上时高亮
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
                if (flag) { // 格子不在可以走的格子里面
                    isHighLighted = false;
                    if (!hasMouseEntered) {
                        setOpaque(false);
                    }
                }
                revalidate();
                repaint();
                if (grid.hasPiece()) {
                    Piece piece = (Piece) grid.getOwnedPiece();
//                    this.label.setText(piece.getName().name());
                    BufferedImage bufferedImage;
                    String m = piece.getName().name() + piece.getColor();
                    String location = "";
                    ImageIcon x = new ImageIcon();
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
                    bufferedImage = bufferedImage.getSubimage(50,34,50,70);
//                    bufferedImage.getScaledInstance(10,10,100);
                    this.label.setIcon(new ImageIcon(bufferedImage));
                    if (piece.getColor() == Color.WHITE)
                        this.label.setForeground(java.awt.Color.WHITE);
                    else
                        this.label.setForeground(java.awt.Color.BLACK);
                } else {
                    this.label.setIcon(new ImageIcon());
                }
            }
        });

        View.setPlayerWinView((player -> JOptionPane.showMessageDialog(GameStage.instance(), player.getName() + " Win!")));
        View.setPlayerLoseView((player -> JOptionPane.showMessageDialog(GameStage.instance(), player.getName() + " Surrender!")));
        // 设置游戏结束的信息。
        // 由于玩家胜利已经会弹窗了，所以要判断一下是不是平局。
        View.setGameEndView(withdraw -> {
            if (withdraw) {
                JOptionPane.showMessageDialog(GameStage.instance(), "Withdraw!");
            }
        });

        // 在GameStage下面的JPanel显示当前玩家。
        JLabel currentPlayerLabel = new JLabel();
        // 监听BoardChangeEvent。第二个传入的lambda每次接受到BoardChangeEvent都会执行里面的内容。
        EventCenter.subscribe(BoardChangeEvent.class, e -> currentPlayerLabel.setText("Now: " + Color.values()[Game.getCurrentPlayerIndex()].name()));

        // 重置，框架的部分调用Game.init()就行。不过还要重置全局变量。
        JButton reset = new JButton("Reset");
        reset.addActionListener((e) -> {
            isSelecting = false;
            selectedPiece = null;
            canMovePositions = new ArrayList<>();
            lastRemovedType = null;
            Game.init();
        });

        // 演示一下Stage文档里提到的自行添加组件
        GameStage.instance().setCustomDrawMethod(() -> {
            GameStage stage = GameStage.instance();
            stage.menuBar.add(reset);
            stage.menuBar.add(stage.menuButton);
            stage.menuBar.add(stage.saveButton);
            stage.menuBar.add(stage.undoButton);
            stage.menuBar.add(stage.surrenderButton);
            stage.scoreBoard.add(currentPlayerLabel);
            stage.add("North", stage.menuBar);
            stage.add("South", stage.scoreBoard);
        });

        View.start();
    }
}
