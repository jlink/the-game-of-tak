package tak;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

@Label("Game of Tak")
class GameProperties {

	@Property
	void new_game_in_all_possible_sizes(@ForAll @IntRange(min = 3, max = 8) int size) {
		GameOfTak game = new GameOfTak(size);
		assertThat(game.size()).isEqualTo(size);
		assertThat(game.moves()).isEmpty();
		assertThat(game.status()).isEqualTo(GameOfTak.Status.PRELUDE_WHITE);

		TakPosition startingPosition = game.position();
		assertThat(startingPosition.board()).isEqualTo(TakBoard.ofSize(size));
		assertThat(startingPosition.nextToMove()).isEqualTo(TakPlayer.WHITE);

		assertThat(startingPosition.inventory(TakPlayer.WHITE)).isNotEmpty();
		assertThat(startingPosition.inventory(TakPlayer.BLACK)).isNotEmpty();
	}

	@Property
	@FromData("inventories")
	void initial_inventory(@ForAll int boardSize, @ForAll int expectedFlats, @ForAll int expectedCaps) {
		TakPosition startingPosition = new GameOfTak(boardSize).position();

		List<TakStone> whiteInventory = startingPosition.inventory(TakPlayer.WHITE);
		List<TakStone> blackInventory = startingPosition.inventory(TakPlayer.BLACK);
		assertThat(whiteInventory).hasSameSizeAs(blackInventory);
		assertThat(whiteInventory).allMatch(stone -> stone.colour() == TakStone.Colour.WHITE);
		assertThat(blackInventory).allMatch(stone -> stone.colour() == TakStone.Colour.BLACK);

		assertThat(countStones(whiteInventory, s -> !s.isCapstone())).isEqualTo(expectedFlats);
		assertThat(countStones(whiteInventory, TakStone::isCapstone)).isEqualTo(expectedCaps);
		assertThat(countStones(blackInventory, s -> !s.isCapstone())).isEqualTo(expectedFlats);
		assertThat(countStones(blackInventory, TakStone::isCapstone)).isEqualTo(expectedCaps);
	}

	@Data
	List<Tuple.Tuple3<Integer, Integer, Integer>> inventories() {
		return List.of(
				Tuple.of(3, 10, 0),
				Tuple.of(4, 15, 0),
				Tuple.of(5, 21, 1),
				Tuple.of(6, 30, 1),
				Tuple.of(7, 40, 2),
				Tuple.of(8, 50, 2)
		);
	}

	private int countStones(List<TakStone> inventory, Predicate<TakStone> condition) {
		return (int) inventory.stream().filter(condition).count();
	}

}
