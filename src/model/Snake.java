package model;

import controller.Direction;

import java.util.ArrayList;

class Snake {
    public Direction moveDirection = Direction.RIGHT;
    public ArrayList<Tuple> snakePositions = new ArrayList<>();
    private boolean needToReturn = false;
    public boolean inPortal = false;
    public boolean replaceAfterDivingInPortal = false;
    public boolean goingOutFromPortal = false;
    public boolean needChangeLevel = false;
    public Entity[][] entities;
    public Field field;
    public boolean lastLevel = false;
    public boolean ShouldInvalidDie = false;
    private Tuple portalPosition = new Tuple(3, 4);
    private int drunkCounter = 0;
    private int poisonCounter = 0;
    private Tuple lastDisappearedBodyPart;

    Snake(Field field) {
        this.entities = field.field;
        this.field = field;
        findSnakeOnTheMap();
    }

    private Direction mirrorDirection(Direction direction) {
        if (direction == Direction.RIGHT)
            return Direction.LEFT;
        if (direction == Direction.LEFT)
            return Direction.RIGHT;
        if (direction == Direction.UP)
            return Direction.DOWN;
        if (direction == Direction.DOWN)
            return Direction.UP;
        return direction;
    }

    private boolean isDrunk() {
        if (drunkCounter > 0) {
            drunkCounter--;
            return true;
        }
        return false;
    }

    private void shortenSnake() {
        if (poisonCounter > 0) {
            poisonCounter--;
            removeTail();
        }
    }

    boolean update(Direction directionFromController) {
        if (snakePositions.size() == 0 && !goingOutFromPortal) {
            needChangeLevel = true;
            ShouldInvalidDie = false;
            goingOutFromPortal = true;
            return true;
        }
        if (snakePositions.size() == 0) {
            snakePositions.add(new Tuple(portalPosition.getRow(), portalPosition.getColumn() + 1));
            entities[portalPosition.getRow()][portalPosition.getColumn() + 1] =
                    new SnakeEntity(Images.HEAD_RIGHT, 0);
            return true;
        }
        if (snakePositions.size() == 1 && goingOutFromPortal) {
            Tuple bodyPosition = snakePositions.get(0);
            entities[bodyPosition.getRow()][bodyPosition.getColumn()].image =
                    Images.SNAKE_BODY_HORIZONTAL;
            snakePositions.add(new Tuple(portalPosition.getRow(), portalPosition.getColumn() + 2));
            entities[portalPosition.getRow()][portalPosition.getColumn() + 2] =
                    new SnakeEntity(Images.HEAD_RIGHT, 0);
            return true;
        }
        if (snakePositions.size() == 2 && goingOutFromPortal) {
            Tuple tailPosition = snakePositions.get(0);
            entities[tailPosition.getRow()][tailPosition.getColumn()].image =
                    Images.TAIL_RIGHT;
            Tuple bodyPosition = snakePositions.get(1);
            entities[bodyPosition.getRow()][bodyPosition.getColumn()].image =
                    Images.SNAKE_BODY_HORIZONTAL;
            snakePositions.add(new Tuple(portalPosition.getRow(), portalPosition.getColumn() + 3));
            entities[portalPosition.getRow()][portalPosition.getColumn() + 3] =
                    new SnakeEntity(Images.HEAD_RIGHT, 0);
            goingOutFromPortal = false;
            inPortal = false;
            moveDirection = Direction.RIGHT;
            replaceAfterDivingInPortal = false;
            entities[portalPosition.getRow()][portalPosition.getColumn()] = null;
            return true;
        }

        if (isDrunk())
            directionFromController = mirrorDirection(directionFromController);

        shortenSnake();

        if (directionFromController == Direction.RIGHT) {
            if (moveDirection != Direction.LEFT)
                moveDirection = directionFromController;
        }
        else if (directionFromController == Direction.UP) {
            if (moveDirection != Direction.DOWN)
                moveDirection = directionFromController;
        }
        else if (directionFromController == Direction.LEFT) {
            if (moveDirection != Direction.RIGHT)
                moveDirection = directionFromController;
        }
        else if (directionFromController == Direction.DOWN) {
            if (moveDirection != Direction.UP)
                moveDirection = directionFromController;
        }

        if (!inPortal)
            addNewTupleToSnakePositions(moveDirection);
        if (needToReturn) {
            needToReturn = false;
            return true;
        }
        if (isCollidedWithPortal()) {
            inPortal = true;
        }
        if (isCollidedWithSnake() || isCollidedWithWall() && !inPortal) {
            snakePositions.remove(snakePositions.size() - 1);
            return false;
        }

        if (isCollidedWithAlcohol())
            drunkCounter += 10;

        if (isCollidedWithPoison()) {
             if (snakePositions.size() < 7) {
                 Tuple poisonPosition = snakePositions.get(snakePositions.size() - 1);
                 entities[poisonPosition.getRow()][poisonPosition.getColumn()] = null;
                 snakePositions.remove(snakePositions.size() - 1);
                 return false;
             }
            poisonCounter += 3;
        }


        if (isCollidedWithFood()) {
            replaceSnake(true);
            if (snakePositions.size() == 7 && !lastLevel)
                openPortal();
            else
                field.spawnFood();
        }
        else if (!inPortal || !replaceAfterDivingInPortal)
            replaceSnake(false);
        else if (snakePositions.size() > 1)
            removeTail();
        else
            snakePositions.remove(0);

        return true;
    }

    private int setAngleAfterTurnBody(Tuple objectForTurnPos, Tuple turnBodyObjectPos) {
        SnakeEntity turnBodyObject =
                (SnakeEntity) entities[turnBodyObjectPos.getRow()][turnBodyObjectPos.getColumn()];

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

    private void replaceSnake(boolean snakeGrow) {
        SnakeEntity newHead;

        if (moveDirection == Direction.RIGHT)
            newHead = new SnakeEntity(Images.HEAD_RIGHT, 0);
        else if (moveDirection == Direction.UP)
            newHead = new SnakeEntity(Images.HEAD_TOP, -90);
        else if (moveDirection == Direction.LEFT)
            newHead = new SnakeEntity(Images.HEAD_LEFT, 180);
        else
            newHead = new SnakeEntity(Images.HEAD_BOTTOM, 90);

        if (!inPortal) {
            Tuple currentHeadPos = snakePositions.get(snakePositions.size() - 1);
            entities[currentHeadPos.getRow()][currentHeadPos.getColumn()] = newHead;
        }
        else
            replaceAfterDivingInPortal = true;

        Tuple previousHeadPos = snakePositions.get(snakePositions.size() - 2);
        SnakeEntity previousHead =
                (SnakeEntity) entities[previousHeadPos.getRow()][previousHeadPos.getColumn()];

        if (previousHead.angle == 0 && newHead.angle == 90 ||
                previousHead.angle == -90 && newHead.angle == 180)
            previousHead = new SnakeEntity(Images.SNAKE_LEFT_BOTTOM, 0);
        else if (previousHead.angle == 0 && newHead.angle == -90 ||
                previousHead.angle == 90 && newHead.angle == 180)
            previousHead = new SnakeEntity(Images.SNAKE_LEFT_TOP, 90);
        else if (previousHead.angle == -90 && newHead.angle == 0 ||
                previousHead.angle == 180 && newHead.angle == 90)
            previousHead = new SnakeEntity(Images.SNAKE_RIGHT_BOTTOM, -90);
        else if (previousHead.angle == 90 && newHead.angle == 0 ||
                previousHead.angle == 180 && newHead.angle == -90)
            previousHead = new SnakeEntity(Images.SNAKE_RIGHT_TOP, 180);
        else if (moveDirection == Direction.RIGHT || moveDirection == Direction.LEFT)
            previousHead = new SnakeEntity(Images.SNAKE_BODY_HORIZONTAL, newHead.angle);
        else
            previousHead = new SnakeEntity(Images.SNAKE_BODY_VERTICAL, newHead.angle);

        entities[previousHeadPos.getRow()][previousHeadPos.getColumn()] = previousHead;

        if (!snakeGrow)
            removeTail();
    }

    private void removeTail() {
        Tuple newTailPos = snakePositions.get(1);
        Tuple previousTailPos = snakePositions.get(0);

        int angleTail = 0;
        Images tailImage = null;
        boolean turnFlag = false;
        Entity previousObj = entities[newTailPos.getRow()][newTailPos.getColumn()];

        if (previousObj.image == Images.SNAKE_LEFT_BOTTOM ||
                previousObj.image == Images.SNAKE_LEFT_TOP ||
                previousObj.image == Images.SNAKE_RIGHT_BOTTOM ||
                previousObj.image == Images.SNAKE_RIGHT_TOP) {
            angleTail = setAngleAfterTurnBody(previousTailPos, newTailPos);
            if (angleTail == 0)
                tailImage = Images.TAIL_RIGHT;
            else if (angleTail == 90)
                tailImage = Images.TAIL_BOTTOM;
            else if (angleTail == 180)
                tailImage = Images.TAIL_LEFT;
            else
                tailImage = Images.TAIL_TOP;

            turnFlag = true;
        }

        if (entities[newTailPos.getRow()][newTailPos.getColumn()].image != Images.PORTAL)
            entities[newTailPos.getRow()][newTailPos.getColumn()] =
                    entities[previousTailPos.getRow()][previousTailPos.getColumn()];

        if (turnFlag) {
            ((SnakeEntity) entities[newTailPos.getRow()][newTailPos.getColumn()]).angle = angleTail;
            ((SnakeEntity) entities[newTailPos.getRow()][newTailPos.getColumn()]).image = tailImage;
        }

        Tuple tailPos = snakePositions.get(0);
        entities[tailPos.getRow()][tailPos.getColumn()] = null;
        snakePositions.remove(0);
    }

    public boolean isDying() {
        if (snakePositions.size() > 0) {
            Tuple last = snakePositions.get(snakePositions.size() - 1);
            snakePositions.remove(snakePositions.size() - 1);
            field.field[last.getRow()][last.getColumn()] = new Blood(Images.BLOOD);
            if (lastDisappearedBodyPart != null)
                field.field[lastDisappearedBodyPart.getRow()][lastDisappearedBodyPart.getColumn()] =
                        null;
            lastDisappearedBodyPart = last;
            return true;
        }

        if (lastDisappearedBodyPart != null) {
            field.field[lastDisappearedBodyPart.getRow()][lastDisappearedBodyPart.getColumn()] = null;
            lastDisappearedBodyPart = null;
            return true;
        }

        return false;
    }

    private boolean isCollidedWithFood() {
        Tuple snakeHeadPos = snakePositions.get(snakePositions.size() - 1);
        return entities[snakeHeadPos.getRow()][snakeHeadPos.getColumn()] instanceof Food;
    }

    private boolean isCollidedWithInvalid(Tuple pos) {
        return entities[pos.getRow()][pos.getColumn()] instanceof InvalidEntity;
    }

    private boolean isCollidedWithWall() {
        Tuple snakeHeadPos = snakePositions.get(snakePositions.size() - 1);
        return entities[snakeHeadPos.getRow()][snakeHeadPos.getColumn()] instanceof RedWall ||
                entities[snakeHeadPos.getRow()][snakeHeadPos.getColumn()] instanceof DarkWall;
    }

    private boolean isCollidedWithAlcohol() {
        Tuple snakeHeadPos = snakePositions.get(snakePositions.size() - 1);
        return entities[snakeHeadPos.getRow()][snakeHeadPos.getColumn()] instanceof Alcohol;
    }

    private boolean isCollidedWithSnake() {
        Tuple snakeHeadPos = snakePositions.get(snakePositions.size() - 1);
        return entities[snakeHeadPos.getRow()][snakeHeadPos.getColumn()] instanceof SnakeEntity;
    }

    private boolean isCollidedWithPoison() {
        Tuple snakeHeadPos = snakePositions.get(snakePositions.size() - 1);
        return entities[snakeHeadPos.getRow()][snakeHeadPos.getColumn()] instanceof Poison;
    }

    private boolean isCollidedWithPortal() {
        Tuple snakeHeadPos = snakePositions.get(snakePositions.size() - 1);
        return entities[snakeHeadPos.getRow()][snakeHeadPos.getColumn()] instanceof Portal;
    }

    private void addNewTupleToSnakePositions(Direction direction) {
        Tuple snakeHeadPos = snakePositions.get(snakePositions.size() - 1);
        Tuple newTuple = new Tuple(0, 0);

        switch (direction) {
            case DOWN:
                newTuple = new Tuple((snakeHeadPos.getRow() + 1) % field.rowsCount,
                        snakeHeadPos.getColumn());
                break;
            case UP:
                newTuple = new Tuple((snakeHeadPos.getRow() - 1 + field.rowsCount) %
                        field.rowsCount, snakeHeadPos.getColumn());
                break;
            case LEFT:
                newTuple = new Tuple(snakeHeadPos.getRow(), (snakeHeadPos.getColumn() - 1
                        + field.columnsCount) % field.columnsCount);
                break;
            case RIGHT:
                newTuple = new Tuple(snakeHeadPos.getRow(),
                        (snakeHeadPos.getColumn() + 1) % field.columnsCount);
                break;
        }
        if (isCollidedWithInvalid(newTuple)) {
            ShouldInvalidDie = true;
            needToReturn = true;
            return;
        }
        snakePositions.add(newTuple);
    }

    private void openPortal() {
        for (int i = 0; i < entities.length; i++)
            for (int j = 0; j < entities[0].length; j++)
                if (entities[i][j] != null && entities[i][j].image == Images.PORTAL) {
                    for (int r = i - 1; r <= i + 1; r++)
                        for (int c = j - 1; c <= j + 1; c++)
                            if (entities[r][c] != null && entities[r][c].image != Images.PORTAL)
                                entities[r][c] = null;
                    break;
                }
    }

    private void findSnakeOnTheMap() {
        snakePositions.clear();
        for (int i = 0; i < entities.length; i++)
            for (int j = 0; j < entities[0].length; j++)
                if (entities[i][j] instanceof SnakeEntity)
                    if (entities[i][j].image == Images.TAIL_RIGHT ) {
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