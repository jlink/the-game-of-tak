package tak;

import java.util.*;

public abstract class Move {

	public static Move place(final Player player, final Stone stone, final Spot spot) {
		return new PlacingMove(player, stone, spot);
	}

	protected final Player player;

	protected Move(final Player player) {
		this.player = player;
	}

	public abstract Result execute(final Position position, final GameOfTak.Status beforeStatus);

	protected Optional<GameOfTak.Status> checkGameFinished(final Position newPosition) {
		return new PositionFinishedChecker().check(newPosition);
	}

	public abstract String toPTN();

	@Override
	public String toString() {
		return String.format("Player(%s): %s", player, toPTN());
	}

	public class Result {
		private final Position newPosition;
		private final GameOfTak.Status newStatus;

		Result(final Position newPosition, final GameOfTak.Status newStatus) {

			this.newPosition = newPosition;
			this.newStatus = newStatus;
		}

		public GameOfTak.Status status() {
			return newStatus;
		}

		public Position position() {
			return newPosition;
		}
	}
}

