package com.example.question_service.controller;

import com.example.question_service.entity.Question;
import com.example.question_service.entity.QuestionWrapper;
import com.example.question_service.entity.Response;
import com.example.question_service.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/allQuestions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping("category/{category}")
    public ResponseEntity<List<Question>> getQuestionByCategory(@PathVariable String category) {
        return questionService.getQuestionByCategory(category);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addQuestion(@RequestBody Question question){
        return questionService.addQuestion(question);
    }

    // generate
    // getQuestion (questionId)
    // getScore

    /**
     * API endpoint to get a list of random question IDs based on category and number.
     * This is intended to be called by the Quiz Service.
     *
     * @param categoryName The category of questions to pick from.
     * @param numberOfQuestions The number of random question IDs to return.
     * @return A ResponseEntity containing a List of Integer (question IDs) or an error status.
     */
    @GetMapping("generate")
    public ResponseEntity<List<Integer>> getQuestionsForQuiz(
            @RequestParam String categoryName,
            @RequestParam Integer numberOfQuestions
    ){
        return questionService.getQuestionsForQuiz(categoryName , numberOfQuestions);
    }

    @PostMapping("getQuestions")
    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(@RequestBody List<Integer> questionsId){
        return questionService.getQuestionsFromId(questionsId);
    }

    @PostMapping("getScore")
    public ResponseEntity<Integer> getScore (@RequestBody List<Response> responses)
    {
        return questionService.getScore(responses);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Integer id){
    return questionService.deleteQuestion(id);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> deleteAllQuestions(){
        return questionService.deleteAllQuestions();
    }
}
