package tak;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;
import static tak.TakBoard.*;

class BoardProperties {

	@Property
	void legalBoardCoordinatesCanBeCreated(
			@ForAll @CharRange(from = 'a', to = 'h') char file,
			@ForAll @IntRange(min = 1, max = 8) int rank
	) {
		Position position = position(file, rank);
		assertThat(position.toString()).isEqualTo(String.valueOf(file) + String.valueOf(rank));
	}

	@Property
	void illegalBoardCoordinatesAreRejected(
			@ForAll char file,
			@ForAll int rank
	) {
		Assume.that(file < 'a' || file > 'h' || rank < 1 || rank > 8);
		assertThatThrownBy(() -> position(file, rank)).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void newBoard(@ForAll TakBoard board) {
		assertThat(board.size()).isBetween(3, 8);

		Position lowerLeft = position('a', 1);
		assertThat(board.at(lowerLeft).isEmpty()).isTrue();

		char highestFile = Position.files().get(board.size() - 1);
		int highestRank = Position.ranks().get(board.size() - 1);
		Position upperRight = position(highestFile, highestRank);
		assertThat(board.at(upperRight).isEmpty()).isTrue();

		if (board.size() < 8) {
			assertThatThrownBy(() -> board.at(position((char) (highestFile + 1), highestRank))).isInstanceOf(TakException.class);
			assertThatThrownBy(() -> board.at(position(highestFile, highestRank + 1))).isInstanceOf(TakException.class);
		}
	}
}
