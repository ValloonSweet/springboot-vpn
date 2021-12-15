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
    private static final Integer[] DAY_COUNTS_FOR_EXPIRATION_NOTIFICATION = new Integer[]{1, 5, 10};

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
        List<UserProfile> users = userProfileRepository.findUsersBornToday();
        for (UserProfile user : users) {
            /**
             * SMS
             */
            SmsRequest smsRequest = new SmsRequest(user.getPhone(),
                    "Happy birthday" + user.getFirstName() + "!\nMay you live your dreams.\n" +
                            "Your friends at Orb Vpn team, NDB Inc.");
            smsService.sendRequest(smsRequest);

            /**
             * Email
             */
            String emailHtml =
                    "<!DOCTYPE html>\n" +
                            "<html lang=\"en\">\n" +
                            "<head>\n" +
                            "    <meta charset=\"UTF-8\">\n" +
                            "    <title>ORB Net - Happy Birthday</title>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "<h2>Happy birthday, " + user.getFirstName() + "!</h2>\n" +
                            "From your friends at <b>NDB</b>,\n" +
                            "We wish you a fabulous birthday celebration.\n" +
                            "May the days ahead of you be filled with prosperity, great health and above all joy in " +
                            "its truest and purest form. \n" +
                            "Orb Vpn Team | NDB Inc." +
                            "</body>\n" +
                            "</html>";
            emailService.sendMail(mailProperties.getUsername(), user.getUser().getEmail(), "Happy Birthday",
                    emailHtml);
        }
    }

    /**
     * sending subscription expiration reminders 1, 5 and 10 day(s) before expiration at 8 AM London time.
     */
    @Scheduled(cron = EVERY_DAY_8AM)
    public void subscriptionExpirationReminder() {
        log.info("sending subscription expiration reminders (1, 5 and 10 day(s) before expiration)...");

        for (Integer dayCount : DAY_COUNTS_FOR_EXPIRATION_NOTIFICATION) {
            List<UserProfile> users = userSubscriptionService.getUsersExpireInNextDays(dayCount);
            for (UserProfile user : users) {
                /**
                 * SMS
                 */
                SmsRequest smsRequest = new SmsRequest(user.getPhone(),
                        "Dear " + user.getFirstName() + ",\n" +
                                "There is just " + dayCount + " day left until your ORB-Vpn subscription expiration.\n");
                smsService.sendRequest(smsRequest);

                /**
                 * Email
                 */
                String emailHtml =
                        "<!DOCTYPE html>\n" +
                                "<html lang=\"en\">\n" +
                                "<head>\n" +
                                "    <meta charset=\"UTF-8\">\n" +
                                "    <title>ORB Net - Subscription Expiration Reminder</title>\n" +
                                "</head>\n" +
                                "<body>\n" +
                                "<h2>Dear " + user.getFirstName() + ", \n" +
                                "There is just " + dayCount + " day left until your ORB-Vpn subscription expiration." + "</h2>\n" +
                                "Orb Vpn Team | NDB Inc." +
                                "</body>\n" +
                                "</html>";
                emailService.sendMail(
                        mailProperties.getUsername(),
                        user.getUser().getEmail(),
                        "Subscription Expiration Reminder",
                        emailHtml);
            }
        }
        log.info("sent subscription expiration reminders successfully");
    }

    /**
     * Welcome new customers for joining ORB-VPN via SMS and Email
     * @param user new created user
     */
    public void welcomingNewUsers(User user){
        String name = "friend";
        try{
            name = user.getProfile().getFirstName();
        } catch (Exception e){
            //Keep calm! user did not define her or his name yet
        }

        /**
         * SMS
         */
        if(user.getProfile() != null && user.getProfile().getPhone() != null) {
            SmsRequest smsRequest = new SmsRequest(user.getProfile().getPhone(),
                    "Dear " + name + ",\n" +
                            "Welcome to Orb Vpn service. We provide 24/7 support for you to enjoy our service.\n");
            smsService.sendRequest(smsRequest);
        }

        /**
         * Email
         */
        String emailHtml =
                "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>ORB Net - Welcome to Orb Vpn!</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<h2>Dear " + name + "! \n" +
                        "Welcome to Orb Vpn service. We provide 24/7 support for you to enjoy our service." + "</h2>\n" +
                        "Orb Vpn Team | NDB Inc." +
                        "</body>\n" +
                        "</html>";
        emailService.sendMail(
                mailProperties.getUsername(),
                user.getEmail(),
                "Welcome to Orb Vpn!",
                emailHtml);
    }
}
