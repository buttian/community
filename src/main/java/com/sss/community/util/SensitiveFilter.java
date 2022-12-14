package com.sss.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词过滤器，使用前缀树实现
 */
@Component
public class SensitiveFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    /**
     * 程序启动时，或初次调用时初始化好，只需要初始化一次，
     * 容器实例化以后，调用构造器，这个方法就会被调用，
     * 这个bean在服务启动时候初始化，所以这个方法在服务启动时候就会被调用。
     */
    @PostConstruct
    public void init() {

        try (
                InputStream is = this.getClass().getClassLoader()
                        .getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 添加到前缀树
                this.addKeyword(keyword);
            }

        } catch (IOException e) {
            LOGGER.error("加载敏感词文件失败：" + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 将一个敏感词添加到前缀树中
     *
     * @param keyword
     */
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if (subNode == null) {
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            // 指向子节点，进入下一轮循环
            tempNode = subNode;

            // 设置结束标志
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }

    }

    /**
     * 过滤敏感词
     * <p>
     * 对于敏感词中有符号的先去除符号
     *
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        // 指针1（指向树）
        TrieNode tempNode = rootNode;
        // 指针2（指向文本中敏感词首）
        int begin = 0;
        // 指针3（指向文本中敏感词尾）
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();
        while (position < text.length()) {
            char c = text.charAt(position);

            // 跳过符号
            if (isSymbol(c)) {
                // 若指针1处于根节点，将此符号计入结果，让指针2向下走一步
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                // 无论符号在开头或者中间，指针3都向下走一步
                position++;
                continue;
            }
            // 检查下级节点（因为从根节点开始）
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一个位置
                position = ++begin;
                // 重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                // 发现敏感词，将begin~position字符串替换掉
                sb.append(REPLACEMENT);
                // 进入下一个位置
                begin = ++position;
                // 重新指向根节点
                tempNode = rootNode;
            } else {
                // 检查下一个字符
                position++;

            }
        }
        //将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();

    }

    private boolean isSymbol(char c) {
        // 0x2E80 ~ 0x9FFF为东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    /**
     * 前缀树
     */
    private class TrieNode {

        // 关键词结束标志
        private boolean isKeywordEnd = false;

        // 子节点(key是下级字符，value是下级节点)
        private Map<Character, TrieNode> subNodes = new HashMap<>(16);

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        /**
         * 添加子节点
         *
         * @param c    字符
         * @param node 前缀树节点
         */
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        /**
         * 获取子节点
         *
         * @param c 字符
         * @return 子节点
         */
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }

    }
}
