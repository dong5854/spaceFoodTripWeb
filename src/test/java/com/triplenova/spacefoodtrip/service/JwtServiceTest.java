package com.triplenova.spacefoodtrip.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.triplenova.spacefoodtrip.domain.user.User;
import com.triplenova.spacefoodtrip.domain.user.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class JwtServiceTest {
    private static final String ACCESS_TOKEN_SUBJECT = "Access Token";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";
    @Autowired
    JwtService jwtService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager em;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.header}")
    private String accessHeader;

    private String userEmail = "userEmail";
    private String userName = "userName";

    @BeforeEach
    public void init() {
        User user = User.builder().email(userEmail).name(userName).password("1234567890").build();
        userRepository.save(user);
        clear();
    }

    private void clear() {
        em.flush();
        em.clear();
    }

    private DecodedJWT getVerify(String token) {
        return JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
    }

    @Test
    public void createAccessToken_AccessToken_발급() throws Exception {
        //given, when
        String accessToken = jwtService.createAccessToken(userEmail);

        DecodedJWT verify = getVerify(accessToken);

        String subject = verify.getSubject();
        String findUserEmail = verify.getClaim(EMAIL_CLAIM).asString();

        //then
        assertThat(findUserEmail).isEqualTo(userEmail);
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
    }

    @Test
    public void setAccessTokenHeader_AccessToken_헤더_설정() throws Exception {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(userEmail);

        jwtService.setAccessTokenHeader(mockHttpServletResponse, accessToken);


        //when
        jwtService.sendAccessToken(mockHttpServletResponse,accessToken);

        //then
        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);

        assertThat(headerAccessToken).isEqualTo(accessToken);
    }


    @Test
    public void sendToken_토큰_전송() throws Exception {
        //given
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        String accessToken = jwtService.createAccessToken(userEmail);

        //when
        jwtService.sendAccessToken(mockHttpServletResponse,accessToken);


        //then
        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);

        assertThat(headerAccessToken).isEqualTo(accessToken);
    }

    private HttpServletRequest setRequest(String accessToken) throws IOException {

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        jwtService.sendAccessToken(mockHttpServletResponse,accessToken);

        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

        httpServletRequest.addHeader(accessHeader, BEARER+headerAccessToken);

        return httpServletRequest;
    }

    @Test
    public void extractAccessToken_AccessToken_추출() throws Exception {
        //given
        String accessToken = jwtService.createAccessToken(userEmail);
        HttpServletRequest httpServletRequest = setRequest(accessToken);

        //when
        String extractAccessToken = jwtService.extractAccessToken(httpServletRequest).orElseThrow(
                () -> new Exception("extractAccessToken 에러")
        );


        //then
        assertThat(extractAccessToken).isEqualTo(accessToken);
        assertThat(getVerify(extractAccessToken).getClaim(EMAIL_CLAIM).asString()).isEqualTo(userEmail);
    }

    @Test
    public void extractUserEmail_UserEmail_추출() throws Exception {
        //given
        String accessToken = jwtService.createAccessToken(userEmail);
        HttpServletRequest httpServletRequest = setRequest(accessToken);

        String requestAccessToken = jwtService.extractAccessToken(httpServletRequest).orElseThrow(
                ()-> new Exception("extractAccessToken 에러")
        );

        //when
        String extractUserEmail = jwtService.extractUserEmail(requestAccessToken);


        //then
        assertThat(extractUserEmail).isEqualTo(userEmail);
    }

    @Test
    public void 토큰_유효성_검사() throws Exception {
        //given
        String accessToken = jwtService.createAccessToken(userEmail);

        //when, then
        assertThat(jwtService.isValid(accessToken)).isTrue();
        assertThat(jwtService.isValid(accessToken+"d")).isFalse();

    }

}
