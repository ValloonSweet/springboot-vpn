package com.orbvpn.api.controller;

import com.orbvpn.api.domain.dto.TwitterUserInfo;
import com.orbvpn.api.service.OauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
public class OAuthController {

    @Autowired
    private OauthService oauthService;

    @GetMapping("/oauth2/callback/google")
    public String google(@AuthenticationPrincipal OAuth2User principal) {
        return "Google Callback - " + principal.toString();
    }

    @GetMapping("/oauth2/callback/facebook")
    public String facebook(@AuthenticationPrincipal OAuth2User principal) {
        return "Facebook Callback - " + principal.toString();
    }

    @GetMapping("/oauth2/callback/linkedin")
    public String linkedIn(@AuthenticationPrincipal OAuth2User principal) {
        return "LinkedIn Callback - " + principal.toString();
    }

    @RequestMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }

    @GetMapping("/oauth2/authorization/manual/twitter")
    public void twitterOauthLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizeUrl = oauthService.twitterOauthLogin();
        response.sendRedirect( authorizeUrl );
    }

    @GetMapping("/oauth2/callback/twitter")
    public TwitterUserInfo getTwitter(HttpServletRequest request, HttpServletResponse response) {
        return oauthService.twitterUserProfile(request, response);
    }


}
