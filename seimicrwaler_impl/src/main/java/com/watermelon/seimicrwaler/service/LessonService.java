package com.watermelon.seimicrwaler.service;

import com.watermelon.seimicrwaler.dao.ChapterDao;
import com.watermelon.seimicrwaler.dao.LessonDao;
import com.watermelon.seimicrwaler.dao.LessonDao;
import com.watermelon.seimicrwaler.entity.Chapter;
import com.watermelon.seimicrwaler.entity.Lesson;
import com.watermelon.seimicrwaler.entity.Lesson;
import com.watermelon.seimicrwaler.entity.Lesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by watermelon on 2019/04/05
 */
@Service
public class LessonService {

    @Autowired
    private LessonDao lessonDao;

    public Lesson findOne(Lesson lesson) {
        Example<Lesson> example = Example.of(lesson);
        Optional<Lesson> optional = lessonDao.findOne(example);
        return optional.orElse(null);
    }

    public Lesson save(Lesson lesson) {
        lesson.setCreateTime(new Date());
        lesson.setUpdateTime(new Date());
        lesson.setDeleted(false);
        return lessonDao.save(lesson);
    }

    public void saveIfNotExist(Lesson lesson) {
        Lesson old = this.findOne(lesson);
        if (old == null) {
            this.save(lesson);
        }
    }

    public List<Lesson> findAll(Lesson lesson) {
        if (lesson == null) {
            return lessonDao.findAll();
        }
        return lessonDao.findAll(Example.of(lesson));
    }

    public Integer count(Lesson lesson) {
        if (lesson == null) {
            return (int) lessonDao.count();
        }
        return (int) lessonDao.count(Example.of(lesson));
    }

    public Page<Lesson> page(Lesson lesson, int page, int size) {
        if (lesson == null) {
            return lessonDao.findAll(PageRequest.of(page, size));
        }
        return lessonDao.findAll(Example.of(lesson), PageRequest.of(page, size));
    }

    public List<Lesson> saveAll(List<Lesson> group) {
        return lessonDao.saveAll(group);
    }
}
