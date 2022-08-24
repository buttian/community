package com.sss.community;

import com.sss.community.dao.LoginTicketMapper;
import com.sss.community.entity.LoginTicket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import com.sss.community.dao.DiscussPostMapper;
import com.sss.community.dao.UserMapper;
import com.sss.community.entity.DiscussPost;
import com.sss.community.entity.User;

import java.util.Date;
import java.util.List;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TestMapper {
    @Autowired
    private UserMapper userMapper;
    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }
    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.abc.com/1.png");
        user.setCreateTime(new Date());
        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }
    @Test
    public void updateUser(){
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "hello");
        System.out.println(rows);
    }
    @Test
    public void test(){
        System.out.println(Integer.parseInt("-001111", 10));
    }
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Test
    public void testSelectPosts(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0, 10,0);
        for(DiscussPost post : list){
            System.out.println(post);
        }
        int count = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(count);
    }

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testInsert() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(1);
        loginTicket.setStatus(0);
        loginTicket.setTicket("dddd");
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 *10));
        int ret = loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(ret);
    }

    @Test
    public void testSelectByTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("dddd");
        System.out.println(loginTicket);
    }

    @Test
    public void testUpdateStatus() {
        int ret = loginTicketMapper.updateStatus("dddd", 1);
        System.out.println(ret);
    }

}
