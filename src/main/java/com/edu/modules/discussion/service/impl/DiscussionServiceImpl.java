package com.edu.modules.discussion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.enums.ResultCode;
import com.edu.common.exception.BusinessException;
import com.edu.modules.discussion.entity.Discussion;
import com.edu.modules.discussion.mapper.DiscussionMapper;
import com.edu.modules.discussion.service.DiscussionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 讨论帖服务实现
 */
@Slf4j
@Service
public class DiscussionServiceImpl extends ServiceImpl<DiscussionMapper, Discussion> implements DiscussionService {

    @Override
    public Discussion createDiscussion(Discussion discussion) {
        // 参数校验
        if (discussion == null || !StringUtils.hasText(discussion.getTitle())) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "讨论标题不能为空");
        }
        if (!StringUtils.hasText(discussion.getContent())) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "讨论内容不能为空");
        }
        if (discussion.getCourseId() == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "课程ID不能为空");
        }
        if (discussion.getUserId() == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "用户ID不能为空");
        }

        // 设置默认值
        if (discussion.getViewCount() == null) {
            discussion.setViewCount(0);
        }
        if (discussion.getReplyCount() == null) {
            discussion.setReplyCount(0);
        }
        if (discussion.getIsTop() == null) {
            discussion.setIsTop(0);
        }
        if (discussion.getIsEssence() == null) {
            discussion.setIsEssence(0);
        }
        if (discussion.getStatus() == null) {
            discussion.setStatus(1);
        }

        boolean success = save(discussion);
        if (!success) {
            throw new BusinessException("创建讨论帖失败");
        }

        log.info("讨论帖创建成功: title={}, courseId={}, userId={}",
                discussion.getTitle(), discussion.getCourseId(), discussion.getUserId());
        return discussion;
    }

    @Override
    public boolean updateDiscussion(Discussion discussion) {
        if (discussion == null || discussion.getId() == null) {
            return false;
        }

        boolean success = updateById(discussion);
        if (success) {
            log.info("讨论帖更新成功: id={}", discussion.getId());
        }
        return success;
    }

    @Override
    public boolean deleteDiscussion(Long discussionId) {
        if (discussionId == null) {
            return false;
        }

        boolean success = removeById(discussionId);
        if (success) {
            log.info("讨论帖删除成功: id={}", discussionId);
        }
        return success;
    }

    @Override
    public List<Discussion> getDiscussionsByCourseId(Long courseId) {
        if (courseId == null) {
            return List.of();
        }

        LambdaQueryWrapper<Discussion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Discussion::getCourseId, courseId)
               .eq(Discussion::getStatus, 1) // 只查询显示状态的讨论
               .orderByDesc(Discussion::getIsTop) // 置顶帖优先
               .orderByDesc(Discussion::getCreateTime);

        return list(wrapper);
    }

    @Override
    public boolean incrementViewCount(Long discussionId) {
        if (discussionId == null) {
            return false;
        }

        LambdaUpdateWrapper<Discussion> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Discussion::getId, discussionId)
               .setSql("view_count = view_count + 1");

        return update(wrapper);
    }

    @Override
    public boolean toggleTop(Long discussionId) {
        if (discussionId == null) {
            return false;
        }

        Discussion discussion = getById(discussionId);
        if (discussion == null) {
            return false;
        }

        Integer newTopStatus = discussion.getIsTop() == 1 ? 0 : 1;

        LambdaUpdateWrapper<Discussion> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Discussion::getId, discussionId)
               .set(Discussion::getIsTop, newTopStatus);

        boolean success = update(wrapper);
        if (success) {
            log.info("讨论帖置顶状态更新成功: id={}, isTop={}", discussionId, newTopStatus);
        }
        return success;
    }

    @Override
    public boolean toggleEssence(Long discussionId) {
        if (discussionId == null) {
            return false;
        }

        Discussion discussion = getById(discussionId);
        if (discussion == null) {
            return false;
        }

        Integer newEssenceStatus = discussion.getIsEssence() == 1 ? 0 : 1;

        LambdaUpdateWrapper<Discussion> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Discussion::getId, discussionId)
               .set(Discussion::getIsEssence, newEssenceStatus);

        boolean success = update(wrapper);
        if (success) {
            log.info("讨论帖精华状态更新成功: id={}, isEssence={}", discussionId, newEssenceStatus);
        }
        return success;
    }

    @Override
    public List<Discussion> getHotDiscussions(Long courseId, Integer limit) {
        if (courseId == null) {
            return List.of();
        }
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        LambdaQueryWrapper<Discussion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Discussion::getCourseId, courseId)
               .eq(Discussion::getStatus, 1)
               .orderByDesc(Discussion::getReplyCount) // 按回复数排序
               .orderByDesc(Discussion::getViewCount)  // 然后按查看数排序
               .last("limit " + limit);

        return list(wrapper);
    }
}