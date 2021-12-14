package com.myd.dubbo.two.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author <a href="mailto:mayuanding@qianmi.com">OF3787-马元丁</a>
 * @version 0.1.0
 * @Date:2021/12/13 19:48
 * @Description:
 */
@Getter
@Setter
public class RpcRequest implements Serializable{

    private static final long serialVersionUID = 8981278702276523807L;

    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] paramType;
    private Object[] params;

}
