package com.watermelon.seimicrwaler.dao;

import com.watermelon.seimicrwaler.entity.Comic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComicDao extends JpaRepository<Comic, Integer> {
}
