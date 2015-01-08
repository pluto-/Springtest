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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * An admin site that shows information about the different resources of the game.
 *
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

    /**
     * Creates the model and view containing information about the resources.
     * @param request
     * @return
     * @throws SQLException
     * @throws IOException
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Object resources(HttpServletRequest request) throws SQLException, IOException {
        ModelAndView modelAndView = new ModelAndView("admin/resources");
        String uri = gamecontentURL + "/resources";
        ResponseEntity<ResourceInfo[]> resourceInfos = gameContentRestTemplate.get(uri, ResourceInfo[].class);
        modelAndView.addObject("resources", Arrays.asList(resourceInfos.getBody()));
        return modelAndView;
    }

    /**
     * The edit page for a resource. Constructs the model and view containing information about the resource.
     * @param id id of the resource.
     * @return model and view.
     * @throws SQLException
     * @throws IOException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object getResource(@PathVariable Integer id) throws SQLException, IOException {
        ModelAndView modelAndView = new ModelAndView("admin/editResource");

        String uri = gamecontentURL + "/resources/" + id;
        ResponseEntity<ResourceInfo> resourceInfo = gameContentRestTemplate.get(uri, ResourceInfo.class);

        modelAndView.addObject("resource", resourceInfo.getBody());
        return modelAndView;
    }

    /**
     * Is called when a resource is edited. Sends an edit-request to the game content subsystem.
     * @param id id of the resource.
     * @param form form containing the new information.
     * @param result
     * @return
     * @throws SQLException
     * @throws IOException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public Object editResource(@PathVariable Integer id, @ModelAttribute @Valid ResourceForm form, BindingResult result) throws SQLException, IOException {

        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setId(id);
        resourceInfo.setName(form.getName());
        gameContentRestTemplate.put(gamecontentURL + "/resources/edit", resourceInfo);
        return new RedirectView("/admin/resources/");
    }

    /**
     * Called when "Create new resource" is pressed. Sends an "add resource"-request to the game content subsystem.
     * @param form
     * @param result
     * @return
     * @throws SQLException
     * @throws IOException
     */
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