package com.myd.dubbo.one.client;

import com.myd.dubbo.one.common.TravelService;

/**
 * @author <a href="mailto:mayuanding@qianmi.com">OF3787-马元丁</a>
 * @version 0.1.0
 * @Date:2021/12/13 09:57
 * @Description:
 */
public class MainClient {
    public static void main(String[] args) {
        Reference reference = new Reference(TravelService.class);
        TravelService  travelService = (TravelService)reference.getReference();

        final String myd = travelService.say("myd");
        System.out.println(myd);
        System.out.println("获取容量："+travelService.capacity());

    }
}
