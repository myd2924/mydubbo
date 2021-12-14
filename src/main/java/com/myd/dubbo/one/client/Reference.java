package com.myd.dubbo.one.client;

import com.myd.dubbo.one.common.Protocol;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.UUID;

/**
 * @author <a href="mailto:mayuanding@qianmi.com">OF3787-马元丁</a>
 * @version 0.1.0
 * @Date:2021/12/13 09:57
 * @Description: 不知道具体类  只知道调用的哪个接口
 */
public class Reference implements InvocationHandler{

    private Protocol protocol = new Protocol();

    private Class interfaceClass;

    public Reference(Class interfaceClass){
        this.interfaceClass = interfaceClass;
    }

    public Object getReference(){
        return Proxy.newProxyInstance(Reference.class.getClassLoader(),new Class[]{interfaceClass},this);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        final String methodName = method.getName();
        Socket socket = new Socket("localhost",8899);
        final OutputStream outputStream = socket.getOutputStream();
        final byte[] requestBytes = protocol.packRequest(UUID.randomUUID().toString(), interfaceClass, methodName, method.getParameterTypes(), args);
        outputStream.write(requestBytes);
        outputStream.flush();

        InputStream inputStream = socket.getInputStream();
        byte[] recv = new byte[1024];
        int read = inputStream.read(recv);

        outputStream.close();
        inputStream.close();

        //解码器
        final Class<?> returnType = method.getReturnType();
        if(String.class == returnType){
            return new String(recv,0,read);
        } else if(int.class == returnType || Integer.class == returnType){
            return Integer.parseInt(new String(recv,0,read));
        }

        socket.close();


        return result;
    }
}
