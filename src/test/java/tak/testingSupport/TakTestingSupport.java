package tak.testingSupport;

import java.util.*;
import java.util.function.*;

public class TakTestingSupport {
	public static <T> int count(Collection<T> collection, Predicate<T> condition) {
		return Math.toIntExact(collection.stream().filter(condition).count());
	}
}
