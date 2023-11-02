package com.example.propagation.ddd.controller;

import com.example.propagation.ddd.dto.SupportDTO;
import com.example.propagation.ddd.service.SupportService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

@Controller
public class RestController {

    final SupportService supportService;

    public RestController(SupportService supportService) {
        this.supportService = supportService;
    }

    @ResponseBody
    @RequestMapping(
            value = "/add/support",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<SupportDTO> saveSupport(@RequestBody(required = true) final SupportDTO supportDTO) {
        SupportDTO dto = supportService.save(supportDTO);
        return Mono.just(dto);
    }

}
