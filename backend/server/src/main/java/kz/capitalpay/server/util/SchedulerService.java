package kz.capitalpay.server.util;


import kz.capitalpay.server.help.model.SupportRequest;
import kz.capitalpay.server.help.service.SupportService;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.service.SendEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
public class SchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerService.class);

    private final SendEmailService sendEmailService;
    private final SupportService supportService;
    private final ApplicationUserService applicationUserService;

    public SchedulerService(SendEmailService sendEmailService, SupportService supportService, ApplicationUserService applicationUserService) {
        this.sendEmailService = sendEmailService;
        this.supportService = supportService;
        this.applicationUserService = applicationUserService;
    }


    @Scheduled(fixedDelayString = "PT1H")
    public void checkPayments() {
        List<SupportRequest> requests = supportService.findAllByEmailMessageSentFalse();
        requests.forEach(request -> {
            LOGGER.info("Send email for supprorRequest with id: {}", request.getId());
            ApplicationUser applicationUser = applicationUserService.getUserById(request.getAuthorId());
            sendEmailService.sendMail(applicationUser.getEmail(), "CapitalPay", String.format("Ваше обращение в службу поддержки принято.<br/>" +
                    "Ему присвоен номер: %s <br/>", request.getId()));
            request.setEmailMessageSent(true);
        });
        supportService.saveAllSupportRequests(requests);
    }

}
