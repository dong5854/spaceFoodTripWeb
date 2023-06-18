package com.triplenova.spacefoodtrip.web;

import com.triplenova.spacefoodtrip.service.JwtService;
import com.triplenova.spacefoodtrip.service.UserService;
import com.triplenova.spacefoodtrip.web.dto.UserRequestDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("login")
    public String login(@RequestBody UserRequestDto requestDto, HttpServletResponse response) throws AuthenticationException, IOException {
        if (userService.isValid(requestDto)) {
            String accessToken = jwtService.createAccessToken(requestDto.email());
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            jwtService.setAccessTokenHeader(response, accessToken);
            return "OK";
        }
        return "FAIL";
    }

    @PostMapping("sign-up")
    public String signUp(@RequestBody UserRequestDto requestDto, HttpServletResponse response) {
        if (userService.save(requestDto)) {
            response.setStatus(HttpServletResponse.SC_OK);
            return "OK";
        }
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return "FAIL";
    }
}
