package com.orbvpn.api.config;

import com.orbvpn.api.service.common.ShellCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationSetup {
    @Value("${application.default-timezone}")
    private String defaultTimeZone;

    @Value("${application.nginx.file-name}")
    private String nginxFileName;

    @Value("${application.nginx.destination-path}")
    private String nginxDestinationPath;

    @Autowired
    private ResourceLoader resourceLoader;

    @PostConstruct
    public void refreshNginxFile() {
        log.debug("refreshing nginx...");
        /**
         * Step1: create refresh file for nginx
         */
        Resource resource = resourceLoader
                .getResource("classpath:deployment/" + nginxFileName);
        if (resource.exists()) {
            try {
                File destFile = new File(nginxDestinationPath + nginxFileName);
                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }
                if (!destFile.exists()) {
                    destFile.createNewFile();
                }
                FileCopyUtils.copy(resource.getFile(), destFile);
                log.info(nginxFileName + " file is refresh.");
            } catch (IOException e) {
                log.error("Failed to create " + nginxFileName + " file in " + nginxDestinationPath);
                return;
            }
        } else {
            log.error("NGINX.CONG.txt resource doesn't exist and refreshing Nginx file is not done.");
            return;
        }
        /**
         * Step2: reloading nginx server
         */
        String command = "sudo systemctl reload nginx";
        try {
            ShellCodeUtil.ShellCodeExecutionResult result = ShellCodeUtil.runShellCode(new String[]{command});
            log.debug("nginx reload executed with result of: " + result);
        } catch (IOException | InterruptedException e) {
            log.error("error in reloading nginx", e);
            return;
        }
    }

    @PostConstruct
    public void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone(defaultTimeZone));
        log.info("Default time zone is set to " + TimeZone.getDefault().getDisplayName());
    }
}
