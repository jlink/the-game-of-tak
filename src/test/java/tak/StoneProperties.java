package tak;

import tak.testingSupport.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

@TakDomain
class StoneProperties {

	@Property
	void noStoneCanBeCapstoneAndFlat(@ForAll Stone stone) {
		assertThat(isValid(stone)).isTrue();
	}

	@Property
	void flattening(@ForAll Stone stone) {
		if (stone.isCapstone() || !stone.isStanding()) {
			assertThatThrownBy(() -> stone.flatten()).isInstanceOf(TakException.class);
		} else {
			assertThat(stone.flatten().isStanding()).isFalse();
		}
	}

	@Property
	void puttingUp(@ForAll Stone stone) {
		if (stone.isStanding()) {
			assertThatThrownBy(() -> stone.standUp()).isInstanceOf(TakException.class);
		} else {
			assertThat(stone.standUp().isStanding()).isTrue();
		}
	}

	@Property
	void ptn(@ForAll Stone stone) {
		if (stone.isCapstone()) {
			assertThat(stone.toPTN()).isEqualTo("C");
		} else if (stone.isStanding()) {
			assertThat(stone.toPTN()).isEqualTo("S");
		} else {
			assertThat(stone.toPTN()).isEqualTo("F");
		}
	}

	@Property
	void canBuildRoad(@ForAll Stone stone) {
		if (stone.isCapstone()) {
			assertThat(stone.canBuildRoad()).isTrue();
		} else if (stone.isStanding()) {
			assertThat(stone.canBuildRoad()).isFalse();
		} else {
			assertThat(stone.canBuildRoad()).isTrue();
		}
	}

	private boolean isValid(final Stone stone) {
		if (stone.isCapstone() && !stone.isStanding()) {
			return false;
		}
		return true;
	}
}
