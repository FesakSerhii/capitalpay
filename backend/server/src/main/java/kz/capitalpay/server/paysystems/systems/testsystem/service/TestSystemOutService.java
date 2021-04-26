package kz.capitalpay.server.paysystems.systems.testsystem.service;

import com.google.gson.Gson;
import kz.capitalpay.server.eventlog.service.SystemEventsLogsService;
import kz.capitalpay.server.merchantsettings.service.CashboxSettingsService;
import kz.capitalpay.server.payments.model.Payment;
import kz.capitalpay.server.paysystems.systems.testsystem.TestSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static kz.capitalpay.server.eventlog.service.SystemEventsLogsService.NOTIFY_CLIENT;
import static kz.capitalpay.server.merchantsettings.service.CashboxSettingsService.INTERACTION_URL;

@Service
public class TestSystemOutService {

    Logger logger = LoggerFactory.getLogger(TestSystemOutService.class);

    @Autowired
    CashboxSettingsService cashboxSettingsService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SystemEventsLogsService systemEventsLogsService;


    @Autowired
    TestSystem testSystem;

    @Autowired
    Gson gson;

//    public boolean notifyClientAboutStatusChange(Payment payment) {
//        try {
//
//            String interactionUrl = cashboxSettingsService.getField(payment.getCashboxId(), INTERACTION_URL);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<Payment> request =
//                    new HttpEntity<>(payment, headers);
//
//            ResponseEntity<String> response =
//                    restTemplate.postForEntity(interactionUrl,
//                            request, String.class);
//
//            logger.info(response.getStatusCode().toString());
//            if (response.hasBody()) {
//                logger.info(response.getBody());
//            }
//
//            systemEventsLogsService.addNewPaysystemAction(testSystem.getComponentName(),
//                    NOTIFY_CLIENT, gson.toJson(payment), payment.getMerchantId().toString());
//
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

}
