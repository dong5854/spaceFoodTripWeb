package com.triplenova.spacefoodtrip.service;

import com.triplenova.spacefoodtrip.domain.user.User;
import com.triplenova.spacefoodtrip.domain.user.UserRepository;
import com.triplenova.spacefoodtrip.web.dto.UserRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(UserRequestDto userRequestDto) throws AuthenticationException {
        User user = userRepository.findByEmail(userRequestDto.email()).orElseThrow(AuthenticationException::new);
        return user.getPassword().equals(userRequestDto.pw());
    }

    public boolean save(UserRequestDto userRequestDto) {
        User user = User.builder().email(userRequestDto.email()).password(userRequestDto.pw()).name(userRequestDto.name()).build();
        return userRepository.save(user).equals(user);
    }
}
