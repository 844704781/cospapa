package com.watermelon.seimicrwaler.service;

import com.watermelon.seimicrwaler.dao.ChapterDao;
import com.watermelon.seimicrwaler.dao.ComicDao;
import com.watermelon.seimicrwaler.dao.LessonDao;
import com.watermelon.seimicrwaler.entity.Chapter;
import com.watermelon.seimicrwaler.entity.Comic;
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
 * Created by watermelon on 2019/04/04
 */
@Service
public class ComicService {

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
            return;
        }
        chapter.setCreateTime(now);
        chapter.setUpdateTime(now);
        chapter.setDeleted(false);
        Chapter model = chapterDao.save(chapter);
        if (lessonList != null) {
            for (Lesson lesson : lessonList) {
                List<Lesson> n = lessonDao.findAll(Example.of(lesson));
                if (n.size()>0) {
                    continue;
                }
                lesson.setChapterId(model.getId());
                lesson.setComicId(model.getComicId());
                lesson.setCreateTime(now);
                lesson.setUpdateTime(now);
                lesson.setDeleted(false);
            }

        }
        lessonDao.saveAll(lessonList);
    }


}
