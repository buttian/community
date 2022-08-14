package com.sss.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import com.sss.community.dao.MessageMapper;
import com.sss.community.entity.Message;

import java.util.List;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MessageMapperTest {
    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void test() {
        List<Message> list = messageMapper.selectConversations(111, 0, 20);
        for (Message message : list) {
            System.out.println(message);
        }
        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        list = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : list) {
            System.out.println(message);
        }
        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);

        count = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(count);

    }
}
