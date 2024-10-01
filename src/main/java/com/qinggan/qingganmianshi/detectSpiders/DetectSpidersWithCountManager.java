package com.qinggan.qingganmianshi.detectSpiders;

import cn.dev33.satoken.stp.StpUtil;
import com.qinggan.qingganmianshi.common.ErrorCode;
import com.qinggan.qingganmianshi.exception.BusinessException;
import com.qinggan.qingganmianshi.manager.CountManager;
import com.qinggan.qingganmianshi.model.entity.User;
import com.qinggan.qingganmianshi.service.UserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Description: 利用CountManager检测爬虫
 * Author: 1401687501x's
 * Date: 2024/10/1 20:32
 */
@Component
public class DetectSpidersWithCountManager {

    @Resource
    private CountManager countManager;

    @Resource
    private UserService userService;

    /**
     * 检测爬虫
     *
     * @param loginUserId
     */
    public void crawlerDetect(long loginUserId){
        // 调用多少次时告警
        final int WARN_COUNT = 10;
        // 超过多少次封号
        final int BAN_COUNT = 20;

        String key = String.format("user:access:%s",loginUserId);
        long count = countManager.incrAndGetCount(key, 1, TimeUnit.MINUTES, 180);

        if(count > BAN_COUNT){
            //踢下线
            StpUtil.kickout(loginUserId);
            //封号
            User updateuser = new User();
            updateuser.setId(loginUserId);
            updateuser.setUserRole("ban");
            userService.updateById(updateuser);
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "访问太频繁，已被封号");
        }

        // 是否告警
        if (count == WARN_COUNT) {
            // 可以修改逻辑,比如改为向管理员发送邮件通知
            throw new BusinessException(110, "警告访问太频繁");
        }
    }
}
