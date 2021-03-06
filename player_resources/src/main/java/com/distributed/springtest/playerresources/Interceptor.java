package com.distributed.springtest.playerresources;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * Created by Jonas on 2015-01-06.
 */
public class Interceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    /**
     * All requests are checked if the digest is correct (except for /counter-requests). The requests must have
     * "username", "nc" and "digest" as headers. These are sent to the DigestHandler where they are checked.
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @return true if authorization succeeded, otherwise false.
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String username = httpServletRequest.getHeader("username");
        String nc = httpServletRequest.getHeader("nc");
        String digest = httpServletRequest.getHeader("digest");

        if(httpServletRequest.getRequestURI().equals("/counter")) {
            return true;
        }

        if(username != null && nc != null && digest != null) {
            boolean result = PlayerResourcesController.digestHandler.handle(username, Integer.valueOf(nc), digest);
            if(result == false) {
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            return true;
        }
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}
