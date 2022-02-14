package com.orbvpn.api.controller;

import com.orbvpn.api.domain.dto.AuthenticatedUser;
import com.orbvpn.api.domain.enums.SocialMedia;
import com.orbvpn.api.service.social_login.OauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class OAuthController {

    @Autowired
    private OauthService oauthService;

    @GetMapping("/oauth2/callback/google")
    public AuthenticatedUser google(@RequestParam("code") String code) {
        return this.oauthService.getTokenAndLogin(code, SocialMedia.GOOGLE);
    }

    @GetMapping("/oauth2/callback/facebook")
    public void facebook(@RequestParam("code") String code) {
        this.oauthService.getTokenAndLogin(code, SocialMedia.FACEBOOK);
    }

    @GetMapping("/oauth2/callback/linkedin")
    public AuthenticatedUser linkedIn(@RequestParam("code") String code) {
        return this.oauthService.getTokenAndLogin(code, SocialMedia.LINKEDIN);
    }

    @GetMapping("/oauth2/authorization/manual/twitter")
    public void twitterOauthLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizeUrl = oauthService.twitterOauthLogin();
        response.sendRedirect(authorizeUrl);
    }

    @GetMapping("/oauth2/callback/twitter")
    public AuthenticatedUser getTwitter(HttpServletRequest request, HttpServletResponse response) {
        return oauthService.twitterUserProfile(request, response);
    }


}
