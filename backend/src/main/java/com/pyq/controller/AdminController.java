package com.pyq.controller;

import com.pyq.dto.TestResultResponse;
import com.pyq.entity.Question;
import com.pyq.entity.Subject;
import com.pyq.service.QuestionService;
import com.pyq.service.SubjectService;
import com.pyq.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TestService testService;

    @PostMapping("/subjects")
    public ResponseEntity<?> addSubject(@RequestBody Map<String, String> body) {
        try {
            Subject subject = subjectService.addSubject(body.get("subjectName"));
            return ResponseEntity.ok(subject);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<?> deleteSubject(@PathVariable Long id) {
        try {
            subjectService.deleteSubject(id);
            return ResponseEntity.ok(Map.of("message", "Subject deleted"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/questions")
    public ResponseEntity<?> addQuestion(@RequestBody Map<String, Object> body) {
        try {
            Long subjectId = Long.valueOf(body.get("subjectId").toString());
            Question question = new Question();
            question.setQuestionText(body.get("questionText").toString());
            question.setOptionA(body.get("optionA").toString());
            question.setOptionB(body.get("optionB").toString());
            question.setOptionC(body.get("optionC").toString());
            question.setOptionD(body.get("optionD").toString());
            question.setCorrectAnswer(body.get("correctAnswer").toString());
            Question saved = questionService.addQuestion(question, subjectId);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        try {
            questionService.deleteQuestion(id);
            return ResponseEntity.ok(Map.of("message", "Question deleted"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/results")
    public ResponseEntity<List<TestResultResponse>> getAllResults() {
        return ResponseEntity.ok(testService.getAllAttempts());
    }
}
