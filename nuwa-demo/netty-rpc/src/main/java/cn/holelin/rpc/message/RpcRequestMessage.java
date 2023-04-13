package cn.holelin.rpc.message;

import lombok.Data;

/**
 * @Description: RPC 请求消息
 * @Author: HoleLin
 * @CreateDate: 2023/4/13 09:20
 * @UpdateUser: HoleLin
 * @UpdateDate: 2023/4/13 09:20
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
@Data
public class RpcRequestMessage extends Message {

    /**
     * 接口全限定名
     */
    private String interfaceFullName;

    /**
     * 接口中方法的名称
     */
    private String methodName;

    /**
     * 方法参数的类型数组
     */
    private Class[] parameterTypes;

    /**
     * 方法参数的实际值数组
     */
    private Object[] parameterValue;

    /**
     * 方法返回值类型
     */
    private Class<?> returnType;

    public RpcRequestMessage(int sequenceId, String interfaceFullName, String methodName, Class<?> returnType, Class[] parameterTypes, Object[] parameterValue) {
        super.setSequenceId(sequenceId);
        this.interfaceFullName = interfaceFullName;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValue = parameterValue;
    }

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_REQUEST;
    }
}
