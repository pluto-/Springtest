package com.distributed.springtest.client;

import com.distributed.springtest.client.database.UserAuthentication;
import com.distributed.springtest.client.database.UserAuthorization;
import com.distributed.springtest.client.forms.player.BuildingForm;
import com.distributed.springtest.client.forms.player.ConstructionForm;
import com.distributed.springtest.client.forms.player.ResourceForm;
import com.distributed.springtest.client.forms.register.CreateUserForm;
import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.gamecontent.ResourceInfo;
import com.distributed.springtest.utils.records.playerresources.Building;
import com.distributed.springtest.utils.records.playerresources.Construction;
import com.distributed.springtest.utils.records.playerresources.Resource;
import com.distributed.springtest.utils.wrappers.BuildingInfoWrapper;
import com.distributed.springtest.utils.wrappers.BuyBuildingWrapper;
import com.distributed.springtest.utils.wrappers.PlayerStateWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jonas on 2014-12-11.
 */
@Controller
@RequestMapping("/register")
public class RegisterController {

    @Value("${hosts.playerresources}")
    private String playerResourcesURL;

    @Value("${admin.password}")
    private String adminPassword;

    @RequestMapping(value="/create", method = RequestMethod.GET)
    public Object createGet() throws SQLException {
        ModelAndView modelAndView = new ModelAndView("register/create");

        CreateUserForm form = new CreateUserForm();

        modelAndView.addObject("form", form);


        return modelAndView;
    }

    @RequestMapping(value="/create", method = RequestMethod.POST)
    public Object createPost(@ModelAttribute @Valid CreateUserForm form, HttpSession session) throws SQLException {

        UserAuthentication check = UserAuthentication.select(UserAuthentication.class, "SELECT * FROM user_authentication WHERE username = #1#", form.getUsername());
        if(check != null) {
            session.setAttribute("message", "Username already used.");
        } else {
            UserAuthentication userAuthentication = new UserAuthentication();
            userAuthentication.setUsername(form.getUsername());
            userAuthentication.setPassword(form.getPassword());
            userAuthentication.setEnabled(true);
            userAuthentication.save();

            UserAuthorization userAuthorization = new UserAuthorization();
            userAuthorization.setPlayerRef(userAuthentication);
            if(form.getRole().equals("ADMIN")) {
                if(form.getAdminPassword().equals(adminPassword)) {
                    userAuthorization.setRole("ROLE_ADMIN");
                } else {
                    userAuthentication.transaction().close();
                    session.setAttribute("message", "Admin password was wrong.");
                    return new RedirectView("/");
                }
            } else if(form.getRole().equals("USER")) {
                userAuthorization.setRole("ROLE_USER");
            }

            userAuthorization.save();

            userAuthentication.transaction().commit();
            session.setAttribute("message", "Player created.");
        }

        return new RedirectView("/");
    }

}
