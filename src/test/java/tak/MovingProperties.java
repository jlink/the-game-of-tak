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
			int flatsBefore = countInventoryFlats(game.position(), Player.BLACK);
			Stone stoneToPlace = Stone.flat(Stone.Colour.BLACK);
			Move placeOnA1 = Move.place(
					Player.WHITE,
					stoneToPlace,
					Spot.of('a', 1)
			);
			game.makeMove(placeOnA1);

			assertThat(game.moves()).containsExactly(placeOnA1);
			assertThat(game.status()).isEqualTo(GameOfTak.Status.PRELUDE_BLACK);

			Position position = game.position();
			assertThat(position.board().at(Spot.of('a', 1))).hasStack(stoneToPlace);
			assertThat(countInventoryFlats(position, Player.BLACK)).isEqualTo(flatsBefore - 1);
			assertThat(position.nextToMove()).isEqualTo(Player.BLACK);
		}

		@Property
		void finishPrelude(@ForAll @Game(isNew = true) GameOfTak game) {
			Move placeOnA1 = Move.place(
					Player.WHITE,
					Stone.flat(Stone.Colour.BLACK),
					Spot.of('a', 1)
			);
			game.makeMove(placeOnA1);

			int flatsBefore = countInventoryFlats(game.position(), Player.WHITE);
			Stone stoneToPlace = Stone.flat(Stone.Colour.WHITE);
			Move placeOnC3 = Move.place(
					Player.BLACK,
					stoneToPlace,
					Spot.of('c', 3)
			);
			game.makeMove(placeOnC3);

			assertThat(game.moves()).containsExactly(placeOnA1, placeOnC3);
			assertThat(game.status()).isEqualTo(GameOfTak.Status.ONGOING);

			Position position = game.position();
			assertThat(position.board().at(Spot.of('c', 3))).hasStack(stoneToPlace);
			assertThat(countInventoryFlats(position, Player.WHITE)).isEqualTo(flatsBefore - 1);
			assertThat(position.nextToMove()).isEqualTo(Player.WHITE);
		}

		@Property
		void onlyFlatStonesCanBePlacedInPrelude(@ForAll @Game(isNew = true) GameOfTak game) {
			Move placeCapstone = Move.place(
					Player.WHITE,
					Stone.capstone(Stone.Colour.BLACK),
					Spot.of('a', 1)
			);
			assertThatThrownBy(() -> game.makeMove(placeCapstone)).isInstanceOf(TakException.class);

			Move placeStandingStone = Move.place(
					Player.WHITE,
					Stone.flat(Stone.Colour.BLACK).standUp(),
					Spot.of('a', 1)
			);
			assertThatThrownBy(() -> game.makeMove(placeStandingStone)).isInstanceOf(TakException.class);

			assertThat(game.moves()).hasSize(0);
		}

		@Property
		void cannotPlaceOnOccupiedSpot(@ForAll @Game(isNew = true) GameOfTak game) {
			Move placeFirst = Move.place(
					Player.WHITE,
					Stone.flat(Stone.Colour.BLACK),
					Spot.of('a', 1)
			);
			game.makeMove(placeFirst);

			Move placeOnSameSpot = Move.place(
					Player.BLACK,
					Stone.flat(Stone.Colour.WHITE),
					Spot.of('a', 1)
			);

			assertThatThrownBy(() -> game.makeMove(placeOnSameSpot)).isInstanceOf(TakException.class);

			assertThat(game.moves()).hasSize(1);

		}
	}

	@Group
	class StandardPlacings {

		// Does shrinking work with stateful games?
		@Property
		void takingTurns(@ForAll Tuple.Tuple2<@Game(isNew = true) GameOfTak, List<Spot>> gameAndSpots) {
			GameOfTak game = gameAndSpots.get1();
			List<Spot> originalSpots = gameAndSpots.get2();
			ArrayList<Spot> spots = new ArrayList<>(originalSpots);
			playPrelude(game, spots.remove(0), spots.remove(0));

			int countMadeMoves = 2; // Prelude is 2 moves
			for (Spot spot : spots) {
				Player player = game.position().nextToMove();
				Move move = Move.place(player, Stone.flat(player.stoneColour()), spot);
				game.makeMove(move);
				countMadeMoves++;
				if (game.status().isFinished()) {
					break;
				}
			}

			assertThat(game.moves()).hasSize(countMadeMoves);
		}

		private void playPrelude(final GameOfTak game, final Spot first, final Spot second) {
			Move whitePrelude = Move.place(Player.WHITE, Stone.flat(Stone.Colour.BLACK), first);
			Move blackPrelude = Move.place(Player.BLACK, Stone.flat(Stone.Colour.WHITE), second);
			game.makeMove(whitePrelude);
			game.makeMove(blackPrelude);
		}
	}

	@Property
	// TODO: Generate any valid game. Currently this test only tests new games
	void onlyPlayerAtTurnCanPlay(@ForAll @Game(isNew = true) GameOfTak game) {
		Player wrongPlayer = game.position().nextToMove().opponent();
		Move wrongPlayerMove = Move.place(
				wrongPlayer,
				Stone.flat(Stone.Colour.BLACK),
				Spot.of('a', 1)
		);
		assertThatThrownBy(() -> game.makeMove(wrongPlayerMove)).isInstanceOf(TakException.class);

		assertThat(game.moves()).hasSize(0);
	}

	@Group
	class Winning {

		@Example
		void whiteWinsThroughRoad() {
			GameOfTak gameOfTak = new GameOfTak(3);
			gameOfTak.makeMove(Move.place(
					Player.WHITE, Stone.flat(Stone.Colour.BLACK), Spot.of('c', 3)
			));
			gameOfTak.makeMove(Move.place(
					Player.BLACK, Stone.flat(Stone.Colour.WHITE), Spot.of('a', 1)
			));
			gameOfTak.makeMove(Move.place(
					Player.WHITE, Stone.flat(Stone.Colour.WHITE), Spot.of('b', 1)
			));
			gameOfTak.makeMove(Move.place(
					Player.BLACK, Stone.flat(Stone.Colour.BLACK), Spot.of('b', 3)
			));
			gameOfTak.makeMove(Move.place(
					Player.WHITE, Stone.flat(Stone.Colour.WHITE), Spot.of('c', 1)
			));
			assertThat(gameOfTak.isFinished()).isTrue();
			assertThat(gameOfTak.status()).isEqualTo(GameOfTak.Status.ROAD_WIN_WHITE);
			assertThat(gameOfTak.status().toPTN()).isEqualTo("R-0");
		}

	}

	private int countInventoryFlats(final Position position, final Player player) {
		return TakTestingSupport.count(position.playerInventory(player), s -> !s.isCapstone());
	}
}
