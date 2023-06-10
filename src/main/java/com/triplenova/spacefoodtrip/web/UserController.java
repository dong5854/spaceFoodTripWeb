package com.triplenova.spacefoodtrip.web;

import com.triplenova.spacefoodtrip.web.dto.UserRequestDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @PostMapping("login")
    public String login(@RequestBody UserRequestDto requestDto) {
        return requestDto.toString();
    }
}
