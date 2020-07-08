package tak;

import java.util.*;

import tak.testingSupport.*;

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

		Position startingPosition = game.position();
		assertThat(startingPosition.board()).isEqualTo(Board.ofSize(size));
		assertThat(startingPosition.nextToMove()).isEqualTo(Player.WHITE);

		assertThat(startingPosition.playerInventory(Player.WHITE)).isNotEmpty();
		assertThat(startingPosition.playerInventory(Player.BLACK)).isNotEmpty();
	}

	@Property
	@FromData("inventories")
	void initial_inventory(@ForAll int boardSize, @ForAll int expectedFlats, @ForAll int expectedCaps) {
		Position startingPosition = new GameOfTak(boardSize).position();

		List<Stone> whiteInventory = startingPosition.playerInventory(Player.WHITE);
		List<Stone> blackInventory = startingPosition.playerInventory(Player.BLACK);
		assertThat(whiteInventory).hasSameSizeAs(blackInventory);
		assertThat(whiteInventory).allMatch(stone -> stone.colour() == Stone.Colour.WHITE);
		assertThat(blackInventory).allMatch(stone -> stone.colour() == Stone.Colour.BLACK);

		assertThat(TakTestingSupport.count(whiteInventory, s -> !s.isCapstone())).isEqualTo(expectedFlats);
		assertThat(TakTestingSupport.count(whiteInventory, Stone::isCapstone)).isEqualTo(expectedCaps);
		assertThat(TakTestingSupport.count(blackInventory, s -> !s.isCapstone())).isEqualTo(expectedFlats);
		assertThat(TakTestingSupport.count(blackInventory, Stone::isCapstone)).isEqualTo(expectedCaps);
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

}
