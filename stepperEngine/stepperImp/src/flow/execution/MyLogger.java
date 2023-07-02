package flow.execution;

import step.api.StepResult;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MyLogger {
    private List<Message> messages;
    private StepResult stepResult;
    private Duration duration;

    public MyLogger() {
        this.messages = new ArrayList<>();
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(String message) {
        messages.add(new Message(message));

    }
    public String getMessageByIndex(int index){
        return this.messages.get(index).getMessage();
    }
    public void sortByInstant(){
        messages.sort(Comparator.comparing(Message::getTime));
    }
    public void setDuration(Duration duration){
        this.duration=duration;
    }

    public void setDurationByInstant() {
        if(stepResult==StepResult.FAILURE)
            this.duration=Duration.ZERO;
        else
            this.duration=Duration.between(messages.get(0).getTime(),messages.get(messages.size()-1).getTime());
    }

    public Duration getDuration() {
        return duration;
    }

    public StepResult getStepResult() {
        return stepResult;
    }
    public String printedMessage() {
        StringBuilder sb = new StringBuilder();
        for (Message message : messages) {
            sb.append("[time: ");
            ZonedDateTime zonedDateTime = message.getTime().atZone(ZoneId.of("UTC"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
            String formattedDateTime = formatter.format(zonedDateTime);
            sb.append(formattedDateTime);
            sb.append("] ");
            sb.append(message.getMessage());
            sb.append("\n");
        }
        return sb.toString();
    }

    public void setStepResult(StepResult result){
        this.stepResult=result;
    }


    private class Message {
        String message;
        Instant time;

        public Message(String message) {
            this.message = message;
            this.time=Instant.now();
        }
        public String getMessage(){
            return message;
        }

        public Instant getTime() {
            return time;
        }
    }
}
