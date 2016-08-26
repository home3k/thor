/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.server;


import com.haoyayi.thor.server.server.NettyServer;
import com.haoyayi.thor.utils.ApplicationUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 服务入口
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public class ThorServer {

    public static void main(String[] args)
            throws Exception {
        ApplicationContext ac = new ClassPathXmlApplicationContext(
                "classpath:ctx-*.xml"
        );
        ApplicationUtils.setApplicationContext(ac);
        NettyServer netty = ac.getBean(NettyServer.class);
        netty.start();
    }

}
