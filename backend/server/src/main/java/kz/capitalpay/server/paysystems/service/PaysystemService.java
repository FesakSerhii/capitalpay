package kz.capitalpay.server.paysystems.service;

import com.google.gson.Gson;
import kz.capitalpay.server.dto.ResultDTO;
import kz.capitalpay.server.paysystems.model.Paysystem;
import kz.capitalpay.server.paysystems.repository.PaysystemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaysystemService {

    Logger logger = LoggerFactory.getLogger(PaysystemService.class);

    @Autowired
    Gson gson;

    @Autowired
    PaysystemRepository paysystemRepository;


    public ResultDTO paysystemList() {
        try {
            List<Paysystem> paysystemList = paysystemRepository.findAll();
            if (paysystemList == null || paysystemList.size() == 0) {
                paysystemList = new ArrayList<>();
                Paysystem paysystem = new Paysystem();
                paysystem.setName("Test PaySystem");
                paysystem.setEnabled(true);
                paysystemRepository.save(paysystem);
                paysystemList.add(paysystem);
            }

            return new ResultDTO(true, paysystemList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(false, e.getMessage(), -1);
        }
    }
}
