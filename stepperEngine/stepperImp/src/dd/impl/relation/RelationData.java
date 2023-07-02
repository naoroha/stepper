package dd.impl.relation;

import java.util.*;
import java.util.stream.Collectors;

public class RelationData {
    private List<String> columns;
    private List<SingleRow> rows;

    public RelationData(List<String> columns) {
        this.columns = columns;
        rows = new ArrayList<>();
    }

    public List<String> getColumns(){return columns;}
    public List<String> getRowDataByColumnsOrder(int rowId) {
        return rowId >= 0 && rowId < rows.size()
                ? rows.get(rowId).getData().values().stream().collect(Collectors.toList())
                : Collections.emptyList();
    }
    public void addColumn(String name){
        columns.add(name);
    }
    public List<SingleRow> getRows(){return rows;}
    public String getRowColumnData(int id,String column){
        return rows.get(id).getData().get(column);
    }

    public void addDatas(List<String> values){
        SingleRow row=new SingleRow();
        for(int i=0;i<columns.size();i++){
            row.addData(columns.get(i), values.get(i));
        }
        rows.add(row);
    }
    private static class SingleRow {
        private Map<String, String> data;

        public SingleRow() {
            data = new HashMap<>();
        }

        public void addData(String columnName, String value) {
            data.put(columnName, value);
        }

        public Map<String, String> getData() {
            return data;
        }
    }
}