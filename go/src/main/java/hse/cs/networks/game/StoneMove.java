package hse.cs.networks.game;

import java.util.Objects;
import java.util.Optional;

public class StoneMove implements Move{

    private Board.Stone stone;

    public StoneMove(Board.Stone stone) {
        this.stone = stone;
    }

    @Override
    public Optional<Board.Stone> stonePosition() {
        return Optional.of(this.stone);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoneMove stoneMove = (StoneMove) o;
        return Objects.equals(stone, stoneMove.stone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stone);
    }
}
