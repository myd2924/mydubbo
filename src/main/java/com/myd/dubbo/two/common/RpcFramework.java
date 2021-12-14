package com.myd.dubbo.two.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

/**
 * @author <a href="mailto:mayuanding@qianmi.com">OF3787-马元丁</a>
 * @version 0.1.0
 * @Date:2021/12/14 09:50
 * @Description:
 */
public class RpcFramework {

    private static final  int GT_PORT = 65535;

    /**
     * 暴露服务
     * @param service
     * @param port
     * @throws Exception
     */
    public static void export(final Object service ,int port) throws Exception{
        if(null == service){
            throw new IllegalArgumentException("service instance is null");
        }
        if(port <= 0 || port > GT_PORT){
            throw new IllegalArgumentException("invalid port "+port);
        }

        ServerSocket server = new ServerSocket(port);
        for(;;){
            try{
                final Socket socket = server.accept();
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            final ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                            final ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                            try {
                                String str = (String)input.readObject();
                                final RpcRequest rpcRequest = JsonUtil.objectMapper.readValue(str, RpcRequest.class);
                                final String methodName = rpcRequest.getMethodName();
                                final Class<?>[] paramType = rpcRequest.getParamType();
                                final Object[] params = rpcRequest.getParams();

                                final Method method = service.getClass().getMethod(methodName, paramType);
                                final Object result = method.invoke(service, params);
                                System.out.println("return result "+result.toString());

                                output.writeObject(result);
                            } catch (Exception e) {
                                output.writeObject(e);
                                e.printStackTrace();
                            } finally {
                                input.close();
                                output.close();
                                socket.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 引用服务
     * @param interfaceClass
     * @param host
     * @param port
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T refer(final Class<T> interfaceClass,final String host,final int port) throws Exception{
        System.out.println("Get remote service "+ interfaceClass.getName() + " from server "+ host +":"+port);
        return (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = new Socket(host,port);
                try{
                    RpcRequest rpcRequest = new RpcRequest();

                    rpcRequest.setRequestId(UUID.randomUUID().toString());
                    rpcRequest.setClassName(interfaceClass.getName());
                    rpcRequest.setMethodName(method.getName());
                    rpcRequest.setParamType(method.getParameterTypes());
                    rpcRequest.setParams(args);

                    final String valueAsString = JsonUtil.objectMapper.writeValueAsString(rpcRequest);
                    final ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                    try{

                        outputStream.writeObject(valueAsString);
                        final ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                        try{

                            final Object result = inputStream.readObject();
                            if(result instanceof Throwable){
                                throw (Throwable)result;
                            }
                            return result;
                        } finally {
                            inputStream.close();
                        }
                    } finally {
                        outputStream.close();
                    }
                } finally {
                    socket.close();
                }
            }
        });
    }

}
