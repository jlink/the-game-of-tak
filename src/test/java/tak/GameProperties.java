package tak;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

@Label("Game of Tak")
class GameProperties {

	@Property
	void new_game_in_all_possible_sizes(@ForAll @IntRange(min = 3, max = 8) int size) {
		GameOfTak game = new GameOfTak(size);
		assertThat(game.size()).isEqualTo(size);
		assertThat(game.board()).isEqualTo(TakBoard.ofSize(size));
	}

}
