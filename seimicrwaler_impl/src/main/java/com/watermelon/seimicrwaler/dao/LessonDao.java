package com.watermelon.seimicrwaler.dao;

import com.watermelon.seimicrwaler.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonDao extends JpaRepository<Lesson, Integer> {

}
