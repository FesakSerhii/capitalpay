package kz.capitalpay.server.paysystems.systems.halyksoap.dto;

public class RegisterPaymentsDateDTO {
    private long timestampNanoSecondsAfter;
    private long timestampNanoSecondsBefore;

    public long getTimestampNanoSecondsAfter() {
        return timestampNanoSecondsAfter;
    }

    public void setTimestampNanoSecondsAfter(long timestampNanoSecondsAfter) {
        this.timestampNanoSecondsAfter = timestampNanoSecondsAfter;
    }

    public long getTimestampNanoSecondsBefore() {
        return timestampNanoSecondsBefore;
    }

    public void setTimestampNanoSecondsBefore(long timestampNanoSecondsBefore) {
        this.timestampNanoSecondsBefore = timestampNanoSecondsBefore;
    }
}
