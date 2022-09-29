package kz.capitalpay.server.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.dto.SendSmsDTO;
import kz.capitalpay.server.login.model.PendingPhone;
import kz.capitalpay.server.login.repository.PendingPhoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.ProxySelector;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class SendSmsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendSmsService.class);

    @Autowired
    Gson gson;

    @Autowired
    SendEmailService sendEmailService;

    @Autowired
    PendingPhoneRepository pendingPhoneRepository;

    @Value("${sms.send.url}")
    String smsSendUrl;

    @Value("${sms.send.login}")
    String smsSendLogin;

    @Value("${sms.send.psw}")
    String smsSendPassword;

    @Value("${sms.callback.url}")
    String smsCallbackUrl;

    @Autowired
    RestTemplate restTemplate;

    public boolean sendSms(String phone, String text) {
        try {
            if (phone.contains("+77")) {
                String mes = URLEncoder.encode(text, StandardCharsets.UTF_8.toString()).replace("+", "%20");

                LOGGER.info(mes);
                String url = smsSendUrl +
                        "?login=" + smsSendLogin +
                        "&psw=" + smsSendPassword +
                        "&phones=" + phone.replace("+", "") +
                        "&mes=" + mes +
                        "&charset=utf-8";
                LOGGER.info(url);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(url))
                        .GET()
                        .build();

                HttpResponse<String> response = HttpClient
                        .newBuilder()
                        .proxy(ProxySelector.getDefault())
                        .build()
                        .send(request, HttpResponse.BodyHandlers.ofString());

                LOGGER.info(response.body());
                return true;
            } else {
                PendingPhone pendingPhone = pendingPhoneRepository.findTopByPhone(phone);
                sendEmailService.sendMail(pendingPhone.getEmail(),
                        "SMS Capital Pay", text);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ResultDTO sendNewSms(SendSmsDTO request) {
        try {
            if (sendSms(request.getPhone(), request.getText())) {
                return new ResultDTO(true, "SMS sended", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResultDTO(false, "Oops!", -1);
    }
}
