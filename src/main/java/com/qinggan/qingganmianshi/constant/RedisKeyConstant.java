package com.qinggan.qingganmianshi.constant;

/**
 * Description: Redis键名常量
 * Author: 1401687501x's
 * Date: 2024/9/25 23:35
 */
public interface RedisKeyConstant {

    String USER_SIGN_IN_REDIS_KEY_PREFIX = "mianshi:user:signins";

    static String getUserSignInRedisKey(int year, long userId){
        return String.format("%s:%s:%s",USER_SIGN_IN_REDIS_KEY_PREFIX,year,userId);
    }
}
