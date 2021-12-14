package com.myd.dubbo.one.common;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:mayuanding@qianmi.com">OF3787-马元丁</a>
 * @version 0.1.0
 * @Date:2021/12/13 09:56
 * @Description: 自定义协议
 *  协议类，用于组装、拆解交流的信息
 *
 */
public class Protocol {

    /**
     * 构建请求信息 发送数据包  client使用
     * @param requestId 请求唯一标识
     * @param targetClass 请求目标类
     * @param methodName 请求目标方法
     * @param paramType 目标方法参数类型
     * @param paraValues 目标方法参数值
     * @return
     */
    public byte[] packRequest(String requestId,Class targetClass,String methodName,Class[] paramType,Object[] paraValues){
        StringBuilder builder = new StringBuilder();
        //拼接消息
        // 以我命名的开头 myd-dubbo
        // 以空格“ ” 作为分隔符
        builder.append("myd-dubbo").append(Constant.BLACK_SPACE)
                .append(requestId).append(Constant.BLACK_SPACE)
                .append(targetClass.getName()).append(Constant.BLACK_SPACE)
                .append(methodName).append(Constant.BLACK_SPACE);

        //处理参数类型
        if(null != paramType && paramType.length>0){
            StringBuilder paramBuilder = new StringBuilder();
            for(Class type : paramType){
                paramBuilder.append(type.getName()).append(",");
            }
            //去掉尾部逗号
            final String substring = paramBuilder.toString().substring(0, paramBuilder.toString().length() - 1);
            builder.append(substring).append(Constant.BLACK_SPACE);
        }
        //处理参数值
        if(null != paraValues && paraValues.length>0){
            StringBuilder valueBuilder = new StringBuilder();
            for(Object value : paraValues){
                valueBuilder.append(value).append(",");
            }
            //去掉尾部逗号
            final String substring = valueBuilder.toString().substring(0, valueBuilder.toString().length() - 1);
            builder.append(substring);
        }
        return builder.toString().getBytes();
    }


    /**
     * 解析client的请求信息
     * @param bytes
     * @param len
     * @return
     */
    public Map<String,Object> unpackRequest(byte[] bytes,int len) throws IllegalAccessException {
        Map<String,Object> data = new ConcurrentHashMap<String, Object>();
        String receive = new String(bytes,0,len);
        final String[] result = receive.split(Constant.BLACK_SPACE);
        //校验 开头
        String startWith = result[0];
        if(!"myd-dubbo".equalsIgnoreCase(startWith)){
            throw new IllegalAccessException();
        }

        data.put(Constant.REQUEST_ID,result[1]);
        data.put(Constant.INSTANCE_NAME,result[2]);
        data.put(Constant.METHOD_NAME,result[3]);

        List<Class> paramTypeList = new ArrayList<Class>();
        if(result.length>4 && result[4]!=null){
            final String[] types = result[4].split(",");
            for(String type:types){
                paramTypeList.add(convertParamType(type));
            }
            data.put(Constant.PARAM_TYPE,paramTypeList);
        }

        if(result.length>5 && null!= result[5]){
            final String[] paramValues = result[5].split(",");
            List<Object> valueList = new ArrayList<Object>(paramValues.length);
            for(int i=0;i<paramValues.length;i++){
                valueList.add(convertParamValue(paramTypeList.get(i),paramValues[i]));
            }
            data.put(Constant.PARAM_VALUE,valueList);
        }

        return data;
    }

    /**
     * 简单写几个
     * @param type
     * @param paramValue
     * @return
     */
    private Object convertParamValue(Class type, String paramValue) {
        if(int.class.equals(type) || Integer.class.equals(type)){
            return Integer.valueOf(paramValue);
        } else if(ArrayList.class.equals(type)){
            return Arrays.asList(paramValue);
        } else if(Long.class.equals(type)){
            return  Long.valueOf(paramValue);
        } else if(Double.class.equals(type)){
            return Double.valueOf(paramValue);
        } else if(BigDecimal.class.equals(type)){
            return new BigDecimal(paramValue);
        } else {
            return paramValue;
        }
    }

    /**
     * 简单写几个
     * 对象类型如何转换 ？？ BeanDefinition
     * @param type
     * @return
     */
    private Class convertParamType(String type) {
        if("int".equals(type) || "Integer".equals(type)){
            return Integer.class;
        } else if("ArrayList".equals(type) || "java.util.ArrayList".equals(type)){
            return ArrayList.class;
        } else if("long".equalsIgnoreCase(type)){
            return  Long.class;
        } else if("double".equalsIgnoreCase(type)){
            return Double.class;
        } else if("BigDecimal".equalsIgnoreCase(type)){
            return BigDecimal.class;
        } else {
            return String.class;
        }
    }

}
