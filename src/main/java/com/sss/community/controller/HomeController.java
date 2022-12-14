package com.sss.community.controller;

import com.sss.community.service.LikeService;
import com.sss.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.sss.community.entity.DiscussPost;
import com.sss.community.entity.Page;
import com.sss.community.entity.User;
import com.sss.community.service.DiscussPostService;
import com.sss.community.service.UserService;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @GetMapping("/index")
//    @ResponseBody
    public String getIndexPage(Model model, Page page, @RequestParam(name = "orderMode", defaultValue = "0") int orderMode){
        // 方法调用前，SpringMVC会自动实例化Model和Page，并将Page注入Model
        // 所以，在thymeleaf中可以直接访问Page对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode=" + orderMode);
        List<DiscussPost> list = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit(),orderMode);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);
                map.put("user", user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("orderMode", orderMode);
        return "/index";
    }

    @GetMapping("/error")
    public String getErrorPage() {
        return "/error/500";
    }

    @GetMapping("/denied")
    public String getDeniedePage(){
        return "/error/404";
    }
}
