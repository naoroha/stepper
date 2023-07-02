package dd.impl.enumerator;

import java.util.*;

public enum Enumerator {
    OPERATION(new EnumeratorData(new HashSet<>(Arrays.asList("ZIP", "UNZIP"))));
    private final EnumeratorData enumeratorData;

    Enumerator(EnumeratorData enumeratorData) {
        this.enumeratorData=enumeratorData;
    }

    public EnumeratorData getEnumeratorData() {
        return enumeratorData;
    }
}
