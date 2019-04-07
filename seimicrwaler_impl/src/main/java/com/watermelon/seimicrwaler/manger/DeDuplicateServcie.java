package com.watermelon.seimicrwaler.manger;

import com.watermelon.seimicrwaler.dao.ComicDao;
import com.watermelon.seimicrwaler.entity.*;
import com.watermelon.seimicrwaler.service.*;
import com.watermelon.seimicrwaler.utils.JsonUtils;
import org.redisson.misc.Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by watermelon on 2019/04/05
 */
@Service
public class DeDuplicateServcie {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ComicService comicService;

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private ComicTypeService comicTypeService;

    public void deDup() {
        Integer count = lessonService.count(null);
        logger.info("根据lesson查找要删除的数据,开始,lesson总数量:{}", count);

        /**
         * 以lesson重复去删对应的chapter与comic
         */
//        for (int i = 0; i < count; i++) {
//            Lesson lesson = lessonService.page(null, 0, 1).getContent().get(0);
//            /**
//             * 查找同名的漫画
//             */
//            Lesson lessonTmp = new Lesson();
//            lessonTmp.setName(lesson.getName());
//            lessonTmp.setPath(lesson.getPath());
//            List<Lesson> lessons = lessonService.findAll(lessonTmp);
//            for (Lesson les : lessons) {
//                if (les.getId() != lesson.getId()) {
//                    Lesson lessonTemp = new Lesson();
//                    lessonTemp.setId(les.getId());
//                    lessonTemp.setDeleted(true);
//                    lessonService.save(lessonTemp);
//
//                    Chapter chapter = new Chapter();
//                    chapter.setId(les.getChapterId());
//                    chapter.setDeleted(true);
//                    chapterService.save(chapter);
//
//                    Comic comic = new Comic();
//                    comic.setId(les.getComicId());
//                    comic.setDeleted(true);
//                    comicService.save(comic);
//                    logger.info("查找到重复lesson,lesson:{}", JsonUtils.toJson(les, Lesson.class));
//                }
//            }
//            logger.info("根据lesson查找要删除的数据,进度:{}", (float) i / count * 100 + "%");
//        }

        count = chapterService.count(null);
        logger.info("根据chapter查找要删除的数据,开始,chapter总数量:{}", count);
        for (int i = 0; i < count; i++) {
            Chapter chapter = chapterService.page(null, i, 1).getContent().get(0);

            /**
             * 查看这章漫画下有没有lesson,没有则删除
             */
            Lesson lesson = new Lesson();
            lesson.setChapterId(chapter.getId());
            lesson.setDeleted(false);
            List<Lesson> lessonList = lessonService.findAll(lesson);
            if (lessonList.size() == 0) {
                chapter.setDeleted(true);
                chapterService.save(chapter);
                logger.info("查找到无效chapter,chapter:{}", JsonUtils.toJson(chapter, Chapter.class));
            }
            logger.info("根据chapter查找要删除的数据,进度:{}", (float) i / count * 100 + "%");
        }

        count = comicService.count(null);
        logger.info("根据comic查找要删除的数据,开始,comic总数量:{}", count);
        for (int i = 0; i < count; i++) {
            Comic comic = comicService.page(null, i, 1).getContent().get(0);
            /**
             * 看看这漫画下有没有章节，没有的话则删除此漫画
             */
            Chapter chapter = new Chapter();
            chapter.setComicId(comic.getId());
            chapter.setDeleted(false);
            List<Chapter> chapters = chapterService.findAll(chapter);
            if (chapters.size() == 0) {
                comic.setDeleted(true);
                comicService.save(comic);
                logger.info("查找到无效comic,comicr:{}", JsonUtils.toJson(comic, Comic.class));
            }
            logger.info("根据comic查找要删除的数据,进度:{}", (float) i / count * 100 + "%");
        }

        count = comicTypeService.count(null);
        logger.info("根据comicType查找要整理的类型数据,comicType数量:{}", count);
        for (int i = 0; i < count; i++) {
            ComicType comicType = comicTypeService.page(null, i, 0).getContent().get(0);
            Comic comic = new Comic();
            comic.setId(comicType.getComicId());
            comic.setDeleted(false);
            comic = comicService.findOne(comic);
            Comic comicTemp = new Comic();
            comicTemp.setUrl(comic.getUrl());
            comicTemp.setDeleted(true);
            comicTemp.setName(comicTemp.getName());
            comicTemp.setCoverUrl(comicTemp.getCoverUrl());
            List<Comic> comicList = comicService.findAll(comicTemp);
            for (Comic c : comicList) {
                ComicType ct = new ComicType();
                ct.setComicId(c.getId());
                List<ComicType> ctList = comicTypeService.findAll(ct);
                for (ComicType cot : ctList) {
                    cot.setComicId(comic.getId());
                    cot.setUpdateTime(new Date());
                    comicTypeService.save(cot);
                }
            }
        }
    }
}
