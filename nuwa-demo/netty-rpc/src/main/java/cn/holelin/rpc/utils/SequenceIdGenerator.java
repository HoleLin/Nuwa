package cn.holelin.rpc.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description: 发号器
 * @Author: HoleLin
 * @CreateDate: 2023/4/13 10:09
 * @UpdateUser: HoleLin
 * @UpdateDate: 2023/4/13 10:09
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class SequenceIdGenerator {

    private static AtomicInteger ATOMIC_INTEGER = new AtomicInteger();


    public static Integer nextId() {
        return ATOMIC_INTEGER.incrementAndGet();
    }

    public static void main(String[] args) {
        System.out.println(nextId());
        System.out.println(nextId());
        System.out.println(nextId());
        System.out.println(nextId());
        System.out.println(nextId());
    }
}
