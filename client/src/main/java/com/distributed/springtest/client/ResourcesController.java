package com.distributed.springtest.client;

import com.distributed.springtest.client.forms.BuildingForm;
import com.distributed.springtest.client.forms.ResourceForm;
import com.distributed.springtest.utils.records.gamecontent.BuildingCost;
import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.gamecontent.ResourceInfo;
import com.distributed.springtest.utils.records.playerresources.Building;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Patrik on 2014-12-09.
 */
@Controller
@RequestMapping("/resources")
public class ResourcesController {

    @RequestMapping("")
    public Object resources() throws SQLException {
        ModelAndView modelAndView = new ModelAndView("resources");
        List<ResourceInfo> resources = ResourceInfo.selectAll(ResourceInfo.class, "SELECT * FROM resources");
        modelAndView.addObject("resources", resources);
        return modelAndView;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object getResource(@PathVariable Integer id) throws SQLException {
        ModelAndView modelAndView = new ModelAndView("editResource");
        ResourceInfo resource = ResourceInfo.findById(ResourceInfo.class, id);
        modelAndView.addObject("resource", resource);
        return modelAndView;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public Object editResource(@PathVariable Integer id, @ModelAttribute @Valid ResourceForm form, BindingResult result) throws SQLException {
        ResourceInfo resource = ResourceInfo.findById(ResourceInfo.class, id);
        resource.setName(form.getName());
        resource.save();
        resource.transaction().commit();
        return new RedirectView("/resources/");
    }
}