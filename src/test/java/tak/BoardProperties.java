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
	void emptyBoardOfSize3() {
		TakBoard board = TakBoard.ofSize(3);
		assertThat(board.at(position('a', 1)).isEmpty()).isTrue();
		assertThat(board.at(position('c', 3)).isEmpty()).isTrue();

		assertThatThrownBy(() -> board.at(position('d', 4))).isInstanceOf(TakException.class);
	}
}
