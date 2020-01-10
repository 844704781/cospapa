package com.watermelon.seimicrwaler.mongoDao;

import com.watermelon.seimicrwaler.entity.Content;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContentRepository extends MongoRepository<Content, Integer> {

}
