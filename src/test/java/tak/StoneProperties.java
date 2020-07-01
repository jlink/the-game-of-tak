package tak;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;

import static org.assertj.core.api.Assertions.*;

@Domain(TakDomain.class)
@Domain(DomainContext.Global.class)
class StoneProperties {

	@Property
	void noStoneCanBeCapstoneAndFlat(@ForAll TakStone stone) {
		assertThat(isValid(stone)).isTrue();
	}

	@Property
	void flattening(@ForAll TakStone stone) {
		if (stone.isCapstone() || !stone.isStanding()) {
			assertThatThrownBy(() -> stone.flatten()).isInstanceOf(TakException.class);
		} else {
			assertThat(stone.flatten().isStanding()).isFalse();
		}
	}

	@Property
	void puttingUp(@ForAll TakStone stone) {
		if (stone.isStanding()) {
			assertThatThrownBy(() -> stone.standUp()).isInstanceOf(TakException.class);
		} else {
			assertThat(stone.standUp().isStanding()).isTrue();
		}
	}

	private boolean isValid(final TakStone stone) {
		if (stone.isCapstone() && !stone.isStanding()) {
			return false;
		}
		return true;
	}
}
