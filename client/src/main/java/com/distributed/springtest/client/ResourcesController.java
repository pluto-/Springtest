package com.distributed.springtest.client;

import com.distributed.springtest.client.forms.ResourceForm;
import com.distributed.springtest.utils.records.gamecontent.ResourceInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Created by Patrik on 2014-12-09.
 */
@Controller
@RequestMapping("/resources")
public class ResourcesController {

    @Value("${hosts.gamecontent}")
    private String gamecontentURL;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Object resources(HttpServletRequest request) throws SQLException, IOException {
        ModelAndView modelAndView = new ModelAndView("resources");
        RestTemplate restTemplate = new RestTemplate();
        String uri = gamecontentURL + "/resources";
        ResourceInfo[] resourceInfos = restTemplate.getForObject(uri, ResourceInfo[].class);
        modelAndView.addObject("resources", Arrays.asList(resourceInfos));
        return modelAndView;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object getResource(@PathVariable Integer id) throws SQLException, IOException {
        ModelAndView modelAndView = new ModelAndView("editResource");

        RestTemplate restTemplate = new RestTemplate();
        String uri = gamecontentURL + "/resources/" + id;
        ResourceInfo resourceInfo = restTemplate.getForObject(uri, ResourceInfo.class);

        modelAndView.addObject("resource", resourceInfo);
        return modelAndView;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public Object editResource(@PathVariable Integer id, @ModelAttribute @Valid ResourceForm form, BindingResult result) throws SQLException, IOException {

        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setId(id);
        resourceInfo.setName(form.getName());
        RestTemplate restTemplate = new RestTemplate();
        String uri = gamecontentURL + "/resources/" + id;
         restTemplate.put(gamecontentURL + "/resources/" + id, resourceInfo, ResourceInfo.class);
        return new RedirectView("/resources/");
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Object newResource(@ModelAttribute @Valid ResourceForm form, BindingResult result) throws SQLException, IOException {

        ResourceInfo resource = new ResourceInfo();
        resource.setName(form.getName());

        RestTemplate restTemplate = new RestTemplate();
        String uri = gamecontentURL + "/resources/add";
        ResponseEntity<Integer> resourceId = restTemplate.postForEntity(uri, resource, Integer.class);

        return new RedirectView("/resources");
    }

}