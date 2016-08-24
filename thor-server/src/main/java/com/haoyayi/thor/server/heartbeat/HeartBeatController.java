/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.server.heartbeat;

import com.haoyayi.thor.server.controller.AbstractController;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
@Controller
@Path("/heartbeat/")
public class HeartBeatController extends AbstractController {

    @GET
    public String heartBeat() {
        return "Got it.";
    }

}

