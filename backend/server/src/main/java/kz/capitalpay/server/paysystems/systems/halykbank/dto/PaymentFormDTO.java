package kz.capitalpay.server.paysystems.systems.halykbank.dto;

public class PaymentFormDTO {

    String ord;
    String sum;
    String Base64Content;
    String send;
    String sendOrderActionLink;
    String postLink;
    String ticket;
    Long timestamp;

    public String getOrd() {
        return ord;
    }

    public void setOrd(String ord) {
        this.ord = ord;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getBase64Content() {
        return Base64Content;
    }

    public void setBase64Content(String base64Content) {
        Base64Content = base64Content;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }


    public String getSendOrderActionLink() {
        return sendOrderActionLink;
    }

    public void setSendOrderActionLink(String sendOrderActionLink) {
        this.sendOrderActionLink = sendOrderActionLink;
    }

    public String getPostLink() {
        return postLink;
    }

    public void setPostLink(String postLink) {
        this.postLink = postLink;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "PaymentFormDTO{" +
                "ord='" + ord + '\'' +
                ", sum='" + sum + '\'' +
                ", Base64Content='" + Base64Content + '\'' +
                ", send='" + send + '\'' +
                ", sendOrderActionLink='" + sendOrderActionLink + '\'' +
                ", postLink='" + postLink + '\'' +
                ", ticket='" + ticket + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
