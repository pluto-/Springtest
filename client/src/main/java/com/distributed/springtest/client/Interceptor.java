package com.distributed.springtest.client;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Patrik on 2014-12-08.
 */
public class Interceptor extends HandlerInterceptorAdapter{

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println(request.getRequestURL());
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response,  handler,  modelAndView);
        if (modelAndView != null && modelAndView.getView() == null) {
            String template = (String)modelAndView.getModel().get("template");

            if(template == null || !template.equals("")) {
                modelAndView.addObject("view", modelAndView.getViewName());
                modelAndView.setViewName(template != null ? template : "layout");
            }
        }
    }
}
