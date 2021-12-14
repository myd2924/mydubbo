package com.myd.dubbo.three.server;

import com.myd.dubbo.three.common.RpcServer;

/**
 * @author <a href="mailto:mayuanding@qianmi.com">OF3787-马元丁</a>
 * @version 0.1.0
 * @Date:2021/12/14 15:13
 * @Description:
 */
public class ServerProvider {
    public static void main(String[] args) throws Exception {
        RpcServer rpcServer = new RpcServer();
        rpcServer.addService(HelloService.class.getName(),new HelloServiceImp());
        rpcServer.startRpcServer(1234);
    }
}
