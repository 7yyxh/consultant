package com.yyxh.consultant.controller;

import com.yyxh.consultant.service.ConsultantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


@RestController
public class ChatController {

    @Autowired
    private ConsultantService consultantService;

    @GetMapping(value = "/chat",produces = "text/html;charset=utf-8")
    public Flux<String> chatStreaming(@RequestParam("memoryId") String memoryId,@RequestParam("message")String message) {
        return consultantService.chatStreaming(memoryId,message);
    }

//    @GetMapping(value = "/chat",produces = "text/html;charset=utf-8")
//    public String chat(@RequestParam("message") String message) {
//        return consultantService.chat(message);
//    }


}
