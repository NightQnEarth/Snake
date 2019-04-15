package model;

public class Tuple {
    private final int row;
    private final int column;

    public Tuple(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Tuple tuple = (Tuple) obj;
        return row == tuple.row && column == tuple.column;
    }
}