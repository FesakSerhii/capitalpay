package kz.capitalpay.server.login.service;

import com.google.gson.Gson;
import kz.capitalpay.server.login.model.TrustIp;
import kz.capitalpay.server.login.repository.TrustIpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrustIpService {

    Logger logger = LoggerFactory.getLogger(TrustIpService.class);

    @Autowired
    Gson gson;

    @Autowired
    TrustIpRepository trustIpRepository;

    public String validIpAddress(Long userId, String ip) {

        List<TrustIp> trustIps = trustIpRepository.findByUserIdAndEnable(userId,true);
        if(trustIps== null || trustIps.size()==0){
            return ip;
        }else{
            for(TrustIp trustIp : trustIps){
                if(trustIp.getIp().equals(ip)){
                    return ip;
                }
            }
            return null;
        }
    }

    public void addTrustIp(Long userId, String ip) {
        TrustIp trustIp = trustIpRepository.findTopByUserIdAndIp(userId,ip);
        if(trustIp==null){
            trustIp = new TrustIp();
            trustIp.setIp(ip);
            trustIp.setUserId(userId);
            List<TrustIp> trustIps = trustIpRepository.findByUserIdAndEnable(userId,true);
            if(trustIps!=null && trustIps.size()>0){
                trustIp.setEnable(true);
            }
            trustIpRepository.save(trustIp);
        }
    }
}
