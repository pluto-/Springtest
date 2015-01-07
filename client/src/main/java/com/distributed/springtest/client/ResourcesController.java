package com.distributed.springtest.client;

import com.distributed.springtest.client.forms.ResourceForm;
import com.distributed.springtest.utils.records.gamecontent.ResourceInfo;
import com.distributed.springtest.utils.security.DigestRestTemplate;
import org.springframework.beans.factory.InitializingBean;
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
@RequestMapping("/admin/resources")
public class ResourcesController implements InitializingBean {

    @Value("${hosts.gamecontent}")
    private String gamecontentURL;

    @Value("${subsystem.username}")
    private String serverUsername;

    @Value("${subsystem.password}")
    private String serverHashedPassword;

    private DigestRestTemplate gameContentRestTemplate;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Object resources(HttpServletRequest request) throws SQLException, IOException {
        ModelAndView modelAndView = new ModelAndView("admin/resources");
        String uri = gamecontentURL + "/resources";
        ResponseEntity<ResourceInfo[]> resourceInfos = gameContentRestTemplate.get(uri, ResourceInfo[].class);
        modelAndView.addObject("resources", Arrays.asList(resourceInfos.getBody()));
        return modelAndView;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object getResource(@PathVariable Integer id) throws SQLException, IOException {
        ModelAndView modelAndView = new ModelAndView("admin/editResource");

        String uri = gamecontentURL + "/resources/" + id;
        ResponseEntity<ResourceInfo> resourceInfo = gameContentRestTemplate.get(uri, ResourceInfo.class);

        modelAndView.addObject("resource", resourceInfo.getBody());
        return modelAndView;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public Object editResource(@PathVariable Integer id, @ModelAttribute @Valid ResourceForm form, BindingResult result) throws SQLException, IOException {

        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setId(id);
        resourceInfo.setName(form.getName());
        gameContentRestTemplate.put(gamecontentURL + "/resources/edit", resourceInfo);
        return new RedirectView("/admin/resources/");
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Object newResource(@ModelAttribute @Valid ResourceForm form, BindingResult result) throws SQLException, IOException {

        ResourceInfo resource = new ResourceInfo();
        resource.setName(form.getName());

        String uri = gamecontentURL + "/resources/add";
        ResponseEntity<Integer> resourceId = gameContentRestTemplate.post(uri, resource, Integer.class);

        return new RedirectView("/admin/resources");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        gameContentRestTemplate = new DigestRestTemplate(gamecontentURL, serverUsername, serverHashedPassword);
    }
}