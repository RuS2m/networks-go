package hse.cs.networks.game;

import java.util.Optional;

public interface Move {

    default boolean isPassive() {
        return false;
    }

    default Optional<Board.Stone> stonePosition() {
        return Optional.empty();
    }
}
