package com.distributed.springtest.playerresources;

import com.distributed.springtest.utils.records.playerresources.Building;
import com.distributed.springtest.utils.records.playerresources.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Jonas on 2014-12-05.
 */
@RestController
public class AdminController {

    @RequestMapping(value="/admin/player/resource", method=RequestMethod.PUT)
    public Object setPlayerResource(@RequestBody Resource resource) {

        try {

            PlayerResourcesController.updatePlayerResources(resource.getPlayerId());

            resource.save();
            resource.transaction().commit();

            return new ResponseEntity<Object>(HttpStatus.OK);

        } catch (SQLException |IOException e) {
            e.printStackTrace();
            return new ResponseEntity<Object>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value="/admin/player/building", method=RequestMethod.PUT)
    public Object setPlayerBuilding(@RequestBody Building building) {

        try {

            PlayerResourcesController.updatePlayerResources(building.getPlayerId());

            building.save();
            building.transaction().commit();

            return new ResponseEntity<Object>(HttpStatus.OK);

        } catch (SQLException |IOException e) {
            e.printStackTrace();
            return new ResponseEntity<Object>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
