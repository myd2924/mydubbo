package com.myd.dubbo.one.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author <a href="mailto:mayuanding@qianmi.com">OF3787-马元丁</a>
 * @version 0.1.0
 * @Date:2021/12/13 09:59
 * @Description: 暴露服务 写死端口
 */
public class ServiceServer {

    private static volatile ServerSocket serverSocket;

    private ServiceServer(){
        throw new IllegalStateException();
    }

    public static ServerSocket getServerSocket() {
        if (null == serverSocket) {
            synchronized (ServiceServer.class) {
                if (null == serverSocket) {
                    try {
                        //无注册中心  写死端口
                        serverSocket = new ServerSocket(8899);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return serverSocket;
    }
}
