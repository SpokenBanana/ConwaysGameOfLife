import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
/*
    The main logic for Conway's Game of life!
 */
public class CGL extends JPanel{
    public static void main(String[] args) {
    }
    private boolean[][] board;
    public int cellSize, w, h, frameRate;
    public Color DEAD_COLOR, LIVE_COLOR;
    public CGL() {
        cellSize = 10;
        setBoardSize(35, 35);
        frameRate = 10;
        this.DEAD_COLOR = Color.BLACK;
        this.LIVE_COLOR = Color.WHITE;
    }
    /*
        setting up the board according to the image they passed
     */
    public void setBoard(BufferedImage pic) {
        board = new boolean[pic.getHeight()][pic.getWidth()];
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++)
                board[j][i] = (new Color(pic.getRGB(i, j)).getRed() != 0);
        setCellSize(this.cellSize);
    }
    /*
        Changing the size of the cells displayed also changes the frame so that it can fit the cells
     */
    public void setCellSize(int size) {
        this.cellSize = size;
        this.w = board[0].length * cellSize;
        this.h = board.length * cellSize;
        this.setPreferredSize(new Dimension(this.w, this.h));
    }
    /*
        Setting up an empty board
     */
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
            try {
                if (board[(((x + i[0]) % board.length) + board.length) % board.length][(((y + i[1]) % board[0].length) + board[0].length) % board[0].length])
                    total++;
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
        return total;
    }
    private void step() {
        // this captures the cells we need to change, allowing us to not have to re-visit every cell
        ArrayList<int[]> trues = new ArrayList<int[]>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                int neighbors = getNeighbors(j, i);
                int[] changed = new int[2]; // this saves the index where a cell is changed
                if (board[j][i]) {
                    if (neighbors < 2 || neighbors > 3) {
                        changed[0] = j;
                        changed[1] = i;
                        trues.add(changed);
                    }
                } else {
                    changed[0] = j; changed[1] = i;
                    if (neighbors == 3)
                        trues.add(changed);
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
                // the ternary condition prevents the console from constantly print an error message when they pause this
                Thread.sleep(1000 / (frameRate == 0 ? 1 : frameRate));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void paintComponent(Graphics g) {
        g.clearRect(0,0, this.getWidth(), this.getHeight());
        g.setColor(this.DEAD_COLOR);
        g.fillRect(0, 0, this.w, this.h);
        g.setColor(this.LIVE_COLOR);
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++)
                if (board[i][j]) g.fillRect(j *  cellSize, i * cellSize, cellSize, cellSize);
    }
}