package com.orbvpn.api.service;

import com.orbvpn.api.domain.entity.SmsRequest;
import com.orbvpn.api.domain.entity.UserProfile;
import com.orbvpn.api.reposiitory.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class BirthdayService {
    private static final String EVERY_DAY_8AM = "0 0 8 * * ?";

    private final UserProfileRepository userProfileRepository;
    private final EmailService emailService;
    private final MailProperties mailProperties;
    private final SmsService smsService;

    @Scheduled(cron = EVERY_DAY_8AM)
    public void sendBirthdayWishSms() {
        log.info("sending birthday sms...");
        List<UserProfile> users = userProfileRepository.findUsersBornToday();
        for (UserProfile user : users) {
            SmsRequest smsRequest = new SmsRequest(user.getPhone(),
                    "Happy birthday" + user.getFirstName() + "!\nMay you live your dreams.\n" +
                            "Your friends at Orb Vpn team, NDB Inc.");
            smsService.sendRequest(smsRequest);
        }
    }

    @Scheduled(cron = EVERY_DAY_8AM)
    public void sendBirthdayWishEmail() {
        log.info("sending birthday email...");
        List<UserProfile> users = userProfileRepository.findUsersBornToday();
        for (UserProfile user : users) {
            String emailHtml =
                    "<!DOCTYPE html>\n" +
                            "<html lang=\"en\">\n" +
                            "<head>\n" +
                            "    <meta charset=\"UTF-8\">\n" +
                            "    <title>ORB Net - Happy Birthday</title>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "<h2>Happy birthday, " + user.getFirstName() + "!</h2>\\n" +
                            "From your friends at <b>NDB</b>,\\n" +
                            "We wish you a fabulous birthday celebration.\\n" +
                            "May the days ahead of you be filled with prosperity, great health and above all joy in its " +
                            "truest and purest form. \\n" +
                            "Orb Vpn Team | NDB Inc." +
                            "<img src=\"public/OrbNET.fld/happyBirthday.jpg\">\n" +
                            "</body>\n" +
                            "</html>";
            emailService.sendMail(mailProperties.getUsername(), user.getUser().getEmail(), "Happy Birthday",
                    emailHtml);
        }
    }
}
