package com.orbvpn.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/.well-known/acme-challenge")
@Slf4j
public class DownloadController {

    @Autowired
    private ResourceLoader resourceLoader;

    @RequestMapping("/dR-WZ4e6S_a8GpzgxQp66no41qNfcmeHfEGQV_8ph5s")///file/{fileName:.+}
    public void downloadSslId(HttpServletResponse response) throws IOException {
        //HttpServletRequest request, @PathVariable("fileName") String fileName
        String fileName = "deployment/dR-WZ4e6S_a8GpzgxQp66no41qNfcmeHfEGQV_8ph5s";
        Resource resource = resourceLoader
                .getResource("classpath:" + fileName);
        if (resource.exists()) {
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            response.setHeader("Content-Disposition",
                    String.format("attachment; filename=" +
                            resource.getFilename()));
            response.setContentLength((int) resource.contentLength());
            InputStream inputStream = resource.getInputStream();
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } else {
            throw new RuntimeException("SSL id resource doesn't exist.");
        }
        log.info("ssl id is sent");
    }
}