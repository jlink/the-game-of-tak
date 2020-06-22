package tak;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

@Label("Game of Tak")
class GameTests {

	@Property
	void new_game_in_all_possible_sizes(@ForAll @IntRange(min = 3, max = 8) int size) {
		GameOfTak game = new GameOfTak(size);
		Assertions.assertThat(game.size()).isEqualTo(size);
	}
}
