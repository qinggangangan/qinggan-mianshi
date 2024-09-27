package com.qinggan.qingganmianshi.aop;

import com.qinggan.qingganmianshi.annotation.DistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Description: 分布式锁 AOP
 * Author: 1401687501x's
 * Date: 2024/9/27 17:28
 */
@Aspect
@Component
public class DistributedLockAspect {

    @Resource
    private RedissonClient redissonClient;

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock){
        String key = distributedLock.key();
        long leaseTime = distributedLock.leaseTime();
        long waitTime = distributedLock.waitTime();
        TimeUnit timeunit = distributedLock.timeunit();

        RLock lock = redissonClient.getLock(key);
        boolean acquire = false;
        try {
            acquire = lock.tryLock(waitTime, leaseTime, timeunit);
            if(acquire){
                return joinPoint.proceed();
            }else {
                throw new RuntimeException("获取锁失败"+lock);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }finally {
            if (acquire){
                lock.unlock();
            }
        }
    }
}
