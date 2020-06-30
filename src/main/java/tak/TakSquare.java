package tak;

import java.util.*;

public class TakSquare {

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return true;
	}

	@Override
	public int hashCode() {
		return 42;
	}

	public boolean isEmpty() {
		return true;
	}
}
