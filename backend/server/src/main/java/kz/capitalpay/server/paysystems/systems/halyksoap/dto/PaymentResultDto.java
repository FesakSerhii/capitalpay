package kz.capitalpay.server.paysystems.systems.halyksoap.dto;

import kz.capitalpay.server.dto.ResultDTO;

public class PaymentResultDto {
    private boolean is3ds;
    private ResultDTO resultDTO;
    private String strFor3ds;

    public PaymentResultDto(boolean is3ds, ResultDTO resultDTO) {
        this.is3ds = is3ds;
        this.resultDTO = resultDTO;
    }

    public PaymentResultDto(boolean is3ds, ResultDTO resultDTO, String strFor3ds) {
        this.is3ds = is3ds;
        this.resultDTO = resultDTO;
        this.strFor3ds = strFor3ds;
    }

    public boolean isIs3ds() {
        return is3ds;
    }

    public void setIs3ds(boolean is3ds) {
        this.is3ds = is3ds;
    }

    public ResultDTO getResultDTO() {
        return resultDTO;
    }

    public void setResultDTO(ResultDTO resultDTO) {
        this.resultDTO = resultDTO;
    }

    public String getStrFor3ds() {
        return strFor3ds;
    }

    public void setStrFor3ds(String strFor3ds) {
        this.strFor3ds = strFor3ds;
    }
}
