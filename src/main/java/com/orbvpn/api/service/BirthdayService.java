package com.orbvpn.api.service;

import com.orbvpn.api.config.sms.TwilioConfig;
import com.orbvpn.api.domain.entity.UserProfile;
import com.orbvpn.api.reposiitory.UserProfileRepository;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

@Service
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class BirthdayService {

    private final UserProfileRepository userProfileRepository;
    private final TwilioConfig twilioConfig;
    private final EmailService emailService;
    private final MailProperties mailProperties;

    @PostConstruct
    public List<UserProfile> loadUsers() {
        List<UserProfile> userListByBirthDate = userProfileRepository.findByBirthDate(LocalDate.now());
        return userListByBirthDate;
    }

    @Scheduled(cron = "0 8 * * * *") // runs every 8:00 am every day and sends birthday SMS to user if they have.
    public void sendBirthdayWishSms() {
        List<UserProfile> users = loadUsers(); // gives users whose birthday is today
        for (UserProfile user : users) {
            MessageCreator creator = Message.creator(new PhoneNumber(user.getPhone()),
                    new PhoneNumber(twilioConfig.getTrialNumber()), "Happy Birthday " +
                            user.getFirstName() + " stay blessed !!");
            creator.create(); // sends the message
        }
    }

    @Scheduled(cron = "0 8 * * * *") // runs every 8:00 am every day and sends birthday Email to user if they have.
    public void sendBirthdayWishEmail() {
        List<UserProfile> users = loadUsers(); // gives users whose birthday is today
        for (UserProfile user : users) {
            emailService.sendMail(mailProperties.getUsername(), user.getUser().getEmail(), "Happy Birth Day",
                    "Happy your birth day "+ user.getFirstName() +", We at NDB wish you all the best in life.");
        }
    }
}
