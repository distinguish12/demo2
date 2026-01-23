package com.edu.modules.discussion;

import com.edu.modules.course.entity.Course;
import com.edu.modules.course.service.CourseService;
import com.edu.modules.discussion.entity.Discussion;
import com.edu.modules.discussion.entity.DiscussionReply;
import com.edu.modules.discussion.service.DiscussionReplyService;
import com.edu.modules.discussion.service.DiscussionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 讨论区模块测试
 */
@SpringBootTest
public class DiscussionModuleTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private DiscussionService discussionService;

    @Autowired
    private DiscussionReplyService discussionReplyService;

    /**
     * 测试讨论帖管理功能
     */
    @Test
    public void testDiscussionManagement() {
        // 创建测试课程
        Course course = new Course();
        course.setTitle("讨论测试课程");
        course.setInstructorId(2L);
        Course createdCourse = courseService.createCourse(course);

        Long userId = 1L; // 假设用户ID为1

        // 创建讨论帖
        Discussion discussion = new Discussion();
        discussion.setCourseId(createdCourse.getId());
        discussion.setUserId(userId);
        discussion.setTitle("Java学习问题讨论");
        discussion.setContent("我在学习Java的过程中遇到了一些问题，想和大家交流一下。");

        Discussion createdDiscussion = discussionService.createDiscussion(discussion);
        System.out.println("讨论帖创建成功: " + createdDiscussion.getId());

        // 更新讨论帖
        discussion.setId(createdDiscussion.getId());
        discussion.setContent("更新后的讨论内容：我在学习Java的过程中遇到了一些问题，想和大家交流一下。");
        boolean updateResult = discussionService.updateDiscussion(discussion);
        System.out.println("讨论帖更新结果: " + updateResult);

        // 查询讨论帖
        Discussion queriedDiscussion = discussionService.getById(createdDiscussion.getId());
        System.out.println("查询讨论帖: " + queriedDiscussion.getTitle());

        // 查询课程讨论帖列表
        List<Discussion> courseDiscussions = discussionService.getDiscussionsByCourseId(createdCourse.getId());
        System.out.println("课程讨论帖数量: " + courseDiscussions.size());

        // 增加查看数
        discussionService.incrementViewCount(createdDiscussion.getId());
        System.out.println("查看数已增加");

        // 置顶操作
        discussionService.toggleTop(createdDiscussion.getId());
        System.out.println("讨论帖置顶状态已切换");

        // 精华操作
        discussionService.toggleEssence(createdDiscussion.getId());
        System.out.println("讨论帖精华状态已切换");

        // 清理测试数据
        discussionService.deleteDiscussion(createdDiscussion.getId());
        courseService.deleteCourse(createdCourse.getId());
    }

    /**
     * 测试讨论回复功能
     */
    @Test
    public void testDiscussionReply() {
        // 创建测试数据
        Course course = new Course();
        course.setTitle("回复测试课程");
        course.setInstructorId(2L);
        Course createdCourse = courseService.createCourse(course);

        Long userId = 1L;
        Discussion discussion = new Discussion();
        discussion.setCourseId(createdCourse.getId());
        discussion.setUserId(userId);
        discussion.setTitle("测试讨论帖");
        discussion.setContent("这是一个测试讨论帖");
        Discussion createdDiscussion = discussionService.createDiscussion(discussion);

        // 创建回复
        DiscussionReply reply1 = new DiscussionReply();
        reply1.setDiscussionId(createdDiscussion.getId());
        reply1.setUserId(userId);
        reply1.setContent("这是一个回复内容");
        reply1.setParentId(0L); // 顶级回复

        DiscussionReply createdReply1 = discussionReplyService.createReply(reply1);
        System.out.println("回复创建成功: " + createdReply1.getId());

        // 创建子回复
        DiscussionReply reply2 = new DiscussionReply();
        reply2.setDiscussionId(createdDiscussion.getId());
        reply2.setUserId(userId);
        reply2.setContent("这是对上面回复的回复");
        reply2.setParentId(createdReply1.getId()); // 回复第一个回复

        DiscussionReply createdReply2 = discussionReplyService.createReply(reply2);
        System.out.println("子回复创建成功: " + createdReply2.getId());

        // 查询回复列表
        List<DiscussionReply> replies = discussionReplyService.getRepliesByDiscussionId(createdDiscussion.getId());
        System.out.println("讨论帖回复数量: " + replies.size());

        for (DiscussionReply reply : replies) {
            System.out.println("回复内容: " + reply.getContent() + ", 父回复ID: " + reply.getParentId());
        }

        // 点赞操作
        discussionReplyService.toggleLike(createdReply1.getId(), userId);
        int likeCount = discussionReplyService.getLikeCount(createdReply1.getId());
        System.out.println("回复点赞数: " + likeCount);

        // 检查点赞状态
        boolean hasLiked = discussionReplyService.hasUserLiked(createdReply1.getId(), userId);
        System.out.println("用户是否已点赞: " + hasLiked);

        // 清理测试数据
        discussionReplyService.deleteReply(createdReply2.getId());
        discussionReplyService.deleteReply(createdReply1.getId());
        discussionService.deleteDiscussion(createdDiscussion.getId());
        courseService.deleteCourse(createdCourse.getId());
    }

    /**
     * 测试讨论区完整流程
     */
    @Test
    public void testCompleteDiscussionFlow() {
        // 1. 创建课程
        Course course = new Course();
        course.setTitle("完整讨论流程测试");
        course.setInstructorId(2L);
        Course createdCourse = courseService.createCourse(course);

        Long studentId1 = 1L;
        Long studentId2 = 3L;

        // 2. 学生1发布讨论帖
        Discussion discussion1 = new Discussion();
        discussion1.setCourseId(createdCourse.getId());
        discussion1.setUserId(studentId1);
        discussion1.setTitle("关于Java基础的疑问");
        discussion1.setContent("Java中的String为什么是不可变的？");
        Discussion createdDiscussion1 = discussionService.createDiscussion(discussion1);

        Discussion discussion2 = new Discussion();
        discussion2.setCourseId(createdCourse.getId());
        discussion2.setUserId(studentId1);
        discussion2.setTitle("Spring框架学习心得");
        discussion2.setContent("分享一下学习Spring Boot的经验。");
        Discussion createdDiscussion2 = discussionService.createDiscussion(discussion2);

        System.out.println("讨论帖发布完成");

        // 3. 学生2回复讨论帖1
        DiscussionReply reply1 = new DiscussionReply();
        reply1.setDiscussionId(createdDiscussion1.getId());
        reply1.setUserId(studentId2);
        reply1.setContent("String不可变的原因主要有：1. 安全性 2. 字符串常量池优化 3. 线程安全");
        DiscussionReply createdReply1 = discussionReplyService.createReply(reply1);

        // 学生1回复自己的帖子
        DiscussionReply reply2 = new DiscussionReply();
        reply2.setDiscussionId(createdDiscussion1.getId());
        reply2.setUserId(studentId1);
        reply2.setContent("谢谢解答！关于安全性这一点，能详细说说吗？");
        discussionReplyService.createReply(reply2);

        // 学生2继续回复
        DiscussionReply reply3 = new DiscussionReply();
        reply3.setDiscussionId(createdDiscussion1.getId());
        reply3.setUserId(studentId2);
        reply3.setContent("安全性方面，比如String经常用作参数，如果可变的话，方法内部修改可能会影响到其他地方。");
        discussionReplyService.createReply(reply3);

        System.out.println("讨论互动完成");

        // 4. 查看讨论区活跃度
        List<Discussion> discussions = discussionService.getDiscussionsByCourseId(createdCourse.getId());
        System.out.println("课程讨论帖总数: " + discussions.size());

        for (Discussion d : discussions) {
            List<DiscussionReply> replies = discussionReplyService.getRepliesByDiscussionId(d.getId());
            System.out.println("讨论帖 '" + d.getTitle() + "' 有 " + replies.size() + " 个回复");
        }

        // 5. 获取热门讨论帖
        List<Discussion> hotDiscussions = discussionService.getHotDiscussions(createdCourse.getId(), 5);
        System.out.println("热门讨论帖数量: " + hotDiscussions.size());

        // 6. 管理员操作（设为精华）
        discussionService.toggleEssence(createdDiscussion1.getId());
        System.out.println("讨论帖已设为精华");

        // 7. 清理测试数据
        // 删除所有回复
        List<DiscussionReply> allReplies1 = discussionReplyService.getRepliesByDiscussionId(createdDiscussion1.getId());
        for (DiscussionReply reply : allReplies1) {
            discussionReplyService.deleteReply(reply.getId());
        }
        List<DiscussionReply> allReplies2 = discussionReplyService.getRepliesByDiscussionId(createdDiscussion2.getId());
        for (DiscussionReply reply : allReplies2) {
            discussionReplyService.deleteReply(reply.getId());
        }

        // 删除讨论帖
        discussionService.deleteDiscussion(createdDiscussion1.getId());
        discussionService.deleteDiscussion(createdDiscussion2.getId());

        // 删除课程
        courseService.deleteCourse(createdCourse.getId());

        System.out.println("完整讨论流程测试完成，数据清理完毕");
    }

    /**
     * 测试权限控制
     */
    @Test
    public void testPermissionControl() {
        // 创建测试数据
        Course course = new Course();
        course.setTitle("权限测试课程");
        course.setInstructorId(2L);
        Course createdCourse = courseService.createCourse(course);

        Long userId1 = 1L;
        Long userId2 = 3L;

        // 用户1创建讨论帖
        Discussion discussion = new Discussion();
        discussion.setCourseId(createdCourse.getId());
        discussion.setUserId(userId1);
        discussion.setTitle("权限测试讨论帖");
        discussion.setContent("测试权限控制");
        Discussion createdDiscussion = discussionService.createDiscussion(discussion);

        // 用户2尝试修改用户1的讨论帖（应该失败）
        Discussion updateDiscussion = new Discussion();
        updateDiscussion.setId(createdDiscussion.getId());
        updateDiscussion.setContent("用户2修改的内容");
        boolean updateResult = discussionService.updateDiscussion(updateDiscussion);
        System.out.println("用户2修改用户1的讨论帖: " + (updateResult ? "成功" : "失败（预期失败）"));

        // 用户1修改自己的讨论帖（应该成功）
        updateDiscussion.setContent("用户1修改的内容");
        boolean updateResult2 = discussionService.updateDiscussion(updateDiscussion);
        System.out.println("用户1修改自己的讨论帖: " + (updateResult2 ? "成功" : "失败"));

        // 清理测试数据
        discussionService.deleteDiscussion(createdDiscussion.getId());
        courseService.deleteCourse(createdCourse.getId());
    }
}