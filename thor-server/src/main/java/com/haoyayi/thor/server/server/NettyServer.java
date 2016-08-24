/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.server.server;

import com.haoyayi.thor.server.context.ServerContext;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.annotation.PreDestroy;
import javax.ws.rs.ext.Provider;
import java.util.Collection;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
@Component
public class NettyServer {

    protected final static Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    @Autowired
    private ApplicationContext ac;

    @Autowired
    private ServerContext serverContext;

    private ConfigurableNettyJaxrsServer netty;

    public void start() {

        ResteasyDeployment dp = new ResteasyDeployment();

        Collection<Object> providers = ac.getBeansWithAnnotation(Provider.class).values();
        Collection<Object> controllers = ac.getBeansWithAnnotation(Controller.class).values();

        // extract providers
        if (providers != null) {
            dp.getProviders().addAll(providers);
        }
        // extract only controller annotated beans
        dp.getResources().addAll(controllers);

        netty = new ConfigurableNettyJaxrsServer();
        netty.initBootstrap().setOption("reuseAddress", true);
        netty.setDeployment(dp);
        netty.setPort(serverContext.getPort());
        netty.setRootResourcePath(serverContext.getServerPath());
        netty.setSecurityDomain(null);
        netty.setIoWorkerCount(serverContext.getIoWorkerCount());
        netty.setExecutorThreadCount(serverContext.getExecutorThreadCount());
        netty.setMaxRequestSize(serverContext.getMaxRequestSize());
        LOGGER.info("Thor server init success..");
        netty.start();
        LOGGER.info("Thor server start success. path:{}, port:{}", serverContext.getServerPath(),
                serverContext.getPort().toString());
    }

    @PreDestroy
    public void cleanUp() {
        netty.stop();
    }

}
