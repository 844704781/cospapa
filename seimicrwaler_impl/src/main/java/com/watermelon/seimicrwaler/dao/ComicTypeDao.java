package com.watermelon.seimicrwaler.dao;

import com.watermelon.seimicrwaler.entity.ComicType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComicTypeDao extends JpaRepository<ComicType, Integer> {
}
