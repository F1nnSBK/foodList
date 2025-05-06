package com.foodlist.service.controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping(path = "/")
    public HttpStatusCode healthCheck() {
        return HttpStatusCode.valueOf(200);
    }

}
