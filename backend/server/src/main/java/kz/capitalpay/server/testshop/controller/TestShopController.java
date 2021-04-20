package kz.capitalpay.server.testshop.controller;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/testshop", produces = "application/json;charset=UTF-8")
public class TestShopController {

    Logger logger = LoggerFactory.getLogger(TestShopController.class);

    @Autowired
    Gson gson;



}
