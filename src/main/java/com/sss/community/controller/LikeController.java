package com.sss.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.sss.community.entity.User;
import com.sss.community.service.LikeService;
import com.sss.community.util.CommunityUtil;
import com.sss.community.util.HostHolder;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    /**
     * 点赞
     *
     * @param entityType
     * @param entityId
     * @return
     */
    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId) {
        User user = hostHolder.getUser();
        // 点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        // 数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        // 返回的结果
        Map<String, Object> map = new HashMap<>(16);
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        return CommunityUtil.getJSONString(0, null, map);
    }
}
