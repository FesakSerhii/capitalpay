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
import kz.capitalpay.server.paymentlink.dto.CreateLinkWithLinkDto;
import kz.capitalpay.server.paymentlink.dto.CreatePaymentCreationLinkDto;
import kz.capitalpay.server.paymentlink.dto.CreatePaymentLinkDto;
import kz.capitalpay.server.paymentlink.mapper.PaymentLinkMapper;
import kz.capitalpay.server.paymentlink.model.EditPaymentCreationLinkDto;
import kz.capitalpay.server.paymentlink.model.PaymentCreationLink;
import kz.capitalpay.server.paymentlink.model.PaymentLink;
import kz.capitalpay.server.paymentlink.repository.PaymentCreationLinkRepository;
import kz.capitalpay.server.paymentlink.repository.PaymentLinkRepository;
import kz.capitalpay.server.payments.service.PaymentService;
import kz.capitalpay.server.service.SendEmailService;
import kz.capitalpay.server.util.QrCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

import static kz.capitalpay.server.constants.ErrorDictionary.BILL_ID_ALREADY_EXISTS;
import static kz.capitalpay.server.constants.ErrorDictionary.BILL_ID_IS_TOO_LONG;

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
    private final PaymentCreationLinkRepository paymentCreationLinkRepository;
    private final PaymentService paymentService;

    @Value("${remote.api.addres}")
    private String apiAddress;
    @Value("${payment.link.creation.url}")
    private String merchantPaymentUrl;


    public PaymentLinkService(PaymentLinkRepository paymentLinkRepository, QrCodeUtil qrCodeUtil, SendEmailService sendEmailService, FileStorageService fileStorageService, ObjectMapper objectMapper, PaymentLinkMapper paymentLinkMapper, ApplicationUserService applicationUserService, CashboxService cashboxService, PaymentCreationLinkRepository paymentCreationLinkRepository, PaymentService paymentService) {
        this.paymentLinkRepository = paymentLinkRepository;
        this.qrCodeUtil = qrCodeUtil;
        this.sendEmailService = sendEmailService;
        this.fileStorageService = fileStorageService;
        this.objectMapper = objectMapper;
        this.paymentLinkMapper = paymentLinkMapper;
        this.applicationUserService = applicationUserService;
        this.cashboxService = cashboxService;
        this.paymentCreationLinkRepository = paymentCreationLinkRepository;
        this.paymentService = paymentService;
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
            paymentLink.setCreateDate(LocalDateTime.now());
            paymentLink.setMerchantEmail(dto.getMerchantEmail());
            paymentLink.setPayerEmail(dto.getPayerEmail());
            paymentLink.setValidHours(dto.getValidHours());
            if (Objects.nonNull(dto.getFileIds())) {
                paymentLink.setFileIds(dto.getFileIds().toString());
            }
            Cashbox cashbox = cashboxService.findById(dto.getCashBoxId());
            if (Objects.isNull(cashbox)) {
                return ErrorDictionary.CASHBOX_NOT_FOUND;
            }
            ResultDTO checkPaymentLinkError = checkPaymentLink(dto.getBillId(), cashbox);
            if (Objects.nonNull(checkPaymentLinkError)) {
                return checkPaymentLinkError;
            }
            paymentLink.setMerchantId(cashbox.getMerchantId());
            paymentLink.setValidTill(LocalDateTime.now().plusHours(dto.getValidHours()));
            paymentLinkRepository.save(paymentLink);

            String link = apiAddress + "/payment/simple/pay-with-link/" + paymentLink.getGuid();
            sendPaymentLinkEmail(paymentLink, link);
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("link", link);
            resultMap.put("qrCode", qrCodeUtil.generateQrCode(link));
            return new ResultDTO(true, resultMap, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO createPaymentLink(CreateLinkWithLinkDto dto) {
        try {
            PaymentCreationLink creationLink = paymentCreationLinkRepository.findByIdAndDeletedFalse(dto.getCreationLinkId()).orElse(null);
            if (Objects.isNull(creationLink)) {
                return ErrorDictionary.LINK_NOT_FOUND;
            }
            Cashbox cashbox = cashboxService.findById(creationLink.getCashBoxId());
            if (Objects.isNull(cashbox)) {
                return ErrorDictionary.CASHBOX_NOT_FOUND;
            }
            ResultDTO checkPaymentLinkError = checkPaymentLink(dto.getBillId(), cashbox);
            if (Objects.nonNull(checkPaymentLinkError)) {
                return checkPaymentLinkError;
            }
            ApplicationUser user = applicationUserService.findById(cashbox.getMerchantId());
            if (Objects.isNull(user)) {
                return ErrorDictionary.USER_NOT_FOUND;
            }

            PaymentLink paymentLink = new PaymentLink();
            paymentLink.setGuid(UUID.randomUUID().toString());
            paymentLink.setBillId(dto.getBillId());
            paymentLink.setDescription(dto.getDescription());
            paymentLink.setTotalAmount(dto.getTotalAmount());
            paymentLink.setEmailText(dto.getEmailText());
            paymentLink.setMerchantName(user.getRealname());
            paymentLink.setEmailTitle(dto.getEmailTitle());
            paymentLink.setCashBoxId(cashbox.getId());
            paymentLink.setCreateDate(LocalDateTime.now());
            paymentLink.setMerchantEmail(user.getEmail());
            paymentLink.setPayerEmail(dto.getPayerEmail());
            paymentLink.setValidHours(dto.getValidHours());
            if (Objects.nonNull(dto.getFileIds())) {
                paymentLink.setFileIds(dto.getFileIds().toString());
            }
            paymentLink.setMerchantId(cashbox.getMerchantId());
            paymentLink.setValidTill(LocalDateTime.now().plusHours(dto.getValidHours()));
            paymentLinkRepository.save(paymentLink);

            String link = apiAddress + "/payment/simple/pay-with-link/" + paymentLink.getGuid();
            if (Objects.nonNull(dto.getPayerEmail())) {
                paymentLink.setEmailTitle("Ссылка на оплату от " + creationLink.getCompanyName());
                paymentLink.setEmailText("");
                sendPaymentLinkEmail(paymentLink, link);
            }
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

    public ResultDTO renewPaymentLink(String linkId) {
        PaymentLink paymentLink = findById(linkId);
        if (Objects.isNull(paymentLink)) {
            return ErrorDictionary.LINK_NOT_FOUND;
        }
        if (paymentLink.isSuccessfulPayment()) {
            return ErrorDictionary.ALREADY_PAID;
        }
        paymentLink.setValidTill(LocalDateTime.now().plusHours(paymentLink.getValidHours()));
        save(paymentLink);
        String link = apiAddress + "/payment/simple/pay-with-link/" + paymentLink.getGuid();
        sendPaymentLinkEmail(paymentLink, link);
        return new ResultDTO(true, "SUCCESS", 0);
    }

    public ResultDTO createPaymentCreationLink(CreatePaymentCreationLinkDto dto) {
        try {
            if (paymentCreationLinkRepository.existsByCashBoxIdAndDeletedFalse(dto.getCashBoxId())) {
                return ErrorDictionary.CREATION_LINK_ALREADY_EXISTS;
            }
            PaymentCreationLink creationLink = new PaymentCreationLink();
            creationLink.setCashBoxId(dto.getCashBoxId());
            creationLink.setId(UUID.randomUUID().toString());
            creationLink.setCompanyName(dto.getCompanyName());
            creationLink.setContactPhone(dto.getContactPhone());
            Cashbox cashbox = cashboxService.findById(dto.getCashBoxId());
            if (Objects.isNull(cashbox)) {
                return ErrorDictionary.CASHBOX_NOT_FOUND;
            }
            paymentCreationLinkRepository.save(creationLink);
            String link = merchantPaymentUrl + creationLink.getId();
            String qr = qrCodeUtil.generateQrCode(link);
            return new ResultDTO(true, paymentLinkMapper.toCreationLinkResponseDto(creationLink, link, qr), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    public ResultDTO getPaymentCreationLink(Long cashBoxId) {
        PaymentCreationLink creationLink = paymentCreationLinkRepository.findByCashBoxIdAndDeletedFalse(cashBoxId).orElse(null);
        if (Objects.isNull(creationLink)) {
            return ErrorDictionary.LINK_NOT_FOUND;
        }
        String link = merchantPaymentUrl + creationLink.getId();
        String qr = qrCodeUtil.generateQrCode(link);
        return new ResultDTO(true, paymentLinkMapper.toCreationLinkResponseDto(creationLink, link, qr), 0);
    }

    public ResultDTO getPaymentCreationLinkPublicInfo(String id) {
        PaymentCreationLink creationLink = paymentCreationLinkRepository.findByIdAndDeletedFalse(id).orElse(null);
        if (Objects.isNull(creationLink)) {
            return ErrorDictionary.LINK_NOT_FOUND;
        }
        return new ResultDTO(true, paymentLinkMapper.toCreationLinkPublicInfoDto(creationLink), 0);
    }

    public ResultDTO deletePaymentCreationLink(Long cashBoxId) {
        PaymentCreationLink creationLink = paymentCreationLinkRepository.findByCashBoxIdAndDeletedFalse(cashBoxId).orElse(null);
        if (Objects.isNull(creationLink)) {
            return ErrorDictionary.LINK_NOT_FOUND;
        }
        creationLink.setDeleted(true);
        paymentCreationLinkRepository.save(creationLink);
        return new ResultDTO(true, "SUCCESS", 0);
    }

    public ResultDTO editPaymentCreationLink(EditPaymentCreationLinkDto dto) {
        try {
            PaymentCreationLink creationLink = paymentCreationLinkRepository.findByIdAndDeletedFalse(dto.getId()).orElse(null);
            if (Objects.isNull(creationLink)) {
                return ErrorDictionary.LINK_NOT_FOUND;
            }
            creationLink.setContactPhone(dto.getContactPhone());
            creationLink.setCompanyName(dto.getCompanyName());
            paymentCreationLinkRepository.save(creationLink);
            String link = merchantPaymentUrl + creationLink.getId();
            String qr = qrCodeUtil.generateQrCode(link);
            return new ResultDTO(true, paymentLinkMapper.toCreationLinkResponseDto(creationLink, link, qr), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }

    private void sendPaymentLinkEmail(PaymentLink paymentLink, String link) {
        List<Long> fileIds;
        try {
            fileIds = objectMapper.readValue(paymentLink.getFileIds(), new TypeReference<List<Long>>() {
            });
        } catch (Exception e) {
            fileIds = new ArrayList<>();
        }
        List<FileStorage> files = fileStorageService.getFilListById(fileIds);
        sendEmailService.sendEmailWithFiles(paymentLink.getPayerEmail(), paymentLink.getEmailTitle(), paymentLink.getEmailText() + "\n\n" + link, files);
    }

    private ResultDTO checkPaymentLink(String billId, Cashbox cashbox) {
        if (billId.length() > 31) {
            return BILL_ID_IS_TOO_LONG;
        }
        if (!paymentService.checkUnique(cashbox, billId)) {
            return BILL_ID_ALREADY_EXISTS;
        }
        return null;
    }
}
