package com.example.question_service.repository;

import com.example.question_service.entity.Question;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface QuestionRepository extends MongoRepository<Question , Integer> {
    List<Question> findByCategory(String category);
}
