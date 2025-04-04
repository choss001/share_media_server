package com.media.share.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);

        // Build full URL
        String fullURL = getFullURL(request);

        // Read body
        String body = new String(wrappedRequest.getInputStream().readAllBytes());

        logger.info("REQUEST: {}\nBODY: {}", fullURL, body);

        filterChain.doFilter(wrappedRequest, response);
    }

    private String getFullURL(HttpServletRequest request) {
        String scheme = request.getScheme();             // http or https
        String serverName = request.getServerName();     // hostname or IP
        int serverPort = request.getServerPort();        // 8080, etc.
        String uri = request.getRequestURI();            // /mediaList
        String queryString = request.getQueryString();   // param1=value1&param2=value2

        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);

        if ((scheme.equals("http") && serverPort != 80) ||
                (scheme.equals("https") && serverPort != 443)) {
            url.append(":").append(serverPort);
        }

        url.append(uri);

        if (queryString != null) {
            url.append("?").append(queryString);
        }

        return url.toString();
    }
}
