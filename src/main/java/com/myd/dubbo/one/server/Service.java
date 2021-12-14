package com.myd.dubbo.one.server;

import com.myd.dubbo.one.common.Constant;
import com.myd.dubbo.one.common.Protocol;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:mayuanding@qianmi.com">OF3787-马元丁</a>
 * @version 0.1.0
 * @Date:2021/12/13 09:58
 * @Description: 服务发布
 */
public class Service<T> {

    private static ConcurrentHashMap<String,Object> beanMap = new ConcurrentHashMap<String, Object>();

    /**协议*/
    private Protocol protocol = new Protocol();
    /**接口实现类*/
    private T object;
    /**接口类型*/
    private Class interfaceClass;

    public Service(Class interfaceClass,T object){
        //校验 对象是否实现了接口
        final boolean assignableFrom = interfaceClass.isAssignableFrom(object.getClass());
        if(!assignableFrom){
            throw new IllegalArgumentException("object 必须实现interfaceClass接口");
        }
        this.interfaceClass = interfaceClass;
        this.object = object;
        //缓存起来 方便使用
        beanMap.put(interfaceClass.getName(),object);

    }

    public void export(){
        try{
            final ServerSocket serverSocket = ServiceServer.getServerSocket();
            while(true){
                //阻塞 等待客户端连接
                Socket socket = serverSocket.accept();
                //响应结果
                byte[] result = new byte[1];
                final InputStream inputStream = socket.getInputStream();
                byte[] recv = new byte[1024];
                final int len = inputStream.read(recv);
                //根据协议  转换消息
                final Map<String, Object> requestMap = protocol.unpackRequest(recv, len);
                //调用的接口
                final Object interfaceName = requestMap.get(Constant.INSTANCE_NAME);
                //接口实现类对象
                final Object object = beanMap.get(interfaceName);
                if(null == object){
                    result = "请求方法不存在".getBytes();
                } else {

                    //方法名
                    final Object menthodName = requestMap.get(Constant.METHOD_NAME);

                    //参数类型
                    Class[] paramTypes = null;
                    final List<Class> paramTypeList = (List<Class>)requestMap.get(Constant.PARAM_TYPE);
                    if(null != paramTypeList  && paramTypeList.size()>0){
                        paramTypes = paramTypeList.toArray(new Class[paramTypeList.size()]);
                    }

                    //参数值
                    Object[] paramValues = null;
                    final List<Object> paramValueList = (List<Object>)requestMap.get(Constant.PARAM_VALUE);
                    if(paramValueList != null && paramValueList.size()>0){
                        paramValues = paramValueList.toArray();
                    }

                    //获取调用的方法
                    final Method method = object.getClass().getMethod(menthodName.toString(), paramTypes);
                    final Object invoke = method.invoke(object, paramValues);
                    System.out.println(invoke);
                    if(invoke != null){
                        result = invoke.toString().getBytes();
                    }

                }

                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(result);
                outputStream.flush();
                outputStream.close();
                inputStream.close();


            }
        } catch (Exception e){
                e.printStackTrace();
        }

    }

}
