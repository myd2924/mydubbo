package com.myd.dubbo.two.server;

/**
 * @author <a href="mailto:mayuanding@qianmi.com">OF3787-马元丁</a>
 * @version 0.1.0
 * @Date:2021/12/14 10:54
 * @Description:
 */
public class HelloServiceImp implements HelloService {

    public String hello(String name) {
        return "我的名字是："+name;
    }

}
