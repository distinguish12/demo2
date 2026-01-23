package com.edu.modules.exam;

import com.edu.modules.course.entity.Course;
import com.edu.modules.course.service.CourseService;
import com.edu.modules.exam.entity.Exam;
import com.edu.modules.exam.entity.ExamQuestion;
import com.edu.modules.exam.entity.ExamRecord;
import com.edu.modules.exam.service.ExamQuestionService;
import com.edu.modules.exam.service.ExamRecordService;
import com.edu.modules.exam.service.ExamService;
import com.edu.modules.exam.service.ExamStats;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考试模块测试
 */
@SpringBootTest
public class ExamModuleTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private ExamService examService;

    @Autowired
    private ExamQuestionService examQuestionService;

    @Autowired
    private ExamRecordService examRecordService;

    /**
     * 测试考试管理功能
     */
    @Test
    public void testExamManagement() {
        // 创建测试课程
        Course course = new Course();
        course.setTitle("考试测试课程");
        course.setInstructorId(2L);
        Course createdCourse = courseService.createCourse(course);

        // 创建考试
        Exam exam = new Exam();
        exam.setCourseId(createdCourse.getId());
        exam.setTitle("期末考试");
        exam.setDescription("课程期末综合考试");
        exam.setDuration(120); // 120分钟
        exam.setTotalScore(BigDecimal.valueOf(100));
        exam.setPassScore(BigDecimal.valueOf(60));
        exam.setStartTime(LocalDateTime.now().plusDays(1)); // 明天开始
        exam.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2)); // 2小时后结束

        Exam createdExam = examService.createExam(exam);
        System.out.println("考试创建成功: " + createdExam.getId());

        // 更新考试
        exam.setId(createdExam.getId());
        exam.setDescription("更新后的考试描述");
        boolean updateResult = examService.updateExam(exam);
        System.out.println("考试更新结果: " + updateResult);

        // 查询考试
        Exam queriedExam = examService.getById(createdExam.getId());
        System.out.println("查询考试: " + queriedExam.getTitle());

        // 查询课程考试列表
        List<Exam> courseExams = examService.getExamsByCourseId(createdCourse.getId());
        System.out.println("课程考试数量: " + courseExams.size());

        // 清理测试数据
        examService.deleteExam(createdExam.getId());
        courseService.deleteCourse(createdCourse.getId());
    }

    /**
     * 测试考试题目管理
     */
    @Test
    public void testExamQuestions() {
        // 创建测试考试
        Course course = new Course();
        course.setTitle("题目测试课程");
        course.setInstructorId(2L);
        Course createdCourse = courseService.createCourse(course);

        Exam exam = new Exam();
        exam.setCourseId(createdCourse.getId());
        exam.setTitle("题目测试考试");
        exam.setTotalScore(BigDecimal.valueOf(100));
        Exam createdExam = examService.createExam(exam);

        // 批量添加题目
        List<ExamQuestion> questions = new ArrayList<>();

        // 单选题
        ExamQuestion singleChoice = new ExamQuestion();
        singleChoice.setTitle("Java是什么类型的语言？");
        singleChoice.setType(1);
        singleChoice.setOptions("[\"编译型\", \"解释型\", \"混合型\", \"脚本语言\"]");
        singleChoice.setAnswer("C"); // 混合型
        singleChoice.setScore(BigDecimal.valueOf(10));
        questions.add(singleChoice);

        // 判断题
        ExamQuestion trueFalse = new ExamQuestion();
        trueFalse.setTitle("Java支持多继承");
        trueFalse.setType(3);
        trueFalse.setAnswer("false");
        trueFalse.setScore(BigDecimal.valueOf(5));
        questions.add(trueFalse);

        // 填空题
        ExamQuestion fillBlank = new ExamQuestion();
        fillBlank.setTitle("Java的创建者是___公司");
        fillBlank.setType(4);
        fillBlank.setAnswer("Sun");
        fillBlank.setScore(BigDecimal.valueOf(15));
        questions.add(fillBlank);

        boolean addResult = examQuestionService.addQuestionsToExam(createdExam.getId(), questions);
        System.out.println("批量添加题目结果: " + addResult);

        // 查询考试题目
        List<ExamQuestion> examQuestions = examQuestionService.getQuestionsByExamId(createdExam.getId());
        System.out.println("考试题目数量: " + examQuestions.size());

        for (ExamQuestion q : examQuestions) {
            System.out.println("题目: " + q.getTitle() + ", 类型: " + q.getType() + ", 分值: " + q.getScore());
        }

        // 清理测试数据
        examQuestionService.removeQuestionsByExamId(createdExam.getId());
        examService.deleteExam(createdExam.getId());
        courseService.deleteCourse(createdCourse.getId());
    }

    /**
     * 测试考试流程
     */
    @Test
    public void testExamProcess() {
        // 创建测试数据
        Course course = new Course();
        course.setTitle("考试流程测试");
        course.setInstructorId(2L);
        Course createdCourse = courseService.createCourse(course);

        Exam exam = new Exam();
        exam.setCourseId(createdCourse.getId());
        exam.setTitle("考试流程测试");
        exam.setTotalScore(BigDecimal.valueOf(50));
        exam.setPassScore(BigDecimal.valueOf(30));
        exam.setStartTime(LocalDateTime.now().minusMinutes(30)); // 30分钟前开始
        exam.setEndTime(LocalDateTime.now().plusMinutes(30)); // 30分钟后结束
        Exam createdExam = examService.createExam(exam);

        // 添加题目
        List<ExamQuestion> questions = new ArrayList<>();
        ExamQuestion q1 = new ExamQuestion();
        q1.setTitle("1+1等于几？");
        q1.setType(1);
        q1.setOptions("[\"1\", \"2\", \"3\", \"4\"]");
        q1.setAnswer("B");
        q1.setScore(BigDecimal.valueOf(10));
        questions.add(q1);

        ExamQuestion q2 = new ExamQuestion();
        q2.setTitle("2+2等于4吗？");
        q2.setType(3);
        q2.setAnswer("true");
        q2.setScore(BigDecimal.valueOf(20));
        questions.add(q2);

        examQuestionService.addQuestionsToExam(createdExam.getId(), questions);

        Long studentId = 1L; // 假设学生ID为1

        // 开始考试
        ExamRecord record = examService.startExam(studentId, createdExam.getId());
        System.out.println("考试开始: " + record.getId() + ", 开始时间: " + record.getStartTime());

        // 提交答案
        Map<Long, String> answers = new HashMap<>();
        answers.put(questions.get(0).getId(), "B"); // 正确答案
        answers.put(questions.get(1).getId(), "true"); // 正确答案

        ExamRecord submittedRecord = examService.submitExam(studentId, createdExam.getId(), answers);
        System.out.println("考试提交完成:");
        System.out.println("- 得分: " + submittedRecord.getScore());
        System.out.println("- 总分: " + submittedRecord.getTotalScore());
        System.out.println("- 提交时间: " + submittedRecord.getSubmitTime());

        // 查看考试统计
        ExamStats stats = examService.getExamStats(createdExam.getId());
        System.out.println("考试统计:");
        System.out.println("- 总参加人数: " + stats.getTotalParticipants());
        System.out.println("- 已完成人数: " + stats.getCompletedCount());
        System.out.println("- 平均分: " + String.format("%.2f", stats.getAverageScore()));
        System.out.println("- 及格率: " + String.format("%.2f%%", stats.getPassRate() * 100));

        // 查看学生考试记录
        List<ExamRecord> studentRecords = examRecordService.getUserExamRecords(studentId);
        System.out.println("学生考试记录数: " + studentRecords.size());

        // 清理测试数据
        examQuestionService.removeQuestionsByExamId(createdExam.getId());
        examService.deleteExam(createdExam.getId());
        courseService.deleteCourse(createdCourse.getId());
    }

    /**
     * 测试自动评分功能
     */
    @Test
    public void testAutoGrading() {
        // 创建模拟题目
        List<ExamQuestion> questions = new ArrayList<>();

        ExamQuestion q1 = new ExamQuestion();
        q1.setId(1L);
        q1.setTitle("单选题");
        q1.setType(1);
        q1.setAnswer("A");
        q1.setScore(BigDecimal.valueOf(10));
        questions.add(q1);

        ExamQuestion q2 = new ExamQuestion();
        q2.setId(2L);
        q2.setTitle("判断题");
        q2.setType(3);
        q2.setAnswer("true");
        q2.setScore(BigDecimal.valueOf(10));
        questions.add(q2);

        // 测试自动评分
        Map<Long, String> userAnswers = new HashMap<>();
        userAnswers.put(1L, "A"); // 正确
        userAnswers.put(2L, "false"); // 错误

        BigDecimal totalScore = examService.autoGrade(userAnswers, questions);
        System.out.println("自动评分结果: " + totalScore + "分 (期望10分，实际" + (totalScore.compareTo(BigDecimal.valueOf(10)) == 0 ? "正确" : "错误") + ")");

        // 测试全对的情况
        userAnswers.put(2L, "true"); // 改为正确
        BigDecimal fullScore = examService.autoGrade(userAnswers, questions);
        System.out.println("全对评分结果: " + fullScore + "分 (期望20分，实际" + (fullScore.compareTo(BigDecimal.valueOf(20)) == 0 ? "正确" : "错误") + ")");
    }
}