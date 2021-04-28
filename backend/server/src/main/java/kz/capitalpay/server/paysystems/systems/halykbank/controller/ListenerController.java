package kz.capitalpay.server.paysystems.systems.halykbank.controller;

import kz.capitalpay.server.paysystems.systems.halykbank.sevice.HalykService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ListenerController {

Logger logger = LoggerFactory.getLogger(ListenerController.class);

@Autowired
    HalykService halykService;

    @PostMapping("/halykbank/listener")
    @ResponseBody
    String setPaymentResult(@RequestParam String response, @RequestParam String appendix) {
        logger.info("Payment result: ");
//        response = "%3Cdocument%3E%3Cbank+name%3D%22Halyk+Saving+Bank+JSC%22+bik%3D%22HSBKKZKX%22%3E%3Ccustomer+name%3D%22ENY+MAN%22+mail%3D%22SeFrolov%40kkb.kz%22+phone%3D%22%22%3E%3Cmerchant+cert_id%3D%2200C182B189%22+name%3D%22test+shop%22%3E%3Corder+order_id%3D%221599040532589%22+amount%3D%2227.36%22+currency%3D%22398%22%3E%3Cdepartment+merchant_id%3D%2292061101%22+amount%3D%2227.36%22%2F%3E%3C%2Forder%3E%3C%2Fmerchant%3E%3Cmerchant_sign+type%3D%22RSA%22%2F%3E%3C%2Fcustomer%3E%3Ccustomer_sign+type%3D%22RSA%22%2F%3E%3Cresults+timestamp%3D%222020-09-02+15%3A58%3A19%22%3E%3Cpayment+merchant_id%3D%2292061101%22+card%3D%22440564-XX-XXXX-6150%22+amount%3D%2227.36%22+reference%3D%22200902155819%22+approval_code%3D%22155819%22+response_code%3D%2200%22+Secure%3D%22No%22+card_bin%3D%22%22+c_hash%3D%2213988BBF7C6649F799F36A4808490A3E%22%2F%3E%3C%2Fresults%3E%3C%2Fbank%3E%3Cbank_sign+cert_id%3D%2200c183d690%22+type%3D%22SHA%2FRSA%22%3EVSrgsZpR7eoK5h4LjyMzH3eXk8jRfAqtyNMzBK536YBMGl21PSzfsJpaRYy%2BXdkFZOC3zWnei6WGKYQLGpJX2ynugOls6FgJKhfpoOiIOYhpcwsjSZkY1Lbk5q2g4qLEGDEj5O6JGlaZYlZexrWnoUFY%2BufYus%2BxBIeQZb6%2BAxU%3D%3C%2Fbank_sign%3E%3C%2Fdocument%3E";
//        appendix = "PGRvY3VtZW50PjxpdGVtIG51bWJlcj0iMSIgbmFtZT0i0KLQtdC70LXRhNC+0L3QvdGL0Lkg0LDQv9C/0LDRgNCw0YIiIHF1YW50aXR5PSIyIiBhbW91bnQ9IjEwMDAiLz48aXRlbSBudW1iZXI9IjIiIG5hbWU9ItCo0L3Rg9GAIDLQvC4iIHF1YW50aXR5PSIyIiBhbW91bnQ9IjIwMCIvPjwvZG9jdW1lbnQ+";
        int result = halykService.setPaySystemResponse(response, appendix);
        return String.valueOf(result);
    }

}
