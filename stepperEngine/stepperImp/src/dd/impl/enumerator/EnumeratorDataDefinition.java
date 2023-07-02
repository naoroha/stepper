package dd.impl.enumerator;

import dd.api.AbstractDataDefinition;

import java.io.File;

public class EnumeratorDataDefinition extends AbstractDataDefinition {


    public EnumeratorDataDefinition() {
        super("Enumerator", true, EnumeratorData.class);
    }
}
