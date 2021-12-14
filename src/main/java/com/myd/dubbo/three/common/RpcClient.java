package com.myd.dubbo.three.common;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author <a href="mailto:mayuanding@qianmi.com">OF3787-马元丁</a>
 * @version 0.1.0
 * @Date:2021/12/14 15:15
 * @Description:
 */
public class RpcClient {

    private static ClientHandler client;

    /**
     * 1.创建一个 NioEventLoopGroup 对象实例 EventLoopGroup group = new NioEventLoopGroup();
     * 2.创建客户端启动引导/辅助类：Bootstrap Bootstrap b = new Bootstrap();
     * 3.指定线程组 b.group(group)
     * 4.指定 IO 模型 .channel(NioSocketChannel.class)
     *               .handler(new ChannelInitializer<SocketChannel>() {
     *               @Override public void initChannel(SocketChannel ch) throws Exception {
     *                  ChannelPipeline p = ch.pipeline();
     * 5.这里可以自定义消息的业务处理逻辑 p.addLast(new HelloClientHandler(message)); } });
     * 6.尝试建立连接 ChannelFuture f = b.connect(host, port).sync();
     * 7.等待连接关闭（阻塞，直到Channel关闭） f.channel().closeFuture().sync(); } finally { group.shutdownGracefully(); }
     */
    public static void init(){
        client = new ClientHandler();
        //创建一个 NioEventLoopGroup
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            //创建引导类
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                        //指定IO模型
                        .channel(NioSocketChannel.class)
                        //自定义业务处理器
                        .handler(new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(new RpcEncoder());
                                pipeline.addLast(new RpcDecoder());
                                pipeline.addLast(client);
                            }
                        });
            //尝试连接
            bootstrap.connect("127.0.0.1",1234).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅关闭
            group.shutdownGracefully();
        }
    }

    public static <T> T refer(final Class<T> interfaceClass){
        System.out.println("Get remote service " + interfaceClass.getName() );
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if(null == client){
                    init();
                }
                RpcContent rpcContent = new RpcContent();
                rpcContent.setRequestId(UUID.randomUUID().toString());
                rpcContent.setClassName(interfaceClass.getName());
                rpcContent.setMethodName(method.getName());
                rpcContent.setParamType(method.getParameterTypes());
                rpcContent.setParams(args);

                final RpcContent result = (RpcContent)client.invoke(rpcContent);
                return result.getResult();
            }
        });
    }
}
