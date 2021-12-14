package com.myd.dubbo.one.common;

/**
 * @author <a href="mailto:mayuanding@qianmi.com">OF3787-马元丁</a>
 * @version 0.1.0
 * @Date:2021/12/13 09:54
 * @Description: 出行服务
 */
public interface TravelService {
    /**
     * 我是什么出行
     * @return
     */
    String say(String ga);

    /**
     * 容量
     * @return
     */
    int capacity();

}
