package tak.testingSupport;

import java.lang.annotation.*;

import net.jqwik.api.domains.*;

@Domain(TakDomainContext.class)
@Domain(DomainContext.Global.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface TakDomain {

}
