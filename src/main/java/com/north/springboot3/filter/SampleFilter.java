package com.north.springboot3.filter;

import com.north.springboot3.config.BackDoorAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

//@Component
public class SampleFilter extends OncePerRequestFilter {

    private String getRequestPayload(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8);
        }
        return "";
    }

    private static final Logger logger = LoggerFactory.getLogger(SampleFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {

            String payload = readRequestBody(request);
            logger.debug("Request payload: {}", payload);
            BackDoorAuthenticationToken authToken = new BackDoorAuthenticationToken("GOD_ADMIN");
            SecurityContextHolder.getContext().setAuthentication(authToken);


            filterChain.doFilter(new DecryptedHttpServletRequest(request, payload), response);
        } catch (Exception e) {
            logger.error("Error processing request: {}", e.getMessage(), e);
            throw e;
        }
    }

    private String readRequestBody(HttpServletRequest request) throws IOException {
        return StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
    }

    private static class DecryptedHttpServletRequest extends HttpServletRequestWrapper {
        private final byte[] decryptedBodyBytes;
        private final ByteArrayInputStream bodyInputStream;

        public DecryptedHttpServletRequest(HttpServletRequest request, String decryptedBody) {
            super(request);
            this.decryptedBodyBytes = decryptedBody.getBytes(StandardCharsets.UTF_8);
            this.bodyInputStream = new ByteArrayInputStream(decryptedBodyBytes);
        }

        @Override
        public ServletInputStream getInputStream() {
            return new ServletInputStream() {
                @Override
                public int read() throws IOException {
                    return bodyInputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return bodyInputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(jakarta.servlet.ReadListener readListener) {
                    throw new UnsupportedOperationException("ReadListener not supported");
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }

        @Override
        public int getContentLength() {
            return decryptedBodyBytes.length;
        }

        @Override
        public long getContentLengthLong() {
            return decryptedBodyBytes.length;
        }
    }
}
