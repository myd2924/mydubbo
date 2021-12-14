package com.myd.dubbo.three.common;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:mayuanding@qianmi.com">OF3787-马元丁</a>
 * @version 0.1.0
 * @Date:2021/12/14 14:47
 * @Description: rpc服务类
 */
public class RpcServer {

    ConcurrentHashMap<String,Object> serviceMap = new ConcurrentHashMap<String, Object>();

    /**注册服务*/
    public void addService(String serviceName,Object service){
        serviceMap.put(serviceName,service);
    }

    /**
     * 1.bossGroup 用于接收连接，
     *   workerGroup 用于具体的处理
     *        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
     *        EventLoopGroup workerGroup = new NioEventLoopGroup();
     *        try {
     * 2.创建服务端启动引导/辅助类：ServerBootstrap ServerBootstrap b = new ServerBootstrap();
     * 3.给引导类配置两大线程组,确定了线程模型 b.group(bossGroup, workerGroup)
     *  (非必备)打印日志 .handler(new LoggingHandler(LogLevel.INFO))
     * 4.指定 IO 模型 .channel(NioServerSocketChannel.class)
     *              .childHandler(new ChannelInitializer<SocketChannel>()
     *              { @Override public void initChannel(SocketChannel ch)
     *              { ChannelPipeline p = ch.pipeline();
     * 5.可以自定义客户端消息的业务处理逻辑 p.addLast(new HelloServerHandler()); } });
     * 6.绑定端口,调用 sync 方法阻塞知道绑定完成 ChannelFuture f = b.bind(port).sync();
     * 7.阻塞等待直到服务器Channel关闭(closeFuture()方法获取Channel 的CloseFuture对象,然后调用sync()方法)
     *                  f.channel().closeFuture().sync(); }
     * 8.优雅关闭相关线程组资源 finally {bossGroup.shutdownGracefully(); workerGroup.shutdownGracefully(); }
     *
     * @param port
     * @throws Exception
     */
    public void startRpcServer(int port) throws Exception{
        //bossGroup 用于接收连接
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //用于具体的处理
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try{
            //创建服务端启动引导/辅助类
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup,workGroup)
                    //指定 IO 模型
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            System.out.println("服务启动。。。");
                            nioSocketChannel.pipeline().addLast(new RpcDecoder());
                            nioSocketChannel.pipeline().addLast(new RpcEncoder());
                            //自定义客户端消息的业务处理逻辑
                            nioSocketChannel.pipeline().addLast(new SimpleChannelInboundHandler() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
                                    RpcContent rpcContent =(RpcContent)msg;
                                    final Object service = serviceMap.get(rpcContent.getClassName());
                                    final String methodName = rpcContent.getMethodName();
                                    final Class<?>[] paramType = rpcContent.getParamType();
                                    final Object[] params = rpcContent.getParams();
                                    final Method method = service.getClass().getMethod(methodName, paramType);
                                    final Object result = method.invoke(service, params);
                                    System.out.println("server return "+result.toString());
                                    RpcContent rtn = new RpcContent();
                                    rtn.setResult(result);
                                    rtn.setRequestId(rpcContent.getRequestId());
                                    channelHandlerContext.writeAndFlush(rtn);
                                }
                            });

                        }
                    })
                    //绑定端口
                    .option(ChannelOption.SO_BACKLOG,1024)
                    //保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            //阻塞等待直到服务器
            ChannelFuture future = server.bind(port).sync();
            System.out.println("start on port: "+port);
            future.channel().closeFuture().sync();
        } finally {
            //优雅关闭相关线程组资源
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }
}
