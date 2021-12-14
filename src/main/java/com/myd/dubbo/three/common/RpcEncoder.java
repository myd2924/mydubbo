package com.myd.dubbo.three.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author <a href="mailto:mayuanding@qianmi.com">OF3787-马元丁</a>
 * @version 0.1.0
 * @Date:2021/12/14 14:43
 * @Description: 编码器
 */
public class RpcEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        final byte[] bytes = JsonUtil.objectMapper.writeValueAsBytes(o);
        byteBuf.writeBytes(bytes);
    }
}
