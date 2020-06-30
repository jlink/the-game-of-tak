package tak;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

public class TakBoardProvider implements ArbitraryProvider {

	@Override
	public boolean canProvideFor(final TypeUsage targetType) {
		return targetType.isOfType(TakBoard.class);
	}

	@Override
	public Set<Arbitrary<?>> provideFor(final TypeUsage targetType, final SubtypeProvider subtypeProvider) {
		return Collections.singleton(
				Arbitraries.integers().between(TakBoard.MIN_SIZE, TakBoard.MAX_SIZE).map(TakBoard::ofSize)
		);
	}

}
