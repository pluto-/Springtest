package com.distributed.springtest.client;

import com.jajja.jorm.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Inteceptor that handles cleanup and processing for all requests
 */
public class Interceptor extends HandlerInterceptorAdapter{
    private static final Logger logger = LoggerFactory.getLogger(AuctionController.class);

    /**
     * Method that is called before a request is handled.
     * Prints out the request URL for the incoming request.
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("Incoming request to: " + request.getRequestURL());
        return super.preHandle(request, response, handler);
    }

    /**
     * Method that is called after a request is processed, but before the page has been rendered.
     * This is required for the general layout (layout.vsl) to work.
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
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

    /**
     * Method that is called after a request has been handled completely.
     * Closes any open database transactions.
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,  Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response,  handler,  ex);
        Database.close();
    }
}
