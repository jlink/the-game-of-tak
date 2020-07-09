package tak.testingSupport;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.TYPE_USE})
public @interface EmptyBoard {
	boolean value() default true;
}
