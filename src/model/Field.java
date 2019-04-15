package model;

public class Field implements java.io.Serializable {
    private static final long serialVersionUID = -2542001418764869760L;

    final int rowsCount;
    final int columnsCount;
    final Entity[][] field;

    public Field(int columnsCount, int rowsCount) {
        this.rowsCount = rowsCount;
        this.columnsCount = columnsCount;
        this.field = new Entity[rowsCount][columnsCount];
        spawnFood();
    }

    public Entity getObjectAt(int row, int column) {
        return field[row][column];
    }

    Tuple getRandomEmptyPos() {
        int ranRow = (int) (Math.random() * rowsCount);
        int ranColumn = (int) (Math.random() * columnsCount);

        while (field[ranRow][ranColumn] != null) {
            ranRow = (int) (Math.random() * rowsCount);
            ranColumn = (int) (Math.random() * columnsCount);
        }
        return new Tuple(ranRow, ranColumn);
    }

    void spawnFood() {
        Tuple position = getRandomEmptyPos();
        field[position.getRow()][position.getColumn()] = new Food();
    }

    public void spawn(Entity entity,Tuple position) {
        field[position.getRow()][position.getColumn()] = entity;
    }

    public void spawn(Entity entity){
        Tuple position = getRandomEmptyPos();
        field[position.getRow()][position.getColumn()] = entity;
    }

    public void resetCell(int row, int column) {
        field[row][column] = null;
    }
}