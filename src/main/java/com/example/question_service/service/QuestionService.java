package com.example.question_service.service;

import com.example.question_service.entity.Question;
import com.example.question_service.entity.QuestionWrapper;
import com.example.question_service.entity.Response;
import com.example.question_service.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);

    public ResponseEntity<List<Question>> getAllQuestions() {
        try {
            return new ResponseEntity<>(questionRepository.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("An error occur while get all question");
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<Question>> getQuestionByCategory(String category) {
        try {
            return new ResponseEntity<>(questionRepository.findByCategory(category), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("An error occur while getting question of category: {}", category);
        }
        logger.info("question of category: {}", category + "not present");
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> addQuestion(Question question) {
        try {
            questionRepository.save(question);
            return new ResponseEntity<>("success", HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("An error occur while adding question");
        }
        return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Void> deleteQuestion(Integer id) { // Return type changed to Void as no body on 204
        try {
            // first check the question exists
            Optional<Question> questionOptional = questionRepository.findById(id);

            if (questionOptional.isPresent()) {
                questionRepository.deleteById(id);
                logger.info("Question with ID {} deleted successfully.", id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                logger.warn("Attempted to delete non-existent question with ID: {}", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Log the specific error
            logger.error("An error occurred while deleting question with ID: {}", id, e);
            // Return 500 Internal Server Error for unexpected issues.
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Void> deleteAllQuestions() {
        questionRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<List<Integer>> getQuestionsForQuiz(String categoryName, Integer numberOfQuestions) {
        try {
            // 1. Fetch all questions for the given category
            List<Question> questionsByCategory = questionRepository.findByCategory(categoryName);

            // 2. Handle cases where not enough questions are found
            if (questionsByCategory.isEmpty()) {
                logger.warn("No questions found for category: {}", categoryName);
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK); // 200 OK empty list
            }

            if (questionsByCategory.size() < numberOfQuestions) {
                logger.warn("Not enough questions in category '{}'. Requested: {}, Found: {}. Returning all available IDs.",
                        categoryName, numberOfQuestions, questionsByCategory.size());
                // If not enough, return all available question IDs for that category
                List<Integer> availableIds = questionsByCategory.stream()
                        .map(Question::getId)
                        .collect(Collectors.toList());
                return new ResponseEntity<>(availableIds, HttpStatus.OK);
            }

            // Shuffle the list of questions
            Collections.shuffle(questionsByCategory);

            // 4. Select the desired number of questions and extract their IDs
            List<Integer> randomQuestionIds = questionsByCategory.stream()
                    .limit(numberOfQuestions)
                    .map(Question::getId) // Map to get only the ID
                    .collect(Collectors.toList());

            logger.info("Generated {} random question IDs for category {}.", randomQuestionIds.size(), categoryName);
            return new ResponseEntity<>(randomQuestionIds, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("An error occurred while generating random question IDs for category: {} and numQuestion: {}: {}",
                    categoryName, numberOfQuestions, e.getMessage(), e);
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

//    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(List<Integer> questionsId) {
//        List<QuestionWrapper> wrappers = new ArrayList<>();
//
//        List<Question> questions = new ArrayList<>();
//
//        for (Integer id : questionsId) {
//            questions.add(questionRepository.findById(id).get());
//        }
//
//        for (Question question : questions) {
//            QuestionWrapper questionWrapper = new QuestionWrapper();
//            questionWrapper.setId(question.getId());
//            questionWrapper.setQuestionTitle(question.getQuestionTitle());
//            questionWrapper.setOption1(question.getOption1());
//            questionWrapper.setOption2(question.getOption2());
//            questionWrapper.setOption3(question.getOption3());
//            questionWrapper.setOption4(question.getOption4());
//            wrappers.add(questionWrapper);
//        }
//        return new ResponseEntity<>(wrappers, HttpStatus.OK);
//    }

    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(List<Integer> questionsId) {
        List<QuestionWrapper> wrappers = new ArrayList<>();

        try {
            // OPTIMIZATION 1: Use findAllById to fetch all questions in a single database query
            // This method returns only the questions that actually exist for the given IDs.
            List<Question> questions = questionRepository.findAllById(questionsId);

            // OPTIMIZATION 2: Use Stream API for concise and efficient conversion
            // This replaces the second loop and directly maps to QuestionWrapper.

            wrappers = questions.stream().map(question -> {
                QuestionWrapper questionWrapper = new QuestionWrapper();
                questionWrapper.setId(question.getId());
                questionWrapper.setQuestionTitle(question.getQuestionTitle());
                questionWrapper.setOption1(question.getOption1());
                questionWrapper.setOption2(question.getOption2());
                questionWrapper.setOption3(question.getOption3());
                questionWrapper.setOption4(question.getOption4());
                return questionWrapper;
            }).collect(Collectors.toList());

            logger.info("Fetched {} questions by IDs and converted to wrappers.", wrappers.size());
            return new ResponseEntity<>(wrappers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("An error occurred while fetching questions by IDs: {}", e.getMessage(), e);
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Integer> getScore(List<Response> responses) {
        int score = 0;
        try {
            // extract all the questions IDs from the user's responses
            List<Integer> questionIds = responses.stream()
                    .map(Response::getId)//Assuming 'id' in Response is the questionId
                    .collect(Collectors.toList());

            // 2. Fetch all original Question entities for these IDs in a single query (Optimization)
            List<Question> originalQuestions = questionRepository.findAllById(questionIds);

            // 3. Create a map for efficient lookup of right answers by question ID
            Map<Integer, String> rightAnswersMap = originalQuestions.stream()
                    .collect(Collectors.toMap(Question::getId, Question::getRightAnswer));

            // 4. Iterate through user responses and compare with actual right answers
            for (Response response : responses) {
                // get the correct answer from the map
                String correctAnswer = rightAnswersMap.get(response.getId());

                // Compare user's response with the correct answer (case-insensitive and trim whitespace)
                if (correctAnswer != null && correctAnswer.equalsIgnoreCase(response.getResponse().trim())) {
                    score++;
                }
            }
            logger.info("Score calculated: {} correct answers.", score);
            return new ResponseEntity<>(score, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("An error occurred while calculating score: {}", e.getMessage(), e);
            return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

//    public ResponseEntity<Integer> getScore(List<Response> responses) {
//        int score = 0;
//        for (Response response : responses) {
//            Question question = questionRepository.findById(response.getId()).get();
//            if (response.getResponse().equals(question.getRightAnswer()))
//                score++;
//
//        }
//        return new ResponseEntity<>(score , HttpStatus.OK);
//    }

}
