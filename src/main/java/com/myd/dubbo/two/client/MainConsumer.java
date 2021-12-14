package com.myd.dubbo.two.client;

import com.myd.dubbo.two.common.RpcFramework;
import com.myd.dubbo.two.server.HelloService;

/**
 * @author <a href="mailto:mayuanding@qianmi.com">OF3787-马元丁</a>
 * @version 0.1.0
 * @Date:2021/12/14 10:57
 * @Description:
 */
public class MainConsumer {
    public static void main(String[] args) throws Exception {
        HelloService service = RpcFramework.refer(HelloService.class,"127.0.0.1",1234);
        final String hello = service.hello("大马曦");
        System.out.println(hello);

    }
}
