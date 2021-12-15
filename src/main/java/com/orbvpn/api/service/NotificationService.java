package com.orbvpn.api.service;

import com.orbvpn.api.domain.entity.SmsRequest;
import com.orbvpn.api.domain.entity.User;
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
public class NotificationService {
    private static final String EVERY_DAY_8AM = "0 0 8 * * ?";
    private static final Integer[] DAYS_BEFORE_EXPIRATION = new Integer[]{1, 5, 10};
    private static final Integer[] DAYS_AFTER_EXPIRATION = new Integer[]{1, 5, 10};

    private final UserProfileRepository userProfileRepository;
    private final UserSubscriptionService userSubscriptionService;

    private final EmailService emailService;
    private final MailProperties mailProperties;
    private final SmsService smsService;

    /**
     * send Sms and Email at 8 AM London time to people whose birthday are today.
     */
    @Scheduled(cron = EVERY_DAY_8AM)
    public void sendBirthdayWish() {
        log.info("sending birthday notifications...");
        String smsMessage = "Happy birthday!\nMay you live your dreams.\n";
        String emailTitle = "ORB Net - Happy Birthday!";
        String emailMessage = "From your friends at <b>NDB</b>,\n" +
                "We wish you a fabulous birthday celebration.\n" +
                "May the days ahead of you be filled with prosperity, great health and above all joy in " +
                "its truest and purest form. \n";
        List<UserProfile> users = userProfileRepository.findUsersBornToday();
        for (UserProfile user : users) {
            sendSms(user, smsMessage);
            sendEmail(user, emailTitle, emailMessage);
        }
    }

    /**
     * sending subscription expiration reminders 1, 5 and 10 day(s) before expiration at 8 AM London time.
     */
    @Scheduled(cron = EVERY_DAY_8AM)
    public void subscriptionExpirationReminder() {
        log.info("sending subscription expiration reminders (1, 5 and 10 day(s) before expiration)...");
        for (Integer dayCount : DAYS_BEFORE_EXPIRATION) {
            List<UserProfile> users = userSubscriptionService.getUsersExpireInNextDays(dayCount);
            for (UserProfile user : users) {
                String smsMessage = "There is just " + dayCount + " " +
                        (dayCount == 1 ? "day" : "days") +
                        " left until your ORB-Vpn subscription expiration.\n";
                String emailTitle = "ORB Net - Subscription Expiration Reminder";
                String emailMessage = "There is just " + dayCount + " day left until your ORB-Vpn subscription expiration.";
                sendSms(user, smsMessage);
                sendEmail(user, emailTitle, emailMessage);
            }
        }
        log.info("sent subscription expiration reminders successfully");
    }

    @Scheduled(cron = EVERY_DAY_8AM)
    public void afterSubscriptionExpiredNotification() {
        log.info("sending notification after subscription expiration after expiration...");
        for (Integer dayCount : DAYS_AFTER_EXPIRATION) {
            List<UserProfile> users = userSubscriptionService.getUsersExpireInPreviousDays(dayCount);
            for (UserProfile user : users) {
                String smsMessage = "Your subscription is finished " + dayCount + " " +
                        (dayCount == 1 ? "day" : "days") +
                        " ago. Please don't hesitate to contact us if there is any problem for your service.";
                String emailTitle = "ORB Net - Subscription Expired";
                String emailMessage = "Your subscription is finished " + dayCount + " " +
                        (dayCount == 1 ? "day" : "days") +
                        " ago. Please don't hesitate to contact us if there is any problem for your service.";
                sendSms(user, smsMessage);
                sendEmail(user, emailTitle, emailMessage);
            }
        }
        log.info("sent notification after subscription expiration successfully");
    }

    /**
     * Welcome new customers for joining ORB-VPN via SMS and Email
     *
     * @param user new created user
     */
    public void welcomingNewUsers(User user) {
        String smsMessage = "Welcome to Orb Vpn service. We provide 24/7 support for you to enjoy our service.\n";
        String emailTitle = "ORB Net - Welcome to Orb Vpn!";
        String emailMessage = "Welcome to Orb Vpn service. We provide 24/7 support for you to enjoy our service.";
        sendSms(user.getProfile(), smsMessage);
        sendEmail(user.getProfile(), emailTitle, emailMessage);
    }

    public void resetPasswordDone(User user) {
        String smsMessage = "Your password is reset successfully.\n";
        String emailTitle = "ORB Net - Password Reset";
        String emailMessage = "Your password is reset successfully by your request.";
        sendSms(user.getProfile(), smsMessage);
        sendEmail(user.getProfile(), emailTitle, emailMessage);
    }

    private void sendSms(UserProfile user, String message) {
        String name = "friend";
        if (user != null && user.getFirstName() != null) {
            name = user.getFirstName();
        }
        if (user != null && user.getPhone() != null) {
            SmsRequest smsRequest = new SmsRequest(user.getPhone(),
                    "Dear " + name + ",\n" + message);
            smsService.sendRequest(smsRequest);
        }
    }

    private void sendEmail(UserProfile user, String title, String message) {
        String name = "friend";
        if (user != null && user.getFirstName() != null) {
            name = user.getFirstName();
        }
        String emailHtml =
                "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>" + title + "</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<h2>Dear " + name + "! \n" + message + "</h2>\n" +
                        "Orb Vpn Team | NDB Inc." +
                        "</body>\n" +
                        "</html>";
        emailService.sendMail(
                mailProperties.getUsername(),
                user.getUser().getEmail(),
                title,
                emailHtml);
    }
}
