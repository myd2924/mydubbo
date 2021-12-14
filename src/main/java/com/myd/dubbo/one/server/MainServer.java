package com.myd.dubbo.one.server;

import com.myd.dubbo.one.common.TravelService;

/**
 * @author <a href="mailto:mayuanding@qianmi.com">OF3787-马元丁</a>
 * @version 0.1.0
 * @Date:2021/12/13 09:58
 * @Description: 服务端起服务
 */
public class MainServer {
    public static void main(String[] args) {
        Service service = new Service(TravelService.class,new CarService());
        service.export();
    }
}
