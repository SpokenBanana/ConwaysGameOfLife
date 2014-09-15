import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
public class CGL extends JPanel{
    public static void main(String[] args) {
    }
    private boolean[][] board;
    public int cellSize, w, h, frameRate;
    public String lastdir;
    public Color black, white;
    public CGL() {
        cellSize = 10;
        setBoardSize(35, 35);
        frameRate = 1000/150;
        this.black = Color.BLACK;
        this.white = Color.white;
    }
    public void setBoard(BufferedImage pic) {
        board = new boolean[pic.getHeight()][pic.getWidth()];
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++)
                board[j][i] = (new Color(pic.getRGB(i, j)).getRed() != 0);
        setCellSize(this.cellSize);
    }
    public void setCellSize(int size) {
        this.cellSize = size;
        this.w = board[0].length * cellSize;
        this.h = board.length * cellSize;
        this.setPreferredSize(new Dimension(this.w, this.h));
    }
    public void setBoardSize(int width, int height) {
        board = new boolean[height][width];
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++)
                board[i][j] = false;
        setCellSize(this.cellSize);
    }
    private int getNeighbors(int x, int y) {
        int[][] increments = {{-1, -1}, {0, -1}, {1, -1},
                              {-1, 0},           {1, 0},
                              {-1, 1},  {0, 1},  {1, 1}};
        int total = 0;
        for (int[] i : increments) {
            try{
                if (board[(((x + i[0]) % board.length) + board.length) % board.length][(((y + i[1]) % board[0].length) + board[0].length) % board[0].length]) total++;
            } catch (IndexOutOfBoundsException e) { continue; }
        }
        return total;
    }
    private void step() {
        ArrayList<int[]> trues = new ArrayList<int[]>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                int neighbors = getNeighbors(j, i);
                int[] changed = new int[2];
                if (board[j][i]) {
                    if (neighbors == 2 || neighbors == 3) continue;
                    else if (neighbors < 2 || neighbors > 3) {
                        changed[0] = j;
                        changed[1] = i;
                        trues.add(changed);
                    }
                } else {
                    changed[0] = j; changed[1] = i;
                    if (neighbors == 3) trues.add(changed);
                }
            }
        }
        for (int[] i : trues) board[i[0]][i[1]] = !board[i[0]][i[1]];
    }
    public void run() {
        while (true) {
            repaint();
            if (frameRate != 0) step();
            try {
                Thread.sleep(1000 / frameRate);
            } catch (Exception e) {}
        }
    }
    public void paintComponent(Graphics g) {
        g.clearRect(0,0, this.getWidth(), this.getHeight());
        g.setColor(this.black);
        g.fillRect(0, 0, this.w, this.h);
        g.setColor(this.white);
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++)
                if (board[i][j]) g.fillRect(j *  cellSize, i * cellSize, cellSize, cellSize);
    }
}