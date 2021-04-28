package kz.capitalpay.server.paysystems.systems.halykbank.dto;

public class OrderDTO {

    Long id;
    Double amount;
    String orderDate;
//    Integer reportCount;
//    Double dataVolume;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

//    public Integer getReportCount() {
//        return reportCount;
//    }
//
//    public void setReportCount(Integer reportCount) {
//        this.reportCount = reportCount;
//    }
//
//    public Double getDataVolume() {
//        return dataVolume;
//    }
//
//    public void setDataVolume(Double dataVolume) {
//        this.dataVolume = dataVolume;
//    }
}
