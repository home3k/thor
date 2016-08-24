/*
 * Copyright 2014 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.server.server;

import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.netty.HttpServerPipelineFactory;
import org.jboss.resteasy.plugins.server.netty.HttpsServerPipelineFactory;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.plugins.server.netty.RequestDispatcher;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.Executors;

/**
 * Expose bootstrap config
 *
 * @author home3k (sunkai@51haoyayi.com)
 */
public class ConfigurableNettyJaxrsServer extends NettyJaxrsServer {

    private int ioWorkerCount = Runtime.getRuntime().availableProcessors() * 2;
    private int executorThreadCount = 16;
    private SSLContext sslContext;
    private int maxRequestSize = 1024 * 1024 * 10;

    static final ChannelGroup allChannels = new DefaultChannelGroup("Thor-Server");

    /**
     * expose bootstrap to be able to config
     *
     * @return
     */
    public Bootstrap initBootstrap() {
        this.bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool(),
                        ioWorkerCount));
        return bootstrap;
    }

    public void setBootstrap(ServerBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void start() {
        deployment.start();
        RequestDispatcher dispatcher = new RequestDispatcher(
                (SynchronousDispatcher) deployment.getDispatcher(),
                deployment.getProviderFactory(), domain);

        // Configure the server.
        if (bootstrap == null) {
            initBootstrap();
        }

        ChannelPipelineFactory factory;
        if (sslContext == null) {
            factory = new HttpServerPipelineFactory(dispatcher, root, executorThreadCount, maxRequestSize, false, new ArrayList<ChannelHandler>());
        } else {
            factory = new HttpsServerPipelineFactory(dispatcher, root, executorThreadCount, maxRequestSize, false, new ArrayList<ChannelHandler>(),  sslContext);
        }
        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(factory);

        // Bind and start to accept incoming connections.
        channel = bootstrap.bind(new InetSocketAddress(port));
        allChannels.add(channel);
    }

    @Override
    public void setIoWorkerCount(int ioWorkerCount) {
        this.ioWorkerCount = ioWorkerCount;
    }

    @Override
    public void setExecutorThreadCount(int executorThreadCount) {
        this.executorThreadCount = executorThreadCount;
    }

    @Override
    public void setMaxRequestSize(int maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }
}
