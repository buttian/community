package com.sss.community.controller;

import com.sss.community.entity.DiscussPost;
import com.sss.community.entity.Event;
import com.sss.community.event.EventProducer;
import com.sss.community.service.DiscussPostService;
import com.sss.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.sss.community.entity.Comment;
import com.sss.community.service.CommentService;
import com.sss.community.util.HostHolder;

import java.util.Date;


@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {
    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;


    /**
     * 添加评论
     *
     * @param discussPostId
     * @param comment
     * @return
     */

    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // 触发事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(comment.getUserId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
        //给帖子的评论
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        //回复
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentsById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            // 触发发帖事件
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
