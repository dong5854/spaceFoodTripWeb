package com.triplenova.spacefoodtrip.service;

import com.triplenova.spacefoodtrip.web.dto.UserRequestDto;

import javax.naming.AuthenticationException;

public interface UserService {
    boolean isValid(UserRequestDto userDto) throws AuthenticationException;
    boolean save(UserRequestDto userRequestDto);
}
