package tak;

import tak.testingSupport.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static tak.testingSupport.TakAssertions.*;

@TakDomain
class MovingTests {

	@Property
	void initialPlacing(@ForAll @IntRange(min = 3, max = 8) int size) {
		GameOfTak game = new GameOfTak(size);

		int flatsBefore = countInventoryFlats(game.position(), TakPlayer.BLACK);
		TakStone stoneToPlace = TakStone.flat(TakStone.Colour.BLACK);
		TakMove placeOnA1 = TakMove.place(
				TakPlayer.WHITE,
				stoneToPlace,
				TakBoard.spot('a', 1)
		);
		game.makeMove(placeOnA1);

		assertThat(game.moves()).containsExactly(placeOnA1);
		assertThat(game.status()).isEqualTo(GameOfTak.Status.PRELUDE_BLACK);

		TakPosition position = game.position();
		assertThat(position.board().at(TakBoard.spot('a', 1))).hasStack(stoneToPlace);
		assertThat(countInventoryFlats(position, TakPlayer.BLACK)).isEqualTo(flatsBefore - 1);
		assertThat(position.nextToMove()).isEqualTo(TakPlayer.BLACK);
	}

	@Property
	void finishPrelude(@ForAll @IntRange(min = 3, max = 8) int size) {
		GameOfTak game = new GameOfTak(size);

		TakMove placeOnA1 = TakMove.place(
				TakPlayer.WHITE,
				TakStone.flat(TakStone.Colour.BLACK),
				TakBoard.spot('a', 1)
		);
		game.makeMove(placeOnA1);

		int flatsBefore = countInventoryFlats(game.position(), TakPlayer.WHITE);
		TakStone stoneToPlace = TakStone.flat(TakStone.Colour.WHITE);
		TakMove placeOnC3 = TakMove.place(
				TakPlayer.BLACK,
				stoneToPlace,
				TakBoard.spot('c', 3)
		);
		game.makeMove(placeOnC3);

		assertThat(game.moves()).containsExactly(placeOnA1, placeOnC3);
		assertThat(game.status()).isEqualTo(GameOfTak.Status.WHITE_TO_MOVE);

		TakPosition position = game.position();
		assertThat(position.board().at(TakBoard.spot('c', 3))).hasStack(stoneToPlace);
		assertThat(countInventoryFlats(position, TakPlayer.WHITE)).isEqualTo(flatsBefore - 1);
		assertThat(position.nextToMove()).isEqualTo(TakPlayer.WHITE);
	}

	private int countInventoryFlats(final TakPosition position, final TakPlayer player) {
		return TakTestingSupport.count(position.playerInventory(player), s -> !s.isCapstone());
	}
}
