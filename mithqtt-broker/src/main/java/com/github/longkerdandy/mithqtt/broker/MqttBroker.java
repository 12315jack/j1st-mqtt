package com.github.longkerdandy.mithqtt.broker;

import com.github.longkerdandy.mithqtt.broker.comm.BrokerListenerFactoryImpl;
import com.github.longkerdandy.mithqtt.broker.handler.BytesMetricsHandler;
import com.github.longkerdandy.mithqtt.broker.handler.MessageMetricsHandler;
import com.github.longkerdandy.mithqtt.broker.handler.SyncRedisHandler;
import com.github.longkerdandy.mithqtt.storage.redis.sync.RedisSyncStorage;
import com.github.longkerdandy.mithqtt.api.auth.Authenticator;
import com.github.longkerdandy.mithqtt.api.comm.BrokerCommunicator;
import com.github.longkerdandy.mithqtt.api.comm.BrokerListenerFactory;
import com.github.longkerdandy.mithqtt.api.metrics.MetricsService;
import com.github.longkerdandy.mithqtt.broker.session.SessionRegistry;
import com.github.longkerdandy.mithqtt.broker.util.Validator;
import com.lambdaworks.redis.ValueScanCursor;
import io.j1st.power.storage.mongo.MongoStorage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

/**
 * MQTT Bridge
 */
public class MqttBroker {

    private static final Logger logger = LoggerFactory.getLogger(MqttBroker.class);

    public static void main(String[] args) throws Exception {

        logger.debug("Starting MQTT broker ...");

        // load config
        logger.debug("Loading MQTT broker config files ...");
        PropertiesConfiguration brokerConfig;
        PropertiesConfiguration redisConfig;
        PropertiesConfiguration communicatorConfig;
        PropertiesConfiguration authenticatorConfig;
        PropertiesConfiguration metricsConfig;
        PropertiesConfiguration mongodbConfig;
        if (args.length >= 5) {
            brokerConfig = new PropertiesConfiguration(args[0]);
            redisConfig = new PropertiesConfiguration(args[1]);
            communicatorConfig = new PropertiesConfiguration(args[2]);
            authenticatorConfig = new PropertiesConfiguration(args[3]);
            metricsConfig = new PropertiesConfiguration(args[4]);
            mongodbConfig = new PropertiesConfiguration(args[5]);
        } else {
            brokerConfig = new PropertiesConfiguration("config/broker.properties");
            redisConfig = new PropertiesConfiguration("config/redis.properties");
            communicatorConfig = new PropertiesConfiguration("config/communicator.properties");
            authenticatorConfig = new PropertiesConfiguration("config/authenticator.properties");
            metricsConfig = new PropertiesConfiguration("config/metrics.properties");
            mongodbConfig = new PropertiesConfiguration("config/mongo.properties");
        }

        final String brokerId = brokerConfig.getString("broker.id");

        // validator
        logger.debug("Initializing validator ...");
        Validator validator = new Validator(brokerConfig);

        // session registry
        logger.debug("Initializing session registry ...");
        SessionRegistry registry = new SessionRegistry();

        // storage
        logger.debug("Initializing redis storage ...");
        RedisSyncStorage redis = (RedisSyncStorage) Class.forName(redisConfig.getString("storage.sync.class")).newInstance();
        redis.init(redisConfig);

        logger.debug("Clearing broker connection state in storage, this may take some time ...");
        clearBrokerConnectionState(brokerId, redis);

        // communicator
        logger.debug("Initializing communicator ...");
        BrokerCommunicator communicator = (BrokerCommunicator) Class.forName(communicatorConfig.getString("communicator.class")).newInstance();
        BrokerListenerFactory listenerFactory = new BrokerListenerFactoryImpl(registry);
        communicator.init(communicatorConfig, brokerId, listenerFactory);

        // authenticator
        logger.debug("Initializing authenticator...");
        Authenticator authenticator = (Authenticator) Class.forName(authenticatorConfig.getString("authenticator.class")).newInstance();
        authenticator.init(mongodbConfig);

        // metrics
        logger.debug("Initializing metrics ...");
        final boolean metricsEnabled = metricsConfig.getBoolean("metrics.enabled");
        MetricsService metrics = metricsEnabled ? (MetricsService) Class.forName(metricsConfig.getString("metrics.class")).newInstance() : null;
        if (metricsEnabled) metrics.init(metricsConfig);

        // broker
        final int keepAlive = brokerConfig.getInt("mqtt.keepalive.default");
        final int keepAliveMax = brokerConfig.getInt("mqtt.keepalive.max");
        final boolean ssl = brokerConfig.getBoolean("mqtt.ssl.enabled");
//        final SslContext sslContext = ssl ? SslContextBuilder.forServer(new File(brokerConfig.getString("mqtt.ssl.certPath")), new File(brokerConfig.getString("mqtt.ssl.keyPath")), brokerConfig.getString("mqtt.ssl.keyPassword")).build() : null;
        final String host = brokerConfig.getString("mqtt.host");
        final int port = brokerConfig.getInt("mqtt.port");


        // tcp server
        logger.debug("Initializing tcp server ...");
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
        EventLoopGroup bossGroup = brokerConfig.getBoolean("netty.useEpoll") ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        EventLoopGroup workerGroup = brokerConfig.getBoolean("netty.useEpoll") ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        EventLoopGroup handlerGroup = brokerConfig.getBoolean("netty.useEpoll") ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        // shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                logger.debug("MQTT broker is shutting down ...");

                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
                if (metricsEnabled) metrics.destroy();
                communicator.destroy();
                authenticator.destroy();

                logger.debug("Clearing broker connection state in storage, this may take some time ...");
                clearBrokerConnectionState(brokerId, redis);

                redis.destroy();

                logger.info("MQTT broker has been shut down.");
            }
        });

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(brokerConfig.getBoolean("netty.useEpoll") ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        // idle
                        p.addFirst("idleHandler", new IdleStateHandler(0, 0, keepAlive));
                        // metrics
                        if (metricsEnabled) {
                            p.addLast("bytesMetrics", new BytesMetricsHandler(metrics, brokerId));
                        }
                        // mqtt encoder & decoder
                        p.addLast("encoder", new MqttEncoder());
                        p.addLast("decoder", new MqttDecoder());
                        // metrics
                        if (metricsEnabled) {
                            p.addLast("msgMetrics", new MessageMetricsHandler(metrics, brokerId));
                        }
                        // logic handler
                        p.addLast(handlerGroup, "logicHandler", new SyncRedisHandler(authenticator, communicator, redis, registry, validator, brokerId, keepAlive, keepAliveMax));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, brokerConfig.getInt("netty.soBacklog"))
                .childOption(ChannelOption.SO_KEEPALIVE, brokerConfig.getBoolean("netty.soKeepAlive"));

        // Bind and start to accept incoming connections.
        ChannelFuture f = b.bind(host, port).sync();


        /**
         * 打开SSL的端口监听
         */
        if (ssl) {

            String password = brokerConfig.getString("mqtt.ssl.password");
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(new File(brokerConfig.getString("mqtt.ssl.certPath"))), password.toCharArray());

            TrustManagerFactory tmFactory = TrustManagerFactory.getInstance("SunX509");
            tmFactory.init(ks);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password.toCharArray());

            final SslContext sslContext = ssl ? SslContextBuilder.forServer(kmf).build() : null;
            final int sslport = ssl ? brokerConfig.getInt("mqtt.ssl.port") : 8883;
            ServerBootstrap sslb = new ServerBootstrap();
            sslb.group(bossGroup, workerGroup)
                    .channel(brokerConfig.getBoolean("netty.useEpoll") ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            // ssl
                            p.addLast("ssl", sslContext.newHandler(ch.alloc()));

                            // idle
                            p.addFirst("idleHandler", new IdleStateHandler(0, 0, keepAlive));
                            // metrics
                            if (metricsEnabled) {
                                p.addLast("bytesMetrics", new BytesMetricsHandler(metrics, brokerId));
                            }
                            // mqtt encoder & decoder
                            p.addLast("encoder", new MqttEncoder());
                            p.addLast("decoder", new MqttDecoder());
                            // metrics
                            if (metricsEnabled) {
                                p.addLast("msgMetrics", new MessageMetricsHandler(metrics, brokerId));
                            }
                            // logic handler
                            p.addLast(handlerGroup, "logicHandler", new SyncRedisHandler(authenticator, communicator, redis, registry, validator, brokerId, keepAlive, keepAliveMax));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, brokerConfig.getInt("netty.soBacklog"))
                    .childOption(ChannelOption.SO_KEEPALIVE, brokerConfig.getBoolean("netty.soKeepAlive"));

            ChannelFuture sf = sslb.bind(host, sslport).sync();

        }

        logger.info("MQTT broker is up and running.");

        // Wait until the server socket is closed.
        // Do this to gracefully shut down the server.
        f.channel().closeFuture().sync();

    }

    /**
     * Loop and mark every clients currently connect to the broker as disconnect
     *
     * @param brokerId Broker Id
     * @param redis    Redis Storage
     */
    protected static void clearBrokerConnectionState(String brokerId, RedisSyncStorage redis) {
        ValueScanCursor<String> r = redis.getConnectedClients(brokerId, "0", 100);
        if (r.getValues() != null) {
            r.getValues().forEach(client -> redis.removeConnectedNode(client, brokerId));
        }
        while (!r.getCursor().equals("0")) {
            r = redis.getConnectedClients(brokerId, r.getCursor(), 100);
            if (r.getValues() != null) {
                r.getValues().forEach(client -> redis.removeConnectedNode(client, brokerId));
            }
        }
    }
}
