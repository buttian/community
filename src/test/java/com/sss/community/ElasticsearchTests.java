package com.sss.community;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import com.sss.community.dao.DiscussPostMapper;
import com.sss.community.dao.elasticsearch.DiscussPostRepository;
import com.sss.community.entity.DiscussPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTests {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;


    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


    @Test
    public void testInsert() {
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(243));
    }

    @Test
    public void testUpdate() {
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(231));
        DiscussPost post = discussPostMapper.selectDiscussPostById(231);
        post.setContent("???????????????????????????");
        discussPostRepository.save(post);
    }

    /**
     * ?????????????????????
     */
    @Test
    public void testInsertList() {
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102, 0, 100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103, 0, 100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111, 0, 100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112, 0, 100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131, 0, 100,0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133, 0, 100,0));
    }

    @Test
    public void testDelete() {
        discussPostRepository.deleteById(231);
        // discussPostRepository.deleteAll();
    }

    @Test
    public void testSearchByRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("???????????????", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(pageable)
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        // ??????????????????????????????????????????????????????
        // Page<DiscussPost> page = discussPostRepository.search(searchQuery);

        SearchHits<DiscussPost> searchHits = elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);
        if (searchHits.getTotalHits() <= 0) {
            return;
        }
        List<DiscussPost> list = new ArrayList<>();
        for (SearchHit<DiscussPost> hit : searchHits) {
            DiscussPost content = hit.getContent();
            DiscussPost post = new DiscussPost();
            BeanUtils.copyProperties(content, post);
            // ????????????
//            Map<String, List<String>> highlightFields = hit.getHighlightFields();
//            for (Map.Entry<String, List<String>> stringHighlightFieldEntry : highlightFields.entrySet()) {
//                String key = stringHighlightFieldEntry.getKey();
//                if(StringUtils.equals(key, "title")){
//                    List<String> fragments = stringHighlightFieldEntry.getValue();
//                    StringBuilder sb = new StringBuilder();
//                    for (String fragment : fragments) {
//                        sb.append(fragment);
//                    }
//                    post.setTitle(sb.toString());
//                }
//                if(StringUtils.equals(key, "content")){
//                    List<String> fragments = stringHighlightFieldEntry.getValue();
//                    StringBuilder sb = new StringBuilder();
//                    for (String fragment : fragments) {
//                        sb.append(fragment);
//                    }
//                    post.setContent(sb.toString());
//                }
//            }
            // ????????????
            List<String> list1 = hit.getHighlightFields().get("title");
            if (list1 != null) {
                post.setTitle(list1.get(0));
            }
            List<String> list2 = hit.getHighlightFields().get("content");
            if (list2 != null) {
                post.setContent(list2.get(0));
            }
            list.add(post);


        }
//        List<DiscussPost> searchDiscussPost = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<DiscussPost> page = new PageImpl<>(list, pageable, searchHits.getTotalHits());

        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for (DiscussPost post : page) {
            System.out.println(post);
        }
    }


    @Test
    public void test() {
        search("?????????", 1);
    }

    private static final Integer ROWS = 10;

    public void search(String keyWord, Integer page) {
        List<DiscussPost> list = new ArrayList();
        Pageable pageable = PageRequest.of(page - 1, ROWS); // ??????????????????
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("title", keyWord).operator(Operator.AND)) // match??????
                .withPageable(pageable).withHighlightBuilder(getHighlightBuilder("title")) // ????????????
                .build();
        SearchHits<DiscussPost> searchHits = this.elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);
        for (SearchHit<DiscussPost> searchHit : searchHits) { // ????????????????????????

            DiscussPost content = searchHit.getContent();
            DiscussPost discussPost = new DiscussPost();
            BeanUtils.copyProperties(content, discussPost);

            // ????????????
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            for (Map.Entry<String, List<String>> stringHighlightFieldEntry : highlightFields.entrySet()) {
                String key = stringHighlightFieldEntry.getKey();
                if (StringUtils.equals(key, "title")) {
                    List<String> fragments = stringHighlightFieldEntry.getValue();
                    StringBuilder sb = new StringBuilder();
                    for (String fragment : fragments) {
                        sb.append(fragment);
                    }
                    discussPost.setTitle(sb.toString());
                }

            }
            list.add(discussPost);
        }
        Page<DiscussPost> all = new PageImpl<>(list, pageable, searchHits.getTotalHits());
        System.out.println(all.getTotalElements());
        System.out.println(all.getTotalPages());
        System.out.println(all.getNumber());
        System.out.println(all.getSize());
        for (DiscussPost post : all) {
            System.out.println(post);
        }
    }

    // ??????????????????
    private HighlightBuilder getHighlightBuilder(String... fields) {
        // ????????????
        HighlightBuilder highlightBuilder = new HighlightBuilder(); // ?????????????????????
        for (String field : fields) {
            highlightBuilder.field(field);// ??????????????????
        }
        highlightBuilder.requireFieldMatch(false); // ???????????????????????????,????????????false
        highlightBuilder.preTags("<span style=\"color:red\">"); // ????????????
        highlightBuilder.postTags("</span>");
        // ???????????????,?????????????????????????????????????????????????????????,????????????,???????????????????????????,?????????????????????
        highlightBuilder.fragmentSize(800000); // ?????????????????????
        highlightBuilder.numOfFragments(0); // ????????????????????????????????????

        return highlightBuilder;
    }


}
