package kz.capitalpay.server.paymentlink.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.capitalpay.server.cashbox.model.Cashbox;
import kz.capitalpay.server.cashbox.service.CashboxService;
import kz.capitalpay.server.constants.ErrorDictionary;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.files.model.FileStorage;
import kz.capitalpay.server.files.service.FileStorageService;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.service.ApplicationUserService;
import kz.capitalpay.server.paymentlink.dto.CreatePaymentLinkDto;
import kz.capitalpay.server.paymentlink.mapper.PaymentLinkMapper;
import kz.capitalpay.server.paymentlink.model.PaymentLink;
import kz.capitalpay.server.paymentlink.repository.PaymentLinkRepository;
import kz.capitalpay.server.service.SendEmailService;
import kz.capitalpay.server.util.QrCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PaymentLinkService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentLinkService.class);

    private final PaymentLinkRepository paymentLinkRepository;
    private final QrCodeUtil qrCodeUtil;
    private final SendEmailService sendEmailService;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;
    private final PaymentLinkMapper paymentLinkMapper;
    private final ApplicationUserService applicationUserService;
    private final CashboxService cashboxService;

    @Value("${remote.api.addres}")
    private String apiAddress;

    public PaymentLinkService(PaymentLinkRepository paymentLinkRepository, QrCodeUtil qrCodeUtil, SendEmailService sendEmailService, FileStorageService fileStorageService, ObjectMapper objectMapper, PaymentLinkMapper paymentLinkMapper, ApplicationUserService applicationUserService, CashboxService cashboxService) {
        this.paymentLinkRepository = paymentLinkRepository;
        this.qrCodeUtil = qrCodeUtil;
        this.sendEmailService = sendEmailService;
        this.fileStorageService = fileStorageService;
        this.objectMapper = objectMapper;
        this.paymentLinkMapper = paymentLinkMapper;
        this.applicationUserService = applicationUserService;
        this.cashboxService = cashboxService;
    }

    public ResultDTO createPaymentLink(CreatePaymentLinkDto dto) {
        try {
            PaymentLink paymentLink = new PaymentLink();
            paymentLink.setGuid(UUID.randomUUID().toString());
            paymentLink.setBillId(dto.getBillId());
            paymentLink.setDescription(dto.getDescription());
            paymentLink.setTotalAmount(dto.getTotalAmount());
            paymentLink.setEmailText(dto.getEmailText());
            paymentLink.setMerchantName(dto.getMerchantName());
            paymentLink.setEmailTitle(dto.getEmailTitle());
            paymentLink.setCashBoxId(dto.getCashBoxId());
            paymentLink.setMerchantEmail(dto.getMerchantEmail());
            paymentLink.setPayerEmail(dto.getPayerEmail());
            paymentLink.setFileIds(dto.getFileIds().toString());
            Cashbox cashbox = cashboxService.findById(dto.getCashBoxId());
            if (Objects.isNull(cashbox)) {
                return ErrorDictionary.CASHBOX_NOT_FOUND;
            }
            paymentLink.setMerchantId(cashbox.getMerchantId());
            paymentLink.setValidTill(LocalDateTime.now().plusHours(dto.getValidHours()));
            paymentLinkRepository.save(paymentLink);

            List<Long> fileIds;
            try {
                fileIds = objectMapper.readValue(paymentLink.getFileIds(), new TypeReference<List<Long>>() {
                });
            } catch (Exception e) {
                fileIds = new ArrayList<>();
            }
            List<FileStorage> files = fileStorageService.getFilListById(fileIds);
            String link = apiAddress + "/payment/simple/pay-with-link/" + paymentLink.getGuid();
            sendEmailService.sendEmailWithFiles(paymentLink.getPayerEmail(), paymentLink.getEmailTitle(), paymentLink.getEmailText() + "\n\n" + link, files);
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("link", link);
            resultMap.put("qrCode", qrCodeUtil.generateQrCode(link));
            return new ResultDTO(true, resultMap, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public PaymentLink findById(String id) {
        return paymentLinkRepository.findById(id).orElse(null);
    }

    public PaymentLink save(PaymentLink paymentLink) {
        return paymentLinkRepository.save(paymentLink);
    }

    public void disablePaymentLink(String id) {
        PaymentLink paymentLink = findById(id);
        if (Objects.isNull(paymentLink)) {
            return;
        }
        paymentLink.setValidTill(LocalDateTime.now());
        paymentLink.setSuccessfulPayment(true);
        save(paymentLink);
    }

    public ResultDTO getList(Principal principal) {
        try {
            ApplicationUser owner = applicationUserService.getUserByLogin(principal.getName());
            List<PaymentLink> paymentLinks = paymentLinkRepository.findAllByMerchantId(owner.getId());
            return new ResultDTO(true, paymentLinks.stream().map(paymentLinkMapper::toPaymentLinkResponseDto), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
