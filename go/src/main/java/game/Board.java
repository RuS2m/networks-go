package game;

import java.util.Objects;

public class Board {

    public static int BOARD_SIZE = 18;

    private Intersection[][] board;

    public Board() {
        this.board = new Intersection[BOARD_SIZE][BOARD_SIZE];
        for (var i = 0; i != BOARD_SIZE; ++i) {
            for (var j = 0; j != BOARD_SIZE; ++j) {
                this.board[i][j] = new Intersection();
            }
        }
    }

    private Board(Intersection[][] board) {
        this.board = board;
    }

    public Intersection[][] getBoard() {
        return board;
    }

    @Override
    public String toString() {
        var stringBuilder = new StringBuilder();
        stringBuilder.append("-".repeat(BOARD_SIZE * 4 + 2));
        stringBuilder.append("\n");
        for (var row : this.board) {
            stringBuilder.append("|   ".repeat(BOARD_SIZE + 1));
            stringBuilder.append("\n|");
            for (var intersection : row) {
                stringBuilder.append("---");
                if (intersection.stoneColor == null) {
                    stringBuilder.append(" ");
                } else if (intersection.stoneColor == 0) {
                    stringBuilder.append("○");
                } else if (intersection.stoneColor == 1) {
                    stringBuilder.append("●");
                }
            }
            stringBuilder.append("---|\n");
        }
        stringBuilder.append("-".repeat(BOARD_SIZE * 4 + 2));
        return stringBuilder.toString();
    }

    public int addStone(int x, int y, int color) {
        if (x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE) {
            if (this.board[x][y].stoneColor != null) {
                System.out.println("There is already a stone in this intersection");
            } else {
                this.board[x][y].setStoneColor(color);
            }
        } else {
            System.out.println("Wrong stone position");
        }
        return 0;
    }

//    private static Set<List<Stone>> searchCircles(int x, int y) {
//
//    }

//    private static int cleanCircle(List<Stone>)

    public static class Stone {
        private int xPosition;
        private int yPosition;
        // 0 for white, 1 for black
        private int color;

        public Stone(int x, int y, int color) {
            this.xPosition = x;
            this.yPosition = y;
            this.color = color;
        }

        public int getxPosition() {
            return xPosition;
        }

        public int getyPosition() {
            return yPosition;
        }

        public int getColor() {
            return color;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Stone stone = (Stone) o;
            return xPosition == stone.xPosition &&
                    yPosition == stone.yPosition &&
                    color == stone.color;
        }

        @Override
        public int hashCode() {
            return Objects.hash(xPosition, yPosition, color);
        }
    }

    public static class Intersection {

        private boolean isCoolDownEffect;

        private Integer stoneColor;

        public Intersection() {
            this.isCoolDownEffect = false;
            this.stoneColor = null;
        }

        public Intersection(boolean isCoolDownEffect, Integer stoneColor) {
            this.isCoolDownEffect = isCoolDownEffect;
            this.stoneColor = stoneColor;
        }

        public boolean isCoolDownEffect() {
            return isCoolDownEffect;
        }

        public void setCoolDownEffect(boolean coolDownEffect) {

            isCoolDownEffect = coolDownEffect;
        }

        public Integer getStoneColor() {
            return stoneColor;
        }

        public void setStoneColor(Integer stoneColor) {
            if (stoneColor == 0 || stoneColor == 1) {
                this.stoneColor = stoneColor;
            }
        }
    }
}
