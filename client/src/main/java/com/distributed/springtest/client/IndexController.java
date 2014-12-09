package com.distributed.springtest.client;

import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.playerresources.Building;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.List;


/**
 * Created by Patrik on 2014-12-08.
 */
@Controller
public class IndexController {
    Logger logger = LoggerFactory.getLogger(IndexController.class);
    @RequestMapping("")
    public Object index() {
        return "index";
    }

}
