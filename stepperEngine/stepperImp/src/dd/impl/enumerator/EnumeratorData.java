package dd.impl.enumerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EnumeratorData{
        public static  Set<String> enumeratorSet=new HashSet<>();

        public EnumeratorData(HashSet<String> strings) {
            enumeratorSet=strings;
        }

        public boolean containsString(String targetValue) {
            //for (Set<String> categorySet : enumeratorSetMap.values()) {
            return enumeratorSet.contains(targetValue);
        }
}
