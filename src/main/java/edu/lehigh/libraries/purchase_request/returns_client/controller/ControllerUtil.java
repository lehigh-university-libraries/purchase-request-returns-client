package edu.lehigh.libraries.purchase_request.returns_client.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ControllerUtil {
    
    static String getReporterName(Authentication authentication, Map<String, String> headers) {
        String reporterName;
        if (authentication != null) {
            reporterName = authentication.getName();
            log.debug("Internal authentication: " + reporterName);
        }
        else {
            reporterName = headers.get("X-Remote-User");
            log.debug("External authentication: " + reporterName);
        }
        return reporterName;
    }

}
