package kz.capitalpay.server.help.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.help.dto.FeedBackDTO;
import kz.capitalpay.server.service.SendEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedBackService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedBackService.class);

    String email = "info@capitalpay.kz";

    @Autowired
    Gson gson;

    @Autowired
    SendEmailService sendEmailService;

    public ResultDTO feedBack(FeedBackDTO request) {
        try {
            String text = (Objects.nonNull(request.getCompanyName()) && !request.getCompanyName().isEmpty() ? "Наименование компании: " + request.getCompanyName() + "<br/>\n" : "")
            + (Objects.nonNull(request.getName()) && !request.getName().isEmpty() ? "Имя Фамилия: " + request.getName() + "<br/>\n" : "")
            + (Objects.nonNull(request.getActivity()) && !request.getActivity().isEmpty() ? "Вид деятельности: " + request.getActivity() + "<br/>\n" : "")
                    + (Objects.nonNull(request.getPhone()) && !request.getPhone().isEmpty() ? "Номер телефона: " + request.getPhone() + " <br/>\n" : "")
                    + (Objects.nonNull(request.getEmail()) && !request.getEmail().isEmpty() ? "Email: " + request.getEmail() + " <br/>\n" : "")
                    + (Objects.nonNull(request.getText()) && !request.getText().isEmpty() ? "<br/>\n" + request.getText() + " <br/>" : "");

            LOGGER.info(text);
            sendEmailService.sendMail(email, "CapitalPay: feedback", text);
            return new ResultDTO(true, "Mail sent", 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
