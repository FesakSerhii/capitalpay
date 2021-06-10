package kz.capitalpay.server.paysystems.systems.halyksoap.dto;

public class RegisterPaymentsDateDTO {
    private long timestampAfter;
    private long timestampBefore;

    public long getTimestampAfter() {
        return timestampAfter;
    }

    public void setTimestampAfter(long timestampAfter) {
        this.timestampAfter = timestampAfter;
    }

    public long getTimestampBefore() {
        return timestampBefore;
    }

    public void setTimestampBefore(long timestampBefore) {
        this.timestampBefore = timestampBefore;
    }
}
