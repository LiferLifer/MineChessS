import frame.board.BaseGrid;
import frame.event.BoardChangeEvent;
import frame.event.EventCenter;
import frame.util.Point2D;
import frame.view.View;
import frame.view.board.BoardView;
import frame.view.board.GridPanelView;
import frame.view.components.BackgroundImagePanel;
import frame.view.stage.BaseStage;
import frame.view.stage.GameStage;
import frame.view.stage.MenuStage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.awt.Image.SCALE_SMOOTH;

public class Settings extends BaseStage {
    private static volatile Settings sInstance = null;
    public JLabel title = new JLabel("Settings");
    public JButton setMenuBG = new JButton("Change Menu Background");
    public JButton setRoomBG = new JButton("Change Room Background");
    public JButton setLoadBG = new JButton("Change Load Background");
    public JButton setGameBG = new JButton("Change Game Background");
    public JButton setPiecePicture = new JButton("Change Piece Picture");
    public JButton setBoardBG = new JButton("Change Board Picture");
    public JButton back = new JButton("Back");
    public Box buttonPanel = new Box(1);
    public BackgroundImagePanel dummyPanel = new BackgroundImagePanel();

    private Settings() {
        super("SettingStage");
        this.setLayout(new BorderLayout());
        this.title.setFont(new Font("Arial", Font.PLAIN, 50));
        this.title.setHorizontalAlignment(0);
        this.setMenuBG.setVisible(true);
        this.setPiecePicture.setVisible(true);
        this.setLoadBG.setVisible(true);
        this.setRoomBG.setVisible(true);
        this.setGameBG.setVisible(true);
        this.setBoardBG.setVisible(true);
        this.back.setVisible(true);

        this.setMenuBG.addActionListener((e) -> {
            try {
                Image menuBackground = ImageIO.read(new File("C:/MineChessS/src/main/resources/29-5-2022_1153_images8.alphacoders.com.jpeg"));
                MenuStage.instance().setBackgroundImage(menuBackground);
            } catch (IOException exception){
                exception.printStackTrace();
            }
        });
        this.setPiecePicture.addActionListener((e) -> {
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
                    boolean flag = true;
                    for (Point2D point : Chess.canMovePositions) {
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
                        String m = piece.getName().name() + piece.getColor();
                        String location = "";
                        ImageIcon x;
                        switch (m) {
                            case "BBLACK" -> {
                                x = new ImageIcon("C:/MineChessS/src/main/resources/pieces3/BBLACK.png");
                                location = x.getDescription();
                            }
                            case "BWHITE" -> {
                                x = new ImageIcon("C:/MineChessS/src/main/resources/pieces3/BWHITE.png");
                                location = x.getDescription();
                            }
                            case "KBLACK" -> {
                                x = new ImageIcon("C:/MineChessS/src/main/resources/pieces3/KBLACK.png");
                                location = x.getDescription();
                            }
                            case "KWHITE" -> {
                                x = new ImageIcon("C:/MineChessS/src/main/resources/pieces3/KWHITE.png");
                                location = x.getDescription();
                            }
                            case "NBLACK" -> {
                                x = new ImageIcon("C:/MineChessS/src/main/resources/pieces3/NBLACK.png");
                                location = x.getDescription();
                            }
                            case "NWHITE" -> {
                                x = new ImageIcon("C:/MineChessS/src/main/resources/pieces3/NWHITE.png");
                                location = x.getDescription();
                            }
                            case "PBLACK" -> {
                                x = new ImageIcon("C:/MineChessS/src/main/resources/pieces3/PBLACK.png");
                                location = x.getDescription();
                            }
                            case "PWHITE" -> {
                                x = new ImageIcon("C:/MineChessS/src/main/resources/pieces3/PWHITE.png");
                                location = x.getDescription();
                            }
                            case "QBLACK" -> {
                                x = new ImageIcon("C:/MineChessS/src/main/resources/pieces3/QBLACK.png");
                                location = x.getDescription();
                            }
                            case "QWHITE" -> {
                                x = new ImageIcon("C:/MineChessS/src/main/resources/pieces3/QWHITE.png");
                                location = x.getDescription();
                            }
                            case "RBLACK" -> {
                                x = new ImageIcon("C:/MineChessS/src/main/resources/pieces3/RBLACK.png");
                                location = x.getDescription();
                            }
                            case "RWHITE" -> {
                                x = new ImageIcon("C:/MineChessS/src/main/resources/pieces3/RWHITE.png");
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
        });
        this.setBoardBG.addActionListener((e) -> {
            try {
                Image chessBackground = ImageIO.read(new File("C:/MineChessS/src/main/resources/img.png"));
                View.setBoardViewPattern(() -> new BoardView(chessBackground) {});
            } catch (IOException exception){
                exception.printStackTrace();
            }
        });
        this.setGameBG.addActionListener((e) -> {
            try {
                Image menuBackground = ImageIO.read(new File("C:/MineChessS/src/main/resources/29-5-2022_105949_images8.alphacoders.com.jpeg"));
                GameStage.instance().setBackgroundImage(menuBackground);
            } catch (IOException exception){
                exception.printStackTrace();
            }
        });
        this.back.addActionListener((e) -> {
            View.changeStage("MenuStage");
        });
        this.drawComponents = () -> {
            this.add(this.dummyPanel);
            this.add("North", this.title);
            this.buttonPanel.add(Box.createVerticalStrut(10));
            this.buttonPanel.add(this.setMenuBG);
            this.buttonPanel.add(Box.createVerticalStrut(10));
            this.buttonPanel.add(this.setLoadBG);
            this.buttonPanel.add(Box.createVerticalStrut(10));
            this.buttonPanel.add(this.setRoomBG);
            this.buttonPanel.add(Box.createVerticalStrut(10));
            this.buttonPanel.add(this.setBoardBG);
            this.buttonPanel.add(Box.createVerticalStrut(10));
            this.buttonPanel.add(this.setPiecePicture);
            this.buttonPanel.add(Box.createVerticalStrut(10));
            this.buttonPanel.add(this.setGameBG);
            this.buttonPanel.add(Box.createVerticalStrut(10));
            this.buttonPanel.add(this.back);
            this.buttonPanel.add(Box.createVerticalGlue());
            this.dummyPanel.add(this.buttonPanel);
        };
    }

    public void init() {
        super.init();
        this.revalidate();
        this.repaint();
    }

    public static Settings instance() {
        if (sInstance == null) {
            Class var0 = BaseStage.class;
            synchronized(BaseStage.class) {
                if (sInstance == null) {
                    sInstance = new Settings();
                }
            }
        }
        return sInstance;
    }
}
