package model;

import controller.Direction;

import java.util.ArrayList;

class Invalid {
    public Direction moveDirection = Direction.RIGHT;
    public final ArrayList<Tuple> invalidPositions = new ArrayList<>();
    public final boolean inPortal = false;
    public boolean replaceAfterDivingInPortal = false;
    public final Entity[][] entities;
    public final Field field;
    private int poisonCounter = 0;
    public final ArrayList<Tuple> snakePositions = new ArrayList<>();
    private Tuple lastDisappearedBodyPart;
    public boolean ShouldSnakeDie = false;

    Invalid(Field field) {
        this.entities = field.field;
        this.field = field;
        findInvalidOnTheMap();
    }

    private void shortenInvalid() {
        if (poisonCounter > 0) {
            poisonCounter--;
            removeTail();
        }
    }

    private int FindDistance(Tuple invalid, Tuple snake) {
        return GetColumnDifference(invalid, snake) +
                GetRowDifference(invalid, snake);
    }

    private int GetRowDifference(Tuple invalid, Tuple snake) {
        return Math.abs(invalid.getRow() - snake.getRow());
    }

    private int GetColumnDifference(Tuple invalid, Tuple snake) {
        return Math.abs(invalid.getColumn() - snake.getColumn());
    }

    private void findDirection() {
        findSnakeOnTheMap();
        int minDist = 100000;
        Tuple ans = new Tuple(0, 0);
        Tuple headInvalid = invalidPositions.get(invalidPositions.size() - 1);
        for (Tuple partOfSnake : snakePositions) {
            if (FindDistance(headInvalid, partOfSnake) < minDist)
            {
                minDist = FindDistance(invalidPositions.get(invalidPositions.size() - 1),
                        partOfSnake);
                ans = partOfSnake;
            }
        }
        if (moveDirection == Direction.LEFT && ans.getColumn() > headInvalid.getColumn()
                && GetRowDifference(headInvalid, ans) > GetColumnDifference(headInvalid, ans)){
            if (ans.getRow() < headInvalid.getRow())
                moveDirection = Direction.UP;
            else
                moveDirection = Direction.DOWN;
        }
        if (moveDirection == Direction.RIGHT && ans.getColumn() < headInvalid.getColumn()
                && GetRowDifference(headInvalid, ans) > GetColumnDifference(headInvalid, ans)){
            if (ans.getRow() < headInvalid.getRow())
                moveDirection = Direction.UP;
            else
                moveDirection = Direction.DOWN;
        }
        if (moveDirection == Direction.DOWN && ans.getRow() < headInvalid.getRow()
                && GetRowDifference(headInvalid, ans) < GetColumnDifference(headInvalid, ans)){
            if (ans.getColumn() > headInvalid.getColumn())
                moveDirection = Direction.RIGHT;
            else
                moveDirection = Direction.LEFT;
        }
        if (moveDirection == Direction.UP && ans.getRow() > headInvalid.getRow()
                && GetRowDifference(headInvalid, ans) < GetColumnDifference(headInvalid, ans)){
            if (ans.getColumn() > headInvalid.getColumn())
                moveDirection = Direction.RIGHT;
            else
                moveDirection = Direction.LEFT;
        }
    }
    void update() {
        shortenInvalid();
        findDirection();

        addNewTupleToInvalidPositions(moveDirection);
        if (isCollidedWithInvalid() || isCollidedWithWall()) {
            if (isCollidedWithWall()) {
                if (moveDirection == Direction.RIGHT || moveDirection == Direction.LEFT)
                    moveDirection = Direction.UP;
                if (moveDirection == Direction.UP || moveDirection == Direction.DOWN)
                    moveDirection = Direction.LEFT;
            }
            invalidPositions.remove(invalidPositions.size() - 1);
            return;
        }

        if (isCollidedWithPoison()) {
            if (invalidPositions.size() < 7) {
                Tuple poisonPosition = invalidPositions.get(invalidPositions.size() - 1);
                entities[poisonPosition.getRow()][poisonPosition.getColumn()] = null;
                invalidPositions.remove(invalidPositions.size() - 1);
                return;
            }
            poisonCounter += 3;
        }
        ShouldSnakeDie = isCollidedWithSnake();
        if (ShouldSnakeDie)
            return;
        if (isCollidedWithFood()) {
            replaceInvalid(true);
            field.spawnFood();
        }

        else if (!inPortal || !replaceAfterDivingInPortal)
            replaceInvalid(false);
        else if (invalidPositions.size() > 1)
            removeTail();
        else
            invalidPositions.remove(0);

    }

    private int setAngleAfterTurnBody(Tuple objectForTurnPos, Tuple turnBodyObjectPos) {
        InvalidEntity turnBodyObject =
                (InvalidEntity) entities[turnBodyObjectPos.getRow()][turnBodyObjectPos.getColumn()];

        if (turnBodyObject.angle == 0 && objectForTurnPos.getRow() == turnBodyObjectPos.getRow())
            return 90;
        if (turnBodyObject.angle == 0)
            return 180;
        if (turnBodyObject.angle == 90 && objectForTurnPos.getRow() == turnBodyObjectPos.getRow())
            return -90;
        if (turnBodyObject.angle == 90)
            return 180;
        if (turnBodyObject.angle == 180 && objectForTurnPos.getRow() == turnBodyObjectPos.getRow())
            return -90;
        if (turnBodyObject.angle == 180)
            return 0;
        if (turnBodyObject.angle == -90 && objectForTurnPos.getRow() == turnBodyObjectPos.getRow())
            return 90;
        return 0;
    }

    private void replaceInvalid(boolean invalidGrow) {
        InvalidEntity newHead;

        if (moveDirection == Direction.RIGHT)
            newHead = new InvalidEntity(Images.INVALID_HEAD_RIGHT, 0);
        else if (moveDirection == Direction.UP)
            newHead = new InvalidEntity(Images.INVALID_HEAD_TOP, -90);
        else if (moveDirection == Direction.LEFT)
            newHead = new InvalidEntity(Images.INVALID_HEAD_LEFT, 180);
        else
            newHead = new InvalidEntity(Images.INVALID_HEAD_BOTTOM, 90);

        if (!inPortal) {
            Tuple currentHeadPos = invalidPositions.get(invalidPositions.size() - 1);
            entities[currentHeadPos.getRow()][currentHeadPos.getColumn()] = newHead;
        }
        else
            replaceAfterDivingInPortal = true;

        Tuple previousHeadPos = invalidPositions.get(invalidPositions.size() - 2);
        InvalidEntity previousHead =
                (InvalidEntity) entities[previousHeadPos.getRow()][previousHeadPos.getColumn()];

        if (previousHead.angle == 0 && newHead.angle == 90 ||
                previousHead.angle == -90 && newHead.angle == 180)
            previousHead = new InvalidEntity(Images.INVALID_SNAKE_LEFT_BOTTOM, 0);
        else if (previousHead.angle == 0 && newHead.angle == -90 ||
                previousHead.angle == 90 && newHead.angle == 180)
            previousHead = new InvalidEntity(Images.INVALID_SNAKE_LEFT_TOP, 90);
        else if (previousHead.angle == -90 && newHead.angle == 0 ||
                previousHead.angle == 180 && newHead.angle == 90)
            previousHead = new InvalidEntity(Images.INVALID_SNAKE_RIGHT_BOTTOM, -90);
        else if (previousHead.angle == 90 && newHead.angle == 0 ||
                previousHead.angle == 180 && newHead.angle == -90)
            previousHead = new InvalidEntity(Images.INVALID_SNAKE_RIGHT_TOP, 180);
        else if (moveDirection == Direction.RIGHT || moveDirection == Direction.LEFT)
            previousHead = new InvalidEntity(Images.INVALID_SNAKE_BODY_HORIZONTAL, newHead.angle);
        else
            previousHead = new InvalidEntity(Images.INVALID_SNAKE_BODY_VERTICAL, newHead.angle);

        entities[previousHeadPos.getRow()][previousHeadPos.getColumn()] = previousHead;

        if (!invalidGrow)
            removeTail();
    }

    private void removeTail() {
        Tuple newTailPos = invalidPositions.get(1);
        Tuple previousTailPos = invalidPositions.get(0);

        int angleTail = 0;
        Images tailImage = null;
        boolean turnFlag = false;
        Entity previousObj = entities[newTailPos.getRow()][newTailPos.getColumn()];

        if (previousObj.image == Images.INVALID_SNAKE_LEFT_BOTTOM ||
                previousObj.image == Images.INVALID_SNAKE_LEFT_TOP ||
                previousObj.image == Images.INVALID_SNAKE_RIGHT_BOTTOM ||
                previousObj.image == Images.INVALID_SNAKE_RIGHT_TOP) {
            angleTail = setAngleAfterTurnBody(previousTailPos, newTailPos);
            if (angleTail == 0)
                tailImage = Images.INVALID_TAIL_RIGHT;
            else if (angleTail == 90)
                tailImage = Images.INVALID_TAIL_BOTTOM;
            else if (angleTail == 180)
                tailImage = Images.INVALID_TAIL_LEFT;
            else
                tailImage = Images.INVALID_TAIL_TOP;

            turnFlag = true;
        }

        if (entities[newTailPos.getRow()][newTailPos.getColumn()].image != Images.PORTAL)
            entities[newTailPos.getRow()][newTailPos.getColumn()] =
                    entities[previousTailPos.getRow()][previousTailPos.getColumn()];

        if (turnFlag) {
            ((InvalidEntity) entities[newTailPos.getRow()][newTailPos.getColumn()]).angle = angleTail;
            ((InvalidEntity) entities[newTailPos.getRow()][newTailPos.getColumn()]).image = tailImage;
        }

        Tuple tailPos = invalidPositions.get(0);
        entities[tailPos.getRow()][tailPos.getColumn()] = null;
        invalidPositions.remove(0);
    }

    public void isDying() {
        if (invalidPositions.size() > 0) {
            Tuple last = invalidPositions.get(invalidPositions.size() - 1);
            invalidPositions.remove(invalidPositions.size() - 1);
            field.field[last.getRow()][last.getColumn()] = new Blood(Images.BLOOD);
            if (lastDisappearedBodyPart != null)
                if (!(field.field[lastDisappearedBodyPart.getRow()][lastDisappearedBodyPart.getColumn()]
                        instanceof SnakeEntity))
                field.field[lastDisappearedBodyPart.getRow()][lastDisappearedBodyPart.getColumn()] =
                        null;
            lastDisappearedBodyPart = last;
            return;
        }

        if (lastDisappearedBodyPart != null) {
            if (!(field.field[lastDisappearedBodyPart.getRow()][lastDisappearedBodyPart.getColumn()]
                    instanceof SnakeEntity))
                field.field[lastDisappearedBodyPart.getRow()][lastDisappearedBodyPart.getColumn()] = null;
            lastDisappearedBodyPart = null;
        }

    }

    private boolean isCollidedWithFood() {
        Tuple invalidHeadPos = invalidPositions.get(invalidPositions.size() - 1);
        return entities[invalidHeadPos.getRow()][invalidHeadPos.getColumn()] instanceof Food;
    }

    private boolean isCollidedWithWall() {
        Tuple invalidHeadPos = invalidPositions.get(invalidPositions.size() - 1);
        return entities[invalidHeadPos.getRow()][invalidHeadPos.getColumn()] instanceof RedWall ||
                entities[invalidHeadPos.getRow()][invalidHeadPos.getColumn()] instanceof DarkWall;
    }

    private boolean isCollidedWithInvalid() {
        Tuple invalidHeadPos = invalidPositions.get(invalidPositions.size() - 1);
        return entities[invalidHeadPos.getRow()][invalidHeadPos.getColumn()] instanceof InvalidEntity;
    }

    private boolean isCollidedWithSnake() {
        Tuple invalidHeadPos = invalidPositions.get(invalidPositions.size() - 1);
        return entities[invalidHeadPos.getRow()][invalidHeadPos.getColumn()] instanceof SnakeEntity;
    }

    private boolean isCollidedWithPoison() {
        Tuple invalidHeadPos = invalidPositions.get(invalidPositions.size() - 1);
        return entities[invalidHeadPos.getRow()][invalidHeadPos.getColumn()] instanceof Poison;
    }

    private void addNewTupleToInvalidPositions(Direction direction) {
        Tuple invalidHeadPos = invalidPositions.get(invalidPositions.size() - 1);
        Tuple newTuple = new Tuple(0, 0);

        switch (direction) {
            case DOWN:
                newTuple = new Tuple((invalidHeadPos.getRow() + 1) % field.rowsCount,
                        invalidHeadPos.getColumn());
                break;
            case UP:
                newTuple = new Tuple((invalidHeadPos.getRow() - 1 + field.rowsCount) %
                        field.rowsCount, invalidHeadPos.getColumn());
                break;
            case LEFT:
                newTuple = new Tuple(invalidHeadPos.getRow(), (invalidHeadPos.getColumn() - 1
                        + field.columnsCount) % field.columnsCount);
                break;
            case RIGHT:
                newTuple = new Tuple(invalidHeadPos.getRow(),
                        (invalidHeadPos.getColumn() + 1) % field.columnsCount);
                break;
        }
        invalidPositions.add(newTuple);
    }

    private void findInvalidOnTheMap() {
        for (int i = 0; i < entities.length; i++)
            for (int j = 0; j < entities[0].length; j++)
                if (entities[i][j] instanceof InvalidEntity)
                    if (entities[i][j].image == Images.INVALID_TAIL_RIGHT) {
                        invalidPositions.add(new Tuple(i, j));
                        break;
                    }
        for (int i = 0; i < entities.length; i++)
            for (int j = 0; j < entities[0].length; j++)
                if (entities[i][j] instanceof InvalidEntity)
                    if (entities[i][j].image == Images.INVALID_SNAKE_BODY_HORIZONTAL) {
                        invalidPositions.add(new Tuple(i, j));
                        break;
                    }
        for (int i = 0; i < entities.length; i++)
            for (int j = 0; j < entities[0].length; j++)
                if (entities[i][j] instanceof InvalidEntity)
                    if (entities[i][j].image == Images.INVALID_HEAD_RIGHT) {
                        invalidPositions.add(new Tuple(i, j));
                        break;
                    }
    }

    private void findSnakeOnTheMap() {
        snakePositions.clear();
        for (int i = 0; i < entities.length; i++)
            for (int j = 0; j < entities[0].length; j++)
                if (entities[i][j] instanceof SnakeEntity)
                    if (entities[i][j].image == Images.TAIL_RIGHT) {
                        snakePositions.add(new Tuple(i, j));
                        break;
                    }
        for (int i = 0; i < entities.length; i++)
            for (int j = 0; j < entities[0].length; j++)
                if (entities[i][j] instanceof SnakeEntity)
                    if (entities[i][j].image == Images.SNAKE_BODY_HORIZONTAL) {
                        snakePositions.add(new Tuple(i, j));
                        break;
                    }
        for (int i = 0; i < entities.length; i++)
            for (int j = 0; j < entities[0].length; j++)
                if (entities[i][j] instanceof SnakeEntity)
                    if (entities[i][j].image == Images.HEAD_RIGHT) {
                        snakePositions.add(new Tuple(i, j));
                        break;
                    }
    }
}