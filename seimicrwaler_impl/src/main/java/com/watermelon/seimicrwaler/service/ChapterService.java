package com.watermelon.seimicrwaler.service;

import com.watermelon.seimicrwaler.dao.ChapterDao;
import com.watermelon.seimicrwaler.entity.Chapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by watermelon on 2019/04/05
 */
@Service
public class ChapterService {


    @Autowired
    private ChapterDao chapterDao;


    public Chapter findOne(Chapter chapter) {
        Example<Chapter> example = Example.of(chapter);
        Optional<Chapter> optional = chapterDao.findOne(example);
        return optional.orElse(null);
    }

    public Chapter save(Chapter chapter) {
        chapter.setCreateTime(new Date());
        chapter.setUpdateTime(new Date());
        chapter.setDeleted(false);
        return chapterDao.save(chapter);
    }

    public List<Chapter> saveAll(List<Chapter> chapters) {
        return chapterDao.saveAll(chapters);
    }

    public void saveIfNotExist(Chapter chapter) {
        Chapter old = this.findOne(chapter);
        if (old == null) {
            this.save(chapter);
        }
    }

    public List<Chapter> findAll(Chapter chapter) {
        if (chapter == null) {
            return chapterDao.findAll();
        }
        return chapterDao.findAll(Example.of(chapter));
    }

    public Integer count(Chapter chapter) {
        if (chapter == null) {
            return (int) chapterDao.count();
        }
        return (int) chapterDao.count(Example.of(chapter));
    }

    public Page<Chapter> page(Chapter chapter, int page, int size) {
        if (chapter == null) {
            return chapterDao.findAll(PageRequest.of(page, size));
        }
        return chapterDao.findAll(Example.of(chapter), PageRequest.of(page, size));
    }
}
