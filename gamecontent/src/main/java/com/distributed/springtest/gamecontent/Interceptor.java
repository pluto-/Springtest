package com.distributed.springtest.gamecontent;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String username = httpServletRequest.getHeader("username");
        String nc = httpServletRequest.getHeader("nc");
        String digest = httpServletRequest.getHeader("digest");

        if(httpServletRequest.getRequestURI().equals("/counter")) {
            return true;
        }

        if(username != null && nc != null && digest != null) {
            boolean result = GameContentController.digestHandler.handle(username, Integer.valueOf(nc), digest);
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
