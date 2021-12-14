package com.myd.dubbo.three.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="mailto:mayuanding@qianmi.com">OF3787-马元丁</a>
 * @version 0.1.0
 * @Date:2021/12/14 14:35
 * @Description: 报文传输类
 */
@Data
public class RpcContent implements Serializable{
    private static final long serialVersionUID = 3931115383253800227L;

    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] paramType;
    private Object[] params;

    private Object result;

}
