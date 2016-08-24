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
public class Bootstrap {

    public static void main(String[] args)
            throws Exception {

        ApplicationContext ac = new ClassPathXmlApplicationContext(
                "classpath:ctx-server.xml",
                "classpath:ctx-biz.xml",
                "classpath:ctx-changehistory.xml",
                "classpath:ctx-common.xml",
                "classpath:ctx-dal.xml",
                "classpath:ctx-db.xml",
                "classpath:ctx-gen.xml",
                "classpath:ctx-meta.xml",
                "classpath:ctx-sal.xml",
                "classpath:ctx-facade.xml",
                "classpath:ctx-interface.xml"
                );
        ApplicationUtils.setApplicationContext(ac);
        NettyServer netty = ac.getBean(NettyServer.class);

        netty.start();

    }
}
