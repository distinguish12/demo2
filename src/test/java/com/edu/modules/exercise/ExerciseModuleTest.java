package com.edu.modules.exercise;

import com.edu.modules.course.entity.Course;
import com.edu.modules.course.entity.CourseChapter;
import com.edu.modules.course.service.CourseService;
import com.edu.modules.course.service.CourseChapterService;
import com.edu.modules.exercise.entity.Exercise;
import com.edu.modules.exercise.entity.ExerciseSubmission;
import com.edu.modules.exercise.service.ExerciseService;
import com.edu.modules.exercise.service.ExerciseStats;
import com.edu.modules.exercise.service.ExerciseSubmissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

/**
 * 练习作业模块测试
 */
@SpringBootTest
public class ExerciseModuleTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseChapterService courseChapterService;

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private ExerciseSubmissionService exerciseSubmissionService;

    /**
     * 测试练习题管理功能
     */
    @Test
    public void testExerciseManagement() {
        // 创建测试课程和章节
        Course course = new Course();
        course.setTitle("练习测试课程");
        course.setInstructorId(2L);
        Course createdCourse = courseService.createCourse(course);

        CourseChapter chapter = new CourseChapter();
        chapter.setCourseId(createdCourse.getId());
        chapter.setTitle("测试章节");
        CourseChapter createdChapter = courseChapterService.createChapter(chapter);

        // 创建单选题
        Exercise singleChoice = new Exercise();
        singleChoice.setCourseId(createdCourse.getId());
        singleChoice.setChapterId(createdChapter.getId());
        singleChoice.setTitle("Java是什么？");
        singleChoice.setType(1); // 单选题
        singleChoice.setOptions("[\"编程语言\", \"数据库\", \"操作系统\", \"网络协议\"]");
        singleChoice.setAnswer("A");
        singleChoice.setExplanation("Java是一种面向对象的编程语言");
        singleChoice.setScore(BigDecimal.valueOf(5.0));

        Exercise createdExercise1 = exerciseService.createExercise(singleChoice);
        System.out.println("单选题创建成功: " + createdExercise1.getId());

        // 创建判断题
        Exercise trueFalse = new Exercise();
        trueFalse.setCourseId(createdCourse.getId());
        trueFalse.setChapterId(createdChapter.getId());
        trueFalse.setTitle("Java是解释型语言");
        trueFalse.setType(3); // 判断题
        trueFalse.setAnswer("false");
        trueFalse.setExplanation("Java是编译型语言，会先编译成字节码");
        trueFalse.setScore(BigDecimal.valueOf(3.0));

        Exercise createdExercise2 = exerciseService.createExercise(trueFalse);
        System.out.println("判断题创建成功: " + createdExercise2.getId());

        // 查询章节练习题
        List<Exercise> chapterExercises = exerciseService.getExercisesByChapterId(createdChapter.getId());
        System.out.println("章节练习题数量: " + chapterExercises.size());

        // 查询课程练习题
        List<Exercise> courseExercises = exerciseService.getExercisesByCourseId(createdCourse.getId());
        System.out.println("课程练习题数量: " + courseExercises.size());

        // 清理测试数据
        exerciseService.deleteExercise(createdExercise1.getId());
        exerciseService.deleteExercise(createdExercise2.getId());
        courseChapterService.deleteChapter(createdChapter.getId());
        courseService.deleteCourse(createdCourse.getId());
    }

    /**
     * 测试练习提交和评分功能
     */
    @Test
    public void testExerciseSubmission() {
        // 创建测试数据
        Course course = new Course();
        course.setTitle("提交测试课程");
        course.setInstructorId(2L);
        Course createdCourse = courseService.createCourse(course);

        CourseChapter chapter = new CourseChapter();
        chapter.setCourseId(createdCourse.getId());
        chapter.setTitle("测试章节");
        CourseChapter createdChapter = courseChapterService.createChapter(chapter);

        Exercise exercise = new Exercise();
        exercise.setCourseId(createdCourse.getId());
        exercise.setChapterId(createdChapter.getId());
        exercise.setTitle("2+2等于几？");
        exercise.setType(1); // 单选题
        exercise.setOptions("[\"3\", \"4\", \"5\", \"6\"]");
        exercise.setAnswer("B"); // 正确答案是B(4)
        exercise.setScore(BigDecimal.valueOf(5.0));

        Exercise createdExercise = exerciseService.createExercise(exercise);

        Long userId = 1L; // 假设学生ID为1

        // 第一次提交 - 正确答案
        ExerciseSubmission submission1 = exerciseSubmissionService.submitAnswer(userId, createdExercise.getId(), "B");
        System.out.println("第一次提交 - 答案: " + submission1.getAnswer() +
                          ", 得分: " + submission1.getScore() +
                          ", 是否正确: " + submission1.getIsCorrect());

        // 重新提交 - 错误答案
        ExerciseSubmission submission2 = exerciseSubmissionService.resubmitAnswer(userId, createdExercise.getId(), "A");
        System.out.println("重新提交 - 答案: " + submission2.getAnswer() +
                          ", 得分: " + submission2.getScore() +
                          ", 是否正确: " + submission2.getIsCorrect());

        // 获取用户提交记录
        ExerciseSubmission userSubmission = exerciseSubmissionService.getUserSubmission(userId, createdExercise.getId());
        System.out.println("用户最新提交: " + userSubmission.getAnswer() + ", 得分: " + userSubmission.getScore());

        // 另一个用户提交
        Long userId2 = 3L; // 假设另一个学生ID为3
        exerciseSubmissionService.submitAnswer(userId2, createdExercise.getId(), "B"); // 正确答案

        // 获取练习统计
        ExerciseStats stats = exerciseSubmissionService.getExerciseStats(createdExercise.getId());
        System.out.println("练习统计:");
        System.out.println("- 总提交数: " + stats.getTotalSubmissions());
        System.out.println("- 正确提交数: " + stats.getCorrectSubmissions());
        System.out.println("- 平均分: " + String.format("%.2f", stats.getAverageScore()));
        System.out.println("- 正确率: " + String.format("%.2f%%", stats.getCorrectRate() * 100));

        // 获取练习的所有提交记录
        List<ExerciseSubmission> submissions = exerciseSubmissionService.getExerciseSubmissions(createdExercise.getId());
        System.out.println("总提交记录数: " + submissions.size());

        // 清理测试数据
        exerciseService.deleteExercise(createdExercise.getId());
        courseChapterService.deleteChapter(createdChapter.getId());
        courseService.deleteCourse(createdCourse.getId());
    }

    /**
     * 测试自动评分功能
     */
    @Test
    public void testAutoGrading() {
        // 测试单选题评分
        BigDecimal score1 = exerciseService.autoGrade("A", "A", 1);
        System.out.println("单选题 - 用户答案: A, 正确答案: A, 得分: " + score1);

        BigDecimal score2 = exerciseService.autoGrade("B", "A", 1);
        System.out.println("单选题 - 用户答案: B, 正确答案: A, 得分: " + score2);

        // 测试判断题评分
        BigDecimal score3 = exerciseService.autoGrade("true", "true", 3);
        System.out.println("判断题 - 用户答案: true, 正确答案: true, 得分: " + score3);

        BigDecimal score4 = exerciseService.autoGrade("false", "true", 3);
        System.out.println("判断题 - 用户答案: false, 正确答案: true, 得分: " + score4);

        // 测试填空题评分（简单匹配）
        BigDecimal score5 = exerciseService.autoGrade("Java", "Java", 4);
        System.out.println("填空题 - 用户答案: Java, 正确答案: Java, 得分: " + score5);

        BigDecimal score6 = exerciseService.autoGrade("python", "Java", 4);
        System.out.println("填空题 - 用户答案: python, 正确答案: Java, 得分: " + score6);
    }

    /**
     * 测试完整练习流程
     */
    @Test
    public void testCompleteExerciseFlow() {
        // 1. 创建课程和章节
        Course course = new Course();
        course.setTitle("完整练习流程测试");
        course.setInstructorId(2L);
        Course createdCourse = courseService.createCourse(course);

        CourseChapter chapter = new CourseChapter();
        chapter.setCourseId(createdCourse.getId());
        chapter.setTitle("练习章节");
        CourseChapter createdChapter = courseChapterService.createChapter(chapter);

        // 2. 创建多个练习题
        Exercise[] exercises = new Exercise[3];

        // 练习题1：单选题
        exercises[0] = new Exercise();
        exercises[0].setCourseId(createdCourse.getId());
        exercises[0].setChapterId(createdChapter.getId());
        exercises[0].setTitle("以下哪项是Java的基本数据类型？");
        exercises[0].setType(1);
        exercises[0].setOptions("[\"String\", \"int\", \"Object\", \"Array\"]");
        exercises[0].setAnswer("B");
        exercises[0].setScore(BigDecimal.valueOf(5.0));
        exercises[0] = exerciseService.createExercise(exercises[0]);

        // 练习题2：判断题
        exercises[1] = new Exercise();
        exercises[1].setCourseId(createdCourse.getId());
        exercises[1].setChapterId(createdChapter.getId());
        exercises[1].setTitle("Java支持多继承");
        exercises[1].setType(3);
        exercises[1].setAnswer("false");
        exercises[1].setScore(BigDecimal.valueOf(3.0));
        exercises[1] = exerciseService.createExercise(exercises[1]);

        // 练习题3：多选题（暂时按单选处理）
        exercises[2] = new Exercise();
        exercises[2].setCourseId(createdCourse.getId());
        exercises[2].setChapterId(createdChapter.getId());
        exercises[2].setTitle("以下哪些是面向对象特性？");
        exercises[2].setType(2);
        exercises[2].setOptions("[\"封装\", \"继承\", \"多态\", \"编译\"]");
        exercises[2].setAnswer("ABC");
        exercises[2].setScore(BigDecimal.valueOf(8.0));
        exercises[2] = exerciseService.createExercise(exercises[2]);

        // 3. 学生答题
        Long[] studentIds = {1L, 3L}; // 两个学生

        for (Long studentId : studentIds) {
            System.out.println("学生" + studentId + "开始答题:");

            // 答题1：正确
            exerciseSubmissionService.submitAnswer(studentId, exercises[0].getId(), "B");
            System.out.println("  练习1完成");

            // 答题2：正确
            exerciseSubmissionService.submitAnswer(studentId, exercises[1].getId(), "false");
            System.out.println("  练习2完成");

            // 答题3：正确
            exerciseSubmissionService.submitAnswer(studentId, exercises[2].getId(), "ABC");
            System.out.println("  练习3完成");
        }

        // 4. 查看统计结果
        for (int i = 0; i < exercises.length; i++) {
            ExerciseStats stats = exerciseSubmissionService.getExerciseStats(exercises[i].getId());
            System.out.println("练习" + (i+1) + "统计:");
            System.out.println("  提交数: " + stats.getTotalSubmissions());
            System.out.println("  平均分: " + String.format("%.2f", stats.getAverageScore()));
            System.out.println("  正确率: " + String.format("%.1f%%", stats.getCorrectRate() * 100));
        }

        // 5. 查看学生提交记录
        for (Long studentId : studentIds) {
            List<ExerciseSubmission> userSubmissions = exerciseSubmissionService.getUserSubmissions(studentId);
            System.out.println("学生" + studentId + "提交记录数: " + userSubmissions.size());
            BigDecimal totalScore = userSubmissions.stream()
                    .map(ExerciseSubmission::getScore)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            System.out.println("学生" + studentId + "总得分: " + totalScore);
        }

        // 6. 清理测试数据
        for (Exercise exercise : exercises) {
            exerciseService.deleteExercise(exercise.getId());
        }
        courseChapterService.deleteChapter(createdChapter.getId());
        courseService.deleteCourse(createdCourse.getId());

        System.out.println("完整练习流程测试完成");
    }
}