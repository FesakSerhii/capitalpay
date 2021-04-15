package kz.capitalpay.server.currency.service;

import com.google.gson.Gson;
import kz.capitalpay.server.currency.model.SystemCurrency;
import kz.capitalpay.server.currency.repository.CurrencyRepository;
import kz.capitalpay.server.dto.ResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyService {

    Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    @Autowired
    Gson gson;

    @Autowired
    CurrencyRepository currencyRepository;

    public ResultDTO systemList() {
        try{
            List<SystemCurrency> currencyList = currencyRepository.findAll();
            if(currencyList==null || currencyList.size()==0){
                SystemCurrency currency = new SystemCurrency();
                currency.setAlpha("USD");
                currency.setName("US Dollar");
                currency.setNumber("840");
                currency.setUnicode("$");
                currencyRepository.save(currency);
                currencyList.add(currency);
            }
            return new ResultDTO(true,currencyList,0);
        }catch (Exception e){
            e.printStackTrace();
            return new ResultDTO(false,e.getMessage(),-1);
        }
    }
}
