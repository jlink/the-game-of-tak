package tak;

import java.util.*;

import tak.testingSupport.*;

import net.jqwik.api.*;

import static tak.testingSupport.TakAssertions.*;

@TakDomain
@Group
class MovingProperties {

	@Group
	class Prelude {
		@Property
		void initialPlacing(@ForAll @Game(isNew = true) GameOfTak game) {
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
		void finishPrelude(@ForAll @Game(isNew = true) GameOfTak game) {
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
			assertThat(game.status()).isEqualTo(GameOfTak.Status.ONGOING);

			TakPosition position = game.position();
			assertThat(position.board().at(TakBoard.spot('c', 3))).hasStack(stoneToPlace);
			assertThat(countInventoryFlats(position, TakPlayer.WHITE)).isEqualTo(flatsBefore - 1);
			assertThat(position.nextToMove()).isEqualTo(TakPlayer.WHITE);
		}

		@Property
		void onlyFlatStonesCanBePlacedInPrelude(@ForAll @Game(isNew = true) GameOfTak game) {
			TakMove placeCapstone = TakMove.place(
					TakPlayer.WHITE,
					TakStone.capstone(TakStone.Colour.BLACK),
					TakBoard.spot('a', 1)
			);
			assertThatThrownBy(() -> game.makeMove(placeCapstone)).isInstanceOf(TakException.class);

			TakMove placeStandingStone = TakMove.place(
					TakPlayer.WHITE,
					TakStone.flat(TakStone.Colour.BLACK).standUp(),
					TakBoard.spot('a', 1)
			);
			assertThatThrownBy(() -> game.makeMove(placeStandingStone)).isInstanceOf(TakException.class);

			assertThat(game.moves()).hasSize(0);
		}

		@Property
		void cannotPlaceOnOccupiedSpot(@ForAll @Game(isNew = true) GameOfTak game) {
			TakMove placeFirst = TakMove.place(
					TakPlayer.WHITE,
					TakStone.flat(TakStone.Colour.BLACK),
					TakBoard.spot('a', 1)
			);
			game.makeMove(placeFirst);

			TakMove placeOnSameSpot = TakMove.place(
					TakPlayer.BLACK,
					TakStone.flat(TakStone.Colour.WHITE),
					TakBoard.spot('a', 1)
			);

			assertThatThrownBy(() -> game.makeMove(placeOnSameSpot)).isInstanceOf(TakException.class);

			assertThat(game.moves()).hasSize(1);

		}
	}

	@Group
	class StandardPlacings {

		@Property
		void takingTurns(@ForAll Tuple.Tuple2<@Game(isNew = true) GameOfTak, List<TakBoard.Spot>> gameAndSpots) {
			GameOfTak game = gameAndSpots.get1();
			List<TakBoard.Spot> originalSpots = gameAndSpots.get2();
			ArrayList<TakBoard.Spot> spots = new ArrayList<>(originalSpots);
			playPrelude(game, spots.remove(0), spots.remove(0));

			for (TakBoard.Spot spot : spots) {
				TakPlayer player = game.position().nextToMove();
				TakMove move = TakMove.place(player, TakStone.flat(player.stoneColour()), spot);
				game.makeMove(move);
				assertThat(game.status()).isEqualTo(GameOfTak.Status.ONGOING);
			}

			assertThat(game.moves()).hasSize(originalSpots.size());
		}

		private void playPrelude(final GameOfTak game, final TakBoard.Spot first, final TakBoard.Spot second) {
			TakMove whitePrelude = TakMove.place(TakPlayer.WHITE, TakStone.flat(TakStone.Colour.BLACK), first);
			TakMove blackPrelude = TakMove.place(TakPlayer.BLACK, TakStone.flat(TakStone.Colour.WHITE), second);
			game.makeMove(whitePrelude);
			game.makeMove(blackPrelude);
		}
	}

	@Property
	// TODO: Generate any valid game. Currently this test only tests new games
	void onlyPlayerAtTurnCanPlay(@ForAll @Game(isNew = true) GameOfTak game) {
		TakPlayer wrongPlayer = game.position().nextToMove().opponent();
		TakMove wrongPlayerMove = TakMove.place(
				wrongPlayer,
				TakStone.flat(TakStone.Colour.BLACK),
				TakBoard.spot('a', 1)
		);
		assertThatThrownBy(() -> game.makeMove(wrongPlayerMove)).isInstanceOf(TakException.class);

		assertThat(game.moves()).hasSize(0);
	}

	private int countInventoryFlats(final TakPosition position, final TakPlayer player) {
		return TakTestingSupport.count(position.playerInventory(player), s -> !s.isCapstone());
	}
}
