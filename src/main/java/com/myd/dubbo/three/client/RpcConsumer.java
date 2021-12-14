package com.myd.dubbo.three.client;

import com.myd.dubbo.three.common.RpcClient;
import com.myd.dubbo.three.server.HelloService;

/**
 * @author <a href="mailto:mayuanding@qianmi.com">OF3787-马元丁</a>
 * @version 0.1.0
 * @Date:2021/12/14 15:28
 * @Description: 客户调用
 */
public class RpcConsumer {
    public static void main(String[] args) {
        HelloService service = RpcClient.refer(HelloService.class);
        System.out.println(service.hello("马晨曦"));
        System.out.println(service.hello("netty"));

    }
}
