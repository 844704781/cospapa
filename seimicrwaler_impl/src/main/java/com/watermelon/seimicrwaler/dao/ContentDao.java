package com.watermelon.seimicrwaler.dao;

import com.watermelon.seimicrwaler.entity.Content;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContentDao extends MongoRepository<Content, Integer> {

    Content findContentByLessonId(Integer lessonId);

}
