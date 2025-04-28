package cn.polister.dianmeetingsystem.utils;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedissonLock {

    @Autowired
    private RedissonClient redissonClient;

    public boolean lock(String key, long expireTime) {
        RLock lock = redissonClient.getLock(key);
        try {
            return lock.tryLock(expireTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void unlock(String key) {
        RLock lock = redissonClient.getLock(key);
        if (lock.isLocked()) {
            lock.unlock();
        }
    }

}