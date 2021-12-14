package com.myd.dubbo.three.common;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author <a href="mailto:mayuanding@qianmi.com">OF3787-马元丁</a>
 * @version 0.1.0
 * @Date:2021/12/14 15:07
 * @Description: 客户端handler
 */
public class ClientHandler extends ChannelInboundHandlerAdapter{
    ChannelHandlerContext context;
    Object result;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{
        super.channelActive(ctx);
        context = ctx;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
        result = msg;
        notify();
    }

    public synchronized Object invoke(RpcContent rpcContent)throws InterruptedException{
        context.writeAndFlush(rpcContent);
        wait();
        return result;
    }


}
