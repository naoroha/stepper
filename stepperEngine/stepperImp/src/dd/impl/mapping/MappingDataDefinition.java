package dd.impl.mapping;

import dd.api.AbstractDataDefinition;

import java.util.Map;

public class MappingDataDefinition extends AbstractDataDefinition {
    public MappingDataDefinition(){
        super("Mapping",false, Map.class);
    }
}
