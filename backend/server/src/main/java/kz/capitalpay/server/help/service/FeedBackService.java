package kz.capitalpay.server.help.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.help.dto.FeedBackDTO;
import kz.capitalpay.server.service.SendEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedBackService {

    Logger logger = LoggerFactory.getLogger(FeedBackService.class);

    String email = "arsenguzhva+888@gmail.com";

    @Autowired
    Gson gson;

    @Autowired
    SendEmailService sendEmailService;

    public ResultDTO feedBack(FeedBackDTO request) {
        try {
            String text = String.format("Наименование компании: %s <br/>\n" +
                            "Имя Фамилия: %s <br/>\n" +
                            "Номер телефона: %s <br/>\n" +
                            "Email: %s <br/>\n" +
                            "<br/>\n%s <br/>",
                    request.getCompanyName(),
                    request.getName(),
                    request.getPhone(),
                    request.getEmail(),
                    request.getText());

            logger.info(text);

            sendEmailService.sendMail(email, "CapitalPay: feedback", text);

            return new ResultDTO(true, "Mail sent", 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
