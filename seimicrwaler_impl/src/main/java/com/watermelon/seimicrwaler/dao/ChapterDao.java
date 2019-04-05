package com.watermelon.seimicrwaler.dao;

import com.watermelon.seimicrwaler.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterDao extends JpaRepository<Chapter, Integer> {
}
