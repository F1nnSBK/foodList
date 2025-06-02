package com.foodlist.service.controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping(path = "/health")
    public HttpStatusCode healthCheck() {
        return HttpStatusCode.valueOf(200);
    }

}
