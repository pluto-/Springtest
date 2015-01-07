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
import com.distributed.springtest.utils.security.DigestRestTemplate;
import com.distributed.springtest.utils.wrappers.BuildingInfoWrapper;
import com.distributed.springtest.utils.wrappers.BuyBuildingWrapper;
import com.distributed.springtest.utils.wrappers.PlayerResourceModificationWrapper;
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
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    @Value("${player.create.beginningresource.id}")
    private Integer beginningResourceId;

    @Value("${player.create.beginningresource.amount}")
    private Double beginningResourceAmount;

    @Value("${hosts.playerresources}")
    private String playerResourcesURL;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${subsystem.username}")
    private String serverUsername;

    @Value("${subsystem.password}")
    private String serverHashedPassword;

    @RequestMapping(value="/create", method = RequestMethod.GET)
    public Object createGet() throws SQLException {
        ModelAndView modelAndView = new ModelAndView("register/create");

        CreateUserForm form = new CreateUserForm();

        modelAndView.addObject("form", form);


        return modelAndView;
    }

    @RequestMapping(value="/create", method = RequestMethod.POST)
    public Object createPost(@ModelAttribute @Valid CreateUserForm form, HttpSession session) throws SQLException, UnsupportedEncodingException, NoSuchAlgorithmException {

        if(!form.getPassword().matches("^[a-zA-Z0-9_]*$") || !form.getUsername().matches("^[a-zA-Z0-9_]*$")) {
            session.setAttribute("message", "Only characters a - z, A - Z, 0-9 and _ are allowed.");
            return new RedirectView("/");
        }

        UserAuthentication check = UserAuthentication.select(UserAuthentication.class, "SELECT * FROM user_authentication WHERE username = #1#", form.getUsername());
        if(check != null) {
            session.setAttribute("message", "Username already used.");
        } else {
            UserAuthentication userAuthentication = new UserAuthentication();
            userAuthentication.setUsername(form.getUsername());

            byte[] bytesOfMessage = form.getPassword().getBytes("UTF-8");

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] passwordHashed = md.digest(bytesOfMessage);

            userAuthentication.setPassword(new BigInteger(1, passwordHashed).toString(16));
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

            PlayerResourceModificationWrapper wrapper = new PlayerResourceModificationWrapper();
            wrapper.setResourceId(beginningResourceId);
            wrapper.setResourceAmount(beginningResourceAmount);
            wrapper.setPlayerId(userAuthentication.getPlayerId());

            DigestRestTemplate restTemplate = new DigestRestTemplate(playerResourcesURL, serverUsername, serverHashedPassword);

            try{
                restTemplate.put(playerResourcesURL + "/resources/modify", wrapper);

                userAuthorization.save();
                userAuthentication.transaction().commit();
                session.setAttribute("message", "Player created.");

            } catch(HttpClientErrorException e) {
                session.setAttribute("message", "ERROR: " + e.getResponseBodyAsString());
            }

        }



        return new RedirectView("/");
    }

}
