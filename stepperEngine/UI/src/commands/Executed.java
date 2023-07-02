package commands;

import flow.execution.api.FlowExecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Executed {
    private ExecutorService executorService;
    private Map<String,FlowExecution> executedFlowsList;
    private Map<String, CompletableFuture<Void>> flowIdToTaskMap;
    public ExecutorService getExecutorService() {
    return executorService;
    }

    public void setExecutorService(int threadPoolSize) {
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    public Executed(){
        this.executedFlowsList=new HashMap<>();
        flowIdToTaskMap=new HashMap<>();
    }

    public void setExecutedFlowsList(Map<String,FlowExecution> executedFlowsList) {
        this.executedFlowsList = executedFlowsList;
    }

    public void setFlowIdToTask(String uniqueId, CompletableFuture<Void> task) {
        this.flowIdToTaskMap.put(uniqueId,task);
    }

    public void addExecutedFlow(FlowExecution executed){
        executedFlowsList.put(executed.getUniqueId(),executed);
    }

    public List<FlowExecution> getExecutedFlowsList() {
        return new ArrayList<>(executedFlowsList.values());
    }
    public FlowExecution getFlowExecutionByUniqueId(String ID){
        for(FlowExecution flowExecution:executedFlowsList.values()){
            if(flowExecution.getUniqueId().equals(ID)){
                return flowExecution;
            }
        }
        return null;
    }
    public void rerunTask(String taskId, Callable<Void> task) {
        CompletableFuture<Void> future = flowIdToTaskMap.get(taskId);
        if (future != null && !future.isDone()) {
            stopTask(taskId);
            CompletableFuture<Void> newFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    task.call();
                    return null;
                } catch (Exception e) {
                    throw new CompletionException(e);
                }
            }, executorService);
            flowIdToTaskMap.put(taskId,newFuture);
        }

    }

    public void stopTask(String taskId) {
        CompletableFuture<Void> future = flowIdToTaskMap.remove(taskId);
        if (future != null) {
            future.cancel(true);
        }
    }

    public void stopAllTasks() {
        for (Future<?> future : flowIdToTaskMap.values()) {
            future.cancel(true);
        }
        flowIdToTaskMap.clear();
    }
}
