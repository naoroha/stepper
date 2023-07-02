package dd.impl;

import dd.api.DataDefinition;
import dd.impl.doubledd.DoubleDataDefinition;
import dd.impl.enumerator.EnumeratorDataDefinition;
import dd.impl.file.FileDataDefinition;
import dd.impl.list.ListDataDefinition;
import dd.impl.mapping.MappingDataDefinition;
import dd.impl.number.NumberDataDefinition;
import dd.impl.relation.RelationDataDefinition;
import dd.impl.string.StringDataDefinition;

import java.util.List;

public enum DataDefinitionRegistry implements DataDefinition {
    STRING(new StringDataDefinition()),
    RELATION(new RelationDataDefinition()),
    NUMBER(new NumberDataDefinition()),
    MAPPING(new MappingDataDefinition()),
    LIST(new ListDataDefinition()),
    DOUBLE(new DoubleDataDefinition()),
    FILE(new FileDataDefinition());
    //ENUMERATOR(new EnumeratorDataDefinition(Class<?> clazz));
    private final DataDefinition data_definition;
    DataDefinitionRegistry(DataDefinition data_definition){
        this.data_definition = data_definition;
    }

    @Override
    public String getName() {
        return data_definition.getName();
    }

    @Override
    public boolean isUserFriendly() {
        return data_definition.isUserFriendly();
    }

    @Override
    public Class<?> getType() {
        return data_definition.getType();
    };
}

