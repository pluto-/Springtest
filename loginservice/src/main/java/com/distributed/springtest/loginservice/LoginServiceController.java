package com.distributed.springtest.loginservice;

import com.distributed.springtest.utils.records.gamecontent.BuildingCost;
import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.login.Player;
import com.distributed.springtest.utils.records.playerresources.Building;
import com.distributed.springtest.utils.records.playerresources.Construction;
import com.distributed.springtest.utils.records.playerresources.Resource;
import com.distributed.springtest.utils.wrappers.BuyBuildingWrapper;
import com.distributed.springtest.utils.wrappers.LoginWrapper;
import com.distributed.springtest.utils.wrappers.PlayerStateWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jonas on 2014-12-05.
 */
@RestController
public class LoginServiceController {

    @RequestMapping(value="/login", method=RequestMethod.PUT)
    public Object login(@RequestBody LoginWrapper wrapper) throws SQLException {

        System.err.println("Username: " + wrapper.getUsername());
        Player player = Player.select(Player.class, "SELECT * FROM players WHERE username=#1#", wrapper.getUsername());

        if(player == null || !player.getPassword().equals(wrapper.getEncryptedPassword())) {
            return new ResponseEntity<Object>("Wrong username/password.", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<Object>(HttpStatus.OK);
    }

}
