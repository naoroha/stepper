package commands;


import java.time.Duration;

public class Statistics {
    private int howManyTimes;

    private Duration totalDuration;

    public Statistics(){
        this.howManyTimes=0;
        this.totalDuration=Duration.ZERO;
    }

    public Duration getTotalDuration() {
        return totalDuration;
    }

    public int getHowManyTimes() {
        return howManyTimes;
    }

    public void setHowManyTimes(int howManyTimes) {
        this.howManyTimes = howManyTimes;
    }

    public void setTotalDuration(Duration totalDuration) {
        this.totalDuration = totalDuration;
    }
}
