package com.watermelon.seimicrwaler.service;

import com.alibaba.druid.support.json.JSONUtils;
import com.watermelon.seimicrwaler.dao.ChapterDao;
import com.watermelon.seimicrwaler.dao.ComicDao;
import com.watermelon.seimicrwaler.dao.LessonDao;
import com.watermelon.seimicrwaler.entity.Chapter;
import com.watermelon.seimicrwaler.entity.Comic;
import com.watermelon.seimicrwaler.entity.Lesson;
import com.watermelon.seimicrwaler.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by watermelon on 2019/04/04
 */
@Service
public class ComicService {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ComicDao comicDao;

    @Autowired
    private ChapterDao chapterDao;

    @Autowired
    private LessonDao lessonDao;

    public Comic findOne(Comic comic) {
        Example<Comic> example = Example.of(comic);
        Optional<Comic> optional = comicDao.findOne(example);
        return optional.orElse(null);
    }

    public Comic save(Comic comic) {
        comic.setCreateTime(new Date());
        comic.setUpdateTime(new Date());
        comic.setDeleted(false);
        return comicDao.save(comic);
    }

    public void saveIfNotExist(Comic comic) {
        Comic old = this.findOne(comic);
        if (old == null) {
            this.save(comic);
        }
    }

    public List<Comic> findAll(Comic comic) {
        if (comic == null) {
            return comicDao.findAll();
        }
        return comicDao.findAll(Example.of(comic));
    }

    public Integer count(Comic comic) {
        if (comic == null) {
            return (int) comicDao.count();
        }
        return (int) comicDao.count(Example.of(comic));
    }

    public Page<Comic> page(Comic comic, int page, int size) {
        if (comic == null) {
            return comicDao.findAll(PageRequest.of(page, size));
        }
        return comicDao.findAll(Example.of(comic), PageRequest.of(page, size));
    }


    public void saveChapter(Chapter chapter, List<Lesson> lessonList) {
        Chapter m = chapterDao.findOne(Example.of(chapter)).orElse(null);
        Date now = new Date();
        if (m != null) {
            chapter.setId(m.getId());
        }
        chapter.setCreateTime(now);
        chapter.setUpdateTime(now);
        chapter.setDeleted(false);
        logger.info("{},开始保存chapter数据,chapter:{}", Thread.currentThread().getName(), JsonUtils.toJson(chapter, Chapter.class));
        Chapter model = chapterDao.save(chapter);
        logger.info("{},数据保存成功,返回chapter:{}", Thread.currentThread().getName(), JsonUtils.toJson(model, Chapter.class));
        List<Lesson> lessons = new ArrayList<>();
        if (lessonList != null) {
            for (Lesson lesson : lessonList) {
                List<Lesson> n = lessonDao.findAll(Example.of(lesson));
                if (n.size() > 0) {
		    logger.info("不保存lesson,因为数据已存在,为:{}",JsonUtils.toJson(n,List.class));
                    break;
                }
                lesson.setChapterId(model.getId());
                lesson.setComicId(model.getComicId());
                lesson.setCreateTime(now);
                lesson.setUpdateTime(now);
                lesson.setDeleted(false);
                lessons.add(lesson);
            }

        }
        logger.info("{},开始保存lesson数据,lessons:{}", Thread.currentThread().getName(), JsonUtils.toJson(lessons, List.class));
        lessonDao.saveAll(lessons);
        logger.info("{},lesson保存成功", Thread.currentThread().getName());
    }

}
