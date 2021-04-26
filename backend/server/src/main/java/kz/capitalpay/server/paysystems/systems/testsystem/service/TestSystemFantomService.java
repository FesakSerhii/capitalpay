package kz.capitalpay.server.paysystems.systems.testsystem.service;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestSystemFantomService {

    Logger logger = LoggerFactory.getLogger(TestSystemFantomService.class);

    @Autowired
    Gson gson;



}
