package model;

import static org.junit.jupiter.api.Assertions.*;
import controller.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;


class ModelTests {

    @Nested
    class GameTests {
        private Game game;
        private Direction moveDirection;
        private Tuple neighborCell;

        @BeforeEach
        void gameCreate() {
            game = new Game("Level1.ser");
            neighborCell = new Tuple(game.snake.snakePositions.get(2).getRow(),
                    (game.snake.snakePositions.get(2).getColumn() + 1) %
                             game.field.field.length);
            moveDirection = Direction.RIGHT;
        }

        @ParameterizedTest
        @EnumSource(Direction.class)
        void simpleUpdateTest(Direction direction) {
            int oldLength = game.snake.snakePositions.size();

            assertTrue(game.update(direction));
            assertEquals(game.snake.snakePositions.size(), oldLength);
        }

        @Test
        void updateWithCollisionWithSnakeTest() {
            game.field.field[neighborCell.getRow()][neighborCell.getColumn()] =
                    new SnakeEntity(Images.SNAKE_BODY_HORIZONTAL, 0);

            assertTrue(game.update(moveDirection));
        }

        @Test
        void updateWithCollisionWithFoodTest() {
            game.field.field[neighborCell.getRow()][neighborCell.getColumn()] = new Food();
            int oldLength = game.snake.snakePositions.size();

            assertTrue(game.update(moveDirection));
            assertEquals(game.snake.snakePositions.size(), oldLength + 1);
        }

        @Test
        void getObjectAtTest() {
            game.field.field[neighborCell.getRow()][neighborCell.getColumn()] = new Food();
            System.out.println(game.field.field[neighborCell.getRow()][neighborCell.getColumn()]);
            assertTrue(game.field.getObjectAt(neighborCell.getRow(), neighborCell.getColumn())
                    instanceof Food);
        }

        @RepeatedTest(50)
        void getRandomEmptyPosTest() {
            int cellsWithSnakeCount = (game.field.field.length * game.field.field[0].length) / 4;
            int count = 0;

                while (count < cellsWithSnakeCount) {
                    int ranRow = (int)(Math.random() * game.field.field.length);
                    int ranColumn = (int)(Math.random() * game.field.field[0].length);

                    game.field.field[ranRow][ranColumn] =
                            new SnakeEntity(Images.SNAKE_BODY_HORIZONTAL, 0);

                    count++;
                }

            Tuple emptyPosition = game.field.getRandomEmptyPos();
            assertNull(game.field.field[emptyPosition.getRow()][emptyPosition.getColumn()]);
        }

        @Test
        void moveToReverseDirection() {
            int oldLength = game.snake.snakePositions.size();

            assertTrue(game.update(Direction.LEFT));
            assertEquals(game.snake.snakePositions.get(game.snake.snakePositions.size() - 1),
                    neighborCell);
            assertEquals(game.snake.snakePositions.size(), oldLength);
        }

        @Test
        void updateWithCollisionWithInvalidTest() {
            game.field.field[neighborCell.getRow()][neighborCell.getColumn()] =
                    new SnakeEntity(Images.INVALID_SNAKE_BODY_HORIZONTAL, 0);

            assertTrue(game.update(moveDirection));
            assertTrue(game.field.field[neighborCell.getRow()][neighborCell.getColumn()]
                    instanceof SnakeEntity);
        }

        @Test
        void updateKillFromInvalidTest() {
            game.field.field[1][4] =
                    new SnakeEntity(Images.SNAKE_BODY_HORIZONTAL, 0);

            assertTrue(game.update(moveDirection));
            assertFalse(game.field.field[1][4] instanceof InvalidEntity);
        }

        @Test
        void updateWithCollisionWithPortalTest() {
            game.field.field[neighborCell.getRow()][neighborCell.getColumn()] =
                    new Portal();

            assertTrue(game.update(moveDirection));
            assertTrue(game.field.field[neighborCell.getRow()][neighborCell.getColumn()]
                    instanceof Portal);
        }

        @Test
        void updateWithCollisionWithDarkWallTest() {
            game.field.field[neighborCell.getRow()][neighborCell.getColumn()] =
                    new DarkWall();

            assertTrue(game.update(moveDirection));
            assertTrue(game.field.field[neighborCell.getRow()][neighborCell.getColumn()]
                    instanceof DarkWall);
        }

        @Test
        void updateWithCollisionWithRedWallTest() {
            game.field.field[neighborCell.getRow()][neighborCell.getColumn()] =
                    new RedWall();

            assertTrue(game.update(moveDirection));
            assertTrue(game.field.field[neighborCell.getRow()][neighborCell.getColumn()]
                    instanceof RedWall);
        }

        @Test
        void updateWithCollisionWithPoisonTest() {
            game.field.field[neighborCell.getRow()][neighborCell.getColumn()] =
                    new Poison();

            assertTrue(game.update(moveDirection));
            assertNull(game.field.field[neighborCell.getRow()][neighborCell.getColumn()]);
        }
    }

    @Nested
    class TupleTests {
        @Test
        void equalTupleTest() {
            Tuple firstTuple = new Tuple(1, 2);
            Tuple secondTuple = new Tuple(1, 2);
            Tuple thirdTuple = new Tuple(1, 1);
            assertAll(
                    () -> assertEquals(firstTuple, secondTuple),
                    () -> assertNotEquals(firstTuple, thirdTuple),
                    () -> assertNotEquals(null, thirdTuple),
                    () -> assertNotEquals("Some string", thirdTuple),
                    () -> assertEquals(secondTuple, secondTuple)
            );
        }

        @Test
        void tupleGetTest() {
            Tuple tuple = new Tuple(1, 2);
            assertAll(
                    () -> assertEquals(tuple.getRow(), 1),
                    () -> assertEquals(tuple.getColumn(), 2)
            );
        }
    }
}