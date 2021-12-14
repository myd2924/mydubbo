package com.myd.dubbo.one.server;

import com.myd.dubbo.one.common.TravelService;

/**
 * @author <a href="mailto:mayuanding@qianmi.com">OF3787-马元丁</a>
 * @version 0.1.0
 * @Date:2021/12/13 09:58
 * @Description: 私家车出行
 */
public class CarService implements TravelService {

    @Override
    public String say(String ga) {
        return "hi,baby:"+ga;
    }

    @Override
    public int capacity() {
        return 5;
    }
}
