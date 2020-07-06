package tak;

import tak.testingSupport.*;

import net.jqwik.api.*;

import static tak.testingSupport.TakAssertions.*;

class MovingTests {

	@Example
	void gameSize3_initialPlacing() {
		GameOfTak gameSize3 = new GameOfTak(3);

		TakMove placeOnA1 = TakMove.place(
				TakPlayer.WHITE,
				TakStone.flat(TakStone.Colour.BLACK),
				TakBoard.spot('a', 1)
		);
		gameSize3.makeMove(placeOnA1);

		assertThat(gameSize3.moves()).containsExactly(placeOnA1);
		assertThat(gameSize3.status()).isEqualTo(GameOfTak.Status.PRELUDE_BLACK);

		TakPosition position = gameSize3.position();
		assertThat(position.board().at(TakBoard.spot('a', 1))).isNotEmpty();
		assertThat(TakTestingSupport.count(position.inventory(TakPlayer.BLACK), s -> !s.isCapstone())).isEqualTo(9);
	}
}
