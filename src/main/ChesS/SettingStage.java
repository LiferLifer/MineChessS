import frame.board.BaseGrid;
import frame.util.Point2D;
import frame.view.View;
import frame.view.board.BoardView;
import frame.view.board.GridPanelView;
import frame.view.components.BackgroundImagePanel;
import frame.view.stage.BaseStage;
import frame.view.stage.MenuStage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.awt.Image.SCALE_SMOOTH;

public class SettingStage extends BaseStage {
    private static volatile SettingStage sInstance = null;
    public JLabel title = new JLabel("Set");
    public JButton setMenuBG = new JButton("Change Menu Background");
    public JButton setRoomBG = new JButton("Change Room Background");
    public JButton setLoadBG = new JButton("Change Load Background");
    public JButton setBGM = new JButton("Change Background Music");
    public JButton setPiecePicture = new JButton("Change Piece Picture");
    public JButton setBoardBG = new JButton("Change Board Picture");
    public JButton back = new JButton("Back");
    public Box buttonPanel = new Box(1);
    public BackgroundImagePanel dummyPanel = new BackgroundImagePanel();

    private SettingStage() {
        super("SettingsStage");
        this.setLayout(new BorderLayout());
        this.title.setFont(new Font("Arial", 0, 50));
        this.title.setHorizontalAlignment(0);
        this.setMenuBG.setVisible(true);
        this.setPiecePicture.setVisible(true);
        this.setLoadBG.setVisible(true);
        this.setRoomBG.setVisible(true);
        this.setBGM.setVisible(true);
        this.setBoardBG.setVisible(true);
        this.back.setVisible(true);

        this.setMenuBG.addActionListener((e) -> {
            try {
                Image menuBackground = ImageIO.read(new File("src/main/resources/menu.png"));
                MenuStage.instance().setBackgroundImage(menuBackground);
            } catch (IOException exception){
                exception.printStackTrace();
            }
        });
        this.setPiecePicture.addActionListener((e) -> {
          View.setGridViewPattern(() -> new GridPanelView() {
              boolean isHighLighted = false;
              boolean hasMouseEntered = false;

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
                  for (Point2D point : Chess.canMovePositions) {
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
                      this.label.setIcon(new ImageIcon(bufferedImage.getScaledInstance(60,60,SCALE_SMOOTH)));
                  } else {
                      this.label.setIcon(new ImageIcon());
                  }
                  }});
        });
        this.setBoardBG.addActionListener((e) -> {
            try {
                Image chessBackground = ImageIO.read(new File("src/main/resources/Snipaste_2022-05-22_14-17-21.png"));
                View.setBoardViewPattern(() -> new BoardView(chessBackground) {});
            } catch (IOException exception){
                exception.printStackTrace();
            }
        });
        this.setBGM.addActionListener((e) -> {
            MusicPlayer bgm = new MusicPlayer("src/main/resources/eS=S - 8bit Faith.mp3");
            bgm.start();
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
            this.buttonPanel.add(this.setBGM);
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

    public static SettingStage instance() {
        if (sInstance == null) {
            Class var0 = BaseStage.class;
            synchronized(BaseStage.class) {
                if (sInstance == null) {
                    sInstance = new SettingStage();
                }
            }
        }

        return sInstance;
    }
}
