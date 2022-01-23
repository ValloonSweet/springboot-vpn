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
        String smsMessage = "Happy birthday!\nMay you live your dreams.\nWith lots of love.\nOrbVPN";
        String emailTitle = "OrbVPN: Happy Birthday!";
        String emailMessage = "From your friends at <b>OrbVPN</b>,<br>" +
                "We wish you a fabulous birthday celebration.<br>" +
                "May the days ahead of you be filled with prosperity, great health and above all joy in " +
                "its truest and purest form.";

        List<UserProfile> userProfiles = userProfileRepository.findUsersBornToday();
        for (UserProfile userProfile : userProfiles) {
            sendSms(userProfile, smsMessage);
            sendEmail(userProfile, emailTitle, emailMessage);
        }
    }

    /**
     * sending subscription expiration reminders 1, 5 and 10 day(s) before expiration at 8 AM London time.
     */
    @Scheduled(cron = EVERY_DAY_8AM)
    public void subscriptionExpirationReminder() {
        log.info("sending subscription expiration reminders (1, 5 and 10 day(s) before expiration)...");
        for (Integer dayCount : DAYS_BEFORE_EXPIRATION) {
            List<UserProfile> userProfiles = userSubscriptionService.getUsersExpireInNextDays(dayCount);
            for (UserProfile userProfile : userProfiles) {
                String smsMessage = "There is just " + dayCount + " " +
                        (dayCount == 1 ? "day" : "days") +
                        " left until your OrbVPN subscription expires.\n";
                String emailTitle = "OrbVPN: Subscription Expiration Reminder";
                String emailMessage = "There is just " + dayCount + " " +
                        (dayCount == 1 ? "day" : "days") +
                        " left until your OrbVPN subscription expires. <br>" +
                        "To renew your account please " +
                        letUsKnow("Renew account order for available subscription",
                                "Please renew my account that there is just " + dayCount + " " +
                                        (dayCount == 1 ? "day" : "days") +
                                        " left until my OrbVPN subscription expires.");
                sendSms(userProfile, smsMessage);
                sendEmail(userProfile, emailTitle, emailMessage);
            }
        }
        log.info("sent subscription expiration reminders successfully");
    }

    @Scheduled(cron = EVERY_DAY_8AM)
    public void afterSubscriptionExpiredNotification() {
        log.info("sending notification after subscription expiration after expiration...");
        for (Integer dayCount : DAYS_AFTER_EXPIRATION) {
            List<UserProfile> userProfiles = userSubscriptionService.getUsersExpireInPreviousDays(dayCount);
            for (UserProfile userProfile : userProfiles) {
                String smsMessage = "Your subscription has expired " + dayCount + " " +
                        (dayCount == 1 ? "day" : "days") +
                        " ago. Please don't hesitate to contact us if you would like to renew your service.";
                String emailTitle = "OrbVPN: Subscription Expired";
                String emailMessage = "Your subscription has expired " + dayCount + " " +
                        (dayCount == 1 ? "day" : "days") +
                        " ago. Please don't hesitate to " +
                        letUsKnow("Renew account order for expired subscription",
                                "Please renew my account. My subscription has expired " + dayCount + " " +
                                        (dayCount == 1 ? "day" : "days") + " ago.") + " if you would like to renew your service.";
                sendSms(userProfile, smsMessage);
                sendEmail(userProfile, emailTitle, emailMessage);
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
        String smsMessage = "Welcome to OrbVPN.\n" +
                "Your username is [" + user.getUsername() + "]\n" +
                "We provide 24/7 support for you to enjoy our service.\n" +
                "WhatsApp: https://wa.me/message/3NYJBB6MNCQPM1 \n" +
                "Telegram: https://t.me/OrbVPN\n" +
                "Instagram: https://www.instagram.com/orbvpn\n";

        String emailTitle = "OrbVPN: Welcome!";
        String emailMessage = " Welcome to your new OrbVPN Account. <br><br>" +
                "Sing in to your OrbVPN account to access the service.<br><br>" +
        //      "the OrbVPN services that you have subscribed for.<br><br>" +
                "<strong>Your username:</strong> " + user.getUsername();
        //"Your subscription is valid for $multi-login devices during next $days/month/years and will be expired on $expirationdate.<br><br>"+

        sendSms(user.getProfile(), smsMessage);
        sendEmail(user.getProfile(), emailTitle, emailMessage);
    }

    /**
     * not used yet
     */
    public void resetPassword(User user) {
        String smsMessage = "Your password is reset successfully.";
        String emailTitle = "OrbVPN: Password Reset Code";
        String emailMessage = "We got a request to reset your password.<br>" +
                "You can open the following link in your browser to change the password:<br><br>" +
                "<herf>$Link<herf><br><br>" +
                "or you can use the following token code:<br><br>" +
                "<strong>Code:</strong> $passwordresetcode<br><br>" +
                "If you ignore this message your password won't be changed.<br>" +
                "If you didn't request a password reset, please " +
                letUsKnow("wrong reset password request",
                        "this reset password reset request is not from me.") + ".<br><br>" +
                "--<br>" +
                "Disclaimer: This message is intended only for the individual(s) named and may contain confidential information. " +
                "If you have received this e-mail in error you should not read, print, copy or forward it. " +
                "Please notify the sender about the error immediately by e-mail and delete this message from your system.";
        sendSms(user.getProfile(), smsMessage);
        sendEmail(user.getProfile(), emailTitle, emailMessage);
    }

    public void resetPasswordDone(User user) {
        String smsMessage = "Your password is reset successfully.";
        String emailTitle = "OrbVPN: Successful Password Reset";
        String emailMessage = "Your password has been changed successfully<br>" +
                "If you didn't request a password reset, please " +
                letUsKnow("wrong successful reset password",
                        "this reset password that has been done is not from me.") + ".";
        sendSms(user.getProfile(), smsMessage);
        sendEmail(user.getProfile(), emailTitle, emailMessage);
    }

    /**
     * not used yet
     */
    public void renewUser(User user) {
        String smsMessage = "Thank you for renewing your OrbVPN subscription.\n" +
                "Your subscription is valid for $multi-login devices during next $days/month/years and will be expired on $expirationdate.";
        String emailTitle = "OrbVPN: Successful subscription renewal";
        String emailMessage = "Thank you for renewing your OrbVPN $subscriptionname subscription.<br><br>" +
                "Your subscription is valid for $multi-login devices during next $days/month/years and will be expired on $expirationdate.";
        sendSms(user.getProfile(), smsMessage);
        sendEmail(user.getProfile(), emailTitle, emailMessage);
    }

    private void sendSms(UserProfile userProfile, String message) {
        String starting;
        if (userProfile != null && userProfile.getFirstName() != null) {
            starting = "Dear " + userProfile.getFirstName() + ",\n";
        } else {
            starting = "Hello,\n";
        }
        if (userProfile != null && userProfile.getPhone() != null) {
            SmsRequest smsRequest = new SmsRequest(userProfile.getPhone(),
                    starting + message);
            smsService.sendRequest(smsRequest);
        }
    }

    private void sendEmail(UserProfile userProfile, String title, String message) {
        if (userProfile == null)
            return;
        String starting = null;
        if (userProfile.getFirstName() != null) {
            starting = "Dear " + userProfile.getFirstName() + ",<br><br>";
        } else {
            starting = "Hello,<br><br>";
        }

        String emailHtml =
                "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>" + title + "</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                            starting + message + "\n" +
                            "<br>" +
                            "<p>" +
                            "Our support team is at your disposal 24/7 to enjoy our service, please do not hesitate to contact us<br> " +
                            "WhatsApp: https://wa.me/message/3NYJBB6MNCQPM1 <br>" +
                            "Telegram: https://t.me/OrbVPN<br>" +
                            "Instagram: https://www.instagram.com/orbvpn/" +
                            "</p>" +
                            "<p>" +
                            "Kind regards,<br>" +
                            "OrbVPN" +
                            "</p>" +
                        "</body>\n" +
                "</html>";
        emailService.sendMail(
                mailProperties.getUsername(),
                userProfile.getUser().getEmail(),
                title,
                emailHtml);
    }

    private String letUsKnow(String title, String message) {
        title = title.replace(" ", "%20");
        message = message.replace(" ", "%20");
        return "<a href = \"mailto:info@orbvpn.com?subject=" + title + "&body=" + message + "\">\n" +
                "let us know\n" +
                "</a>";
    }
}
