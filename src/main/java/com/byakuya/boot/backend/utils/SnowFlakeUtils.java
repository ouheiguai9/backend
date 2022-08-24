package com.byakuya.boot.backend.utils;

import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.util.Random;

/**
 * Created by 田伯光 at 2022/8/21 14:27
 */
public abstract class SnowFlakeUtils {

    //设置一个时间初始值    2^41 - 1   差不多可以用69年
    private static final long startTime = 1585644268888L;
    //5位的机器id
    private static final long workerIdBits = 5L;
    //5位的机房id
    private static final long datacenterIdBits = 5L;
    //每毫秒内产生的id数 2 的 12次方
    private static final long sequenceBits = 12L;
    // 这个是二进制运算，就是5 bit最多只能有31个数字，也就是说机器id最多只能是32以内
    private static final long maxWorkerId = ~(-1L << workerIdBits);
    // 这个是一个意思，就是5 bit最多只能有31个数字，机房id最多只能是32以内
    private static final long maxDatacenterId = ~(-1L << datacenterIdBits);
    private static final long workerIdShift = sequenceBits;
    private static final long datacenterIdShift = sequenceBits + workerIdBits;
    private static final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private static final long sequenceMask = ~(-1L << sequenceBits);


    private final static Generator instance;

    static  {
        long datacenterId = autoDatacenterId();
        instance = new Generator(datacenterId,autoWorkerId(datacenterId),0);
    }

    private static long autoDatacenterId() {
        long id = 0L;
        try {
            if(DockerUtils.isDocker()) {
                id = (DockerUtils.getDockerHost() + DockerUtils.getDockerPort()).hashCode();
            } else {
                NetworkInterface network = NetworkInterface.getByInetAddress(NetUtils.localAddress);
                byte[] mac = network.getHardwareAddress();
                id = ((0x000000FF & (long) mac[mac.length - 2]) | (0x0000FF00 & (((long) mac[mac.length - 1]) << 8))) >> 6;
            }
        } catch (Exception e) {
            id = new Random().nextLong();
        } finally{
            id = id % (maxDatacenterId + 1);
        }
        return id;
    }

    private static long autoWorkerId(long datacenterId) {
        StringBuilder sb = new StringBuilder();
        sb.append(datacenterId);
        try {
            //获取jvm进程信息
            String name = ManagementFactory.getRuntimeMXBean().getName();
            sb.append(name.split("@")[0]);
        } catch (Exception e) {
            sb.append(new Random().nextLong());
        }

        /*
         * MAC + PID 的 hashcode 获取16个低位
         */
        return (sb.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }


    public static long newId() {
        return instance.nextId();
    }

    private static class Generator {
        //因为二进制里第一个 bit 为如果是 1，那么都是负数，但是我们生成的 id 都是正数，所以第一个 bit 统一都是 0。

        //机器ID  2进制5位  32位减掉1位 31个
        private final long workerId;
        //机房ID 2进制5位  32位减掉1位 31个
        private final long datacenterId;
        //代表一毫秒内生成的多个id的最新序号  12位 4096 -1 = 4095 个
        private long sequence;
        //记录产生时间毫秒数，判断是否是同1毫秒
        private long lastTimestamp = -1L;

        public Generator(long workerId, long datacenterId, long sequence) {

            // 检查机房id和机器id是否超过31 不能小于0
            if (workerId > maxWorkerId || workerId < 0) {
                throw new IllegalArgumentException(
                        String.format("worker Id can't be greater than %d or less than 0",maxWorkerId));
            }

            if (datacenterId > maxDatacenterId || datacenterId < 0) {

                throw new IllegalArgumentException(
                        String.format("datacenter Id can't be greater than %d or less than 0",maxDatacenterId));
            }
            this.workerId = workerId;
            this.datacenterId = datacenterId;
            this.sequence = sequence;
        }

        // 这个是核心方法，通过调用nextId()方法，让当前这台机器上的snowflake算法程序生成一个全局唯一的id
        public synchronized long nextId() {
            // 这儿就是获取当前时间戳，单位是毫秒
            long timestamp = timeGen();
            if (timestamp < lastTimestamp) {

                System.err.printf(
                        "clock is moving backwards. Rejecting requests until %d.", lastTimestamp);
                throw new RuntimeException(
                        String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                                lastTimestamp - timestamp));
            }

            // 下面是说假设在同一个毫秒内，又发送了一个请求生成一个id
            // 这个时候就得把seqence序号给递增1，最多就是4096
            if (lastTimestamp == timestamp) {

                // 这个意思是说一个毫秒内最多只能有4096个数字，无论你传递多少进来，
                //这个位运算保证始终就是在4096这个范围内，避免你自己传递个sequence超过了4096这个范围
                sequence = (sequence + 1) & sequenceMask;
                //当某一毫秒的时间，产生的id数 超过4095，系统会进入等待，直到下一毫秒，系统继续产生ID
                if (sequence == 0) {
                    timestamp = tilNextMillis(lastTimestamp);
                }

            } else {
                sequence = 0;
            }
            // 这儿记录一下最近一次生成id的时间戳，单位是毫秒
            lastTimestamp = timestamp;
            // 这儿就是最核心的二进制位运算操作，生成一个64bit的id
            // 先将当前时间戳左移，放到41 bit那儿；将机房id左移放到5 bit那儿；将机器id左移放到5 bit那儿；将序号放最后12 bit
            // 最后拼接起来成一个64 bit的二进制数字，转换成10进制就是个long型
            return ((timestamp - startTime) << timestampLeftShift) |
                    (datacenterId << datacenterIdShift) |
                    (workerId << workerIdShift) | sequence;
        }

        /**
         * 当某一毫秒的时间，产生的id数 超过4095，系统会进入等待，直到下一毫秒，系统继续产生ID
         * @param lastTimestamp 最后时间戳
         * @return 下一毫秒
         */
        private long tilNextMillis(long lastTimestamp) {

            long timestamp = timeGen();

            while (timestamp <= lastTimestamp) {
                timestamp = timeGen();
            }
            return timestamp;
        }
        //获取当前时间戳
        private long timeGen(){
            return System.currentTimeMillis();
        }


    }
}
