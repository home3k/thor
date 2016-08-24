/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.server;


import com.haoyayi.thor.server.server.NettyServer;
import com.haoyayi.thor.utils.ApplicationUtils;

import org.springframework.context.ApplicationContext;

/**
 * 服务入口
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public class ThorServer {


    public void start(ApplicationContext ac) {
        ApplicationUtils.setApplicationContext(ac);
        NettyServer netty = ac.getBean(NettyServer.class);
        netty.start();
    }

}
