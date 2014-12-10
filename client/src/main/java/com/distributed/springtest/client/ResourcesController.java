package com.distributed.springtest.client;

import com.distributed.springtest.client.forms.BuildingForm;
import com.distributed.springtest.client.forms.ResourceForm;
import com.distributed.springtest.utils.records.gamecontent.BuildingCost;
import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.gamecontent.ResourceInfo;
import com.distributed.springtest.utils.records.playerresources.Building;
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

import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Patrik on 2014-12-09.
 */
@Controller
@RequestMapping("/resources")
public class ResourcesController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Object resources() throws SQLException, IOException {
        ModelAndView modelAndView = new ModelAndView("resources");
        RestTemplate restTemplate = new RestTemplate();
        String uri = PropertiesLoader.getGameContentAddressAndPort() + "/resource";
        ResourceInfo[] resourceInfos = restTemplate.getForObject(uri, ResourceInfo[].class);
        modelAndView.addObject("resources", Arrays.asList(resourceInfos));
        return modelAndView;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object getResource(@PathVariable Integer id) throws SQLException, IOException {
        ModelAndView modelAndView = new ModelAndView("editResource");

        RestTemplate restTemplate = new RestTemplate();
        String uri = PropertiesLoader.getGameContentAddressAndPort() + "/resource/" + id;
        ResourceInfo resourceInfo = restTemplate.getForObject(uri, ResourceInfo.class);

        modelAndView.addObject("resource", resourceInfo);
        return modelAndView;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public Object editResource(@PathVariable Integer id, @ModelAttribute @Valid ResourceForm form, BindingResult result) throws SQLException, IOException {

        RestTemplate restTemplate = new RestTemplate();
        String uri = PropertiesLoader.getGameContentAddressAndPort() + "/resource/" + id;
        ResourceInfo resource = restTemplate.getForObject(uri, ResourceInfo.class);

        resource.setName(form.getName());
        resource.save();
        resource.transaction().commit();
        return new RedirectView("/resources/");
    }

    /*@RequestMapping(value = "/new", method = RequestMethod.GET)
    public Object newResource() throws SQLException {
        ModelAndView modelAndView = new ModelAndView("editResource");
        modelAndView.addObject("edit", false);
        return modelAndView;
    }*/

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Object newResource(@ModelAttribute @Valid ResourceForm form, BindingResult result) throws SQLException, IOException {

        ResourceInfo resource = new ResourceInfo();
        resource.setName(form.getName());

        RestTemplate restTemplate = new RestTemplate();
        String uri = PropertiesLoader.getGameContentAddressAndPort() + "/resource/add";
        ResponseEntity<Integer> resourceId = restTemplate.postForEntity(uri, resource, Integer.class);

        return new RedirectView("/resources");
    }
}