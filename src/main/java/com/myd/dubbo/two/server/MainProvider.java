package com.myd.dubbo.two.server;

import com.myd.dubbo.two.common.RpcFramework;

/**
 * @author <a href="mailto:mayuanding@qianmi.com">OF3787-马元丁</a>
 * @version 0.1.0
 * @Date:2021/12/14 10:55
 * @Description:
 */
public class MainProvider {
    public static void main(String[] args) throws Exception {
        HelloServiceImp service = new HelloServiceImp();
        RpcFramework.export(service,1234);
    }
}
