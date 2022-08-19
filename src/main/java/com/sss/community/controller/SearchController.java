package com.sss.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.sss.community.entity.DiscussPost;
import com.sss.community.entity.Page;
import com.sss.community.service.ElasticsearchService;
import com.sss.community.service.LikeService;
import com.sss.community.service.UserService;
import com.sss.community.util.CommunityConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticSearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    /**
     * 搜索帖子
     * search?keyword=xxx
     *
     * @param keyword
     * @param page
     * @param model
     * @return
     */
    @GetMapping("/search")
    public String search(String keyword, Page page, Model model) {
        // 搜索帖子
        org.springframework.data.domain.Page<DiscussPost> searchResult =
                elasticSearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());

        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (searchResult != null) {
            for (DiscussPost post : searchResult) {
                Map<String, Object> map = new HashMap<>(16);
                // 帖子
                map.put("post", post);
                // 作者
                map.put("user", userService.findUserById(post.getUserId()));
                // 点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());
        return "/site/search";
    }
}
