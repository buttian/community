package com.sss.community.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.sss.community.entity.DiscussPost;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    /**
     * 分页查询帖子
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit,  @Param("orderMode") int orderMode);

    /**
     * <p>@Param注解用于给定参数取别名</p>
     * 如果只有一个参数，并且在if里面使用，则必须加别名
     *
     * @param userId
     * @return
     */
    int selectDiscussPostRows (@Param("userId") int userId);

    /**
     * 插入一条帖子
     *
     * @param discussPost
     * @return
     */
    int insertDiscussPost(DiscussPost discussPost);

    /**
     * 通过id查找帖子
     *
     * @param id
     * @return
     */
    DiscussPost selectDiscussPostById(int id);

    /**
     * 更新帖子评论数量
     *
     * @param id
     * @param commentCount
     * @return
     */
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

    /**
     * 更新帖子类型
     * 0：普通
     * 1：置顶
     *
     * @param id
     * @param type
     * @return
     */
    int updateType(@Param("id") int id, @Param("type") int type);

    /**
     * 更新帖子状态
     * 0：正常
     * 1：精华
     * 2：拉黑
     *
     * @param id
     * @param status
     * @return
     */
    int updateStatus(@Param("id") int id, @Param("status") int status);

    /**
     * 更新帖子分数
     *
     * @param id
     * @param score
     */
    int updateScore(@Param("id") int id, @Param("score") double score);
}
