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
        //      Integer count = lessonService.count(null);
        //       logger.info("根据lesson查找要删除的数据,开始,lesson总数量:{}", count);

        /**
         * 以lesson重复去删对应的chapter与comic
         */
        //        for (int i = 0; i < count; i++) {
        //            Lesson lesson = lessonService.page(null, i, 1).getContent().get(0);
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

        List<Chapter>chapterList=chapterService.findAll(new Chapter());
        Integer chapterCount=chapterList.size();
        logger.info("根据chapter查找要删除的数据,开始,chapter总数量:{}", chapterCount);
        List<Chapter>updateChapterList=new ArrayList<>();
        for (int i = 0; i < chapterCount; i++) {
            Chapter chapter = chapterList.get(i);

            /**
             * 查看这章漫画下有没有lesson,没有则删除
             */
            Lesson lesson = new Lesson();
            lesson.setChapterId(chapter.getId());
            lesson.setDeleted(false);
            List<Lesson> lessonList = lessonService.findAll(lesson);
            if (lessonList.size() == 0) {
                chapter.setDeleted(true);
                updateChapterList.add(chapter);
                logger.info("查找到无效chapter,chapter:{}", JsonUtils.toJson(chapter, Chapter.class));
            }
            logger.info("根据chapter查找要删除的数据,进度:{}", (float) i / chapterCount * 100 + "%");
        }
        logger.info("要更新的chapter数量为{},开始更新数据",updateChapterList.size());
        chapterService.saveAll(updateChapterList);
        
        logger.info("要更新的chapter数量为{},结束更新数据",updateChapterList.size());

        List<Comic>comicList=comicService.findAll(new Comic());
        Integer comicCount=comicList.size();
        logger.info("根据comic查找要删除的数据,开始,comic总数量:{}", comicCount);
        List<Comic>updateComicList=new ArrayList<>();
        for (int i = 0; i < comicCount; i++) {
            Comic comic = comicList.get(i);
            /**
             * 看看这漫画下有没有章节，没有的话则删除此漫画
             */
            Chapter chapter = new Chapter();
            chapter.setComicId(comic.getId());
            chapter.setDeleted(false);
            List<Chapter> chapters = chapterService.findAll(chapter);
            if (chapters.size() == 0) {
                comic.setDeleted(true);
                updateComicList.add(comic);
                logger.info("查找到无效comic,comicr:{}", JsonUtils.toJson(comic, Comic.class));
            }
            logger.info("根据comic查找要删除的数据,进度:{}", (float) i / comicCount * 100 + "%");
        }

        logger.info("要更新的comic数量为{},开始更新数据",updateComicList.size());
        comicService.saveAll(updateComicList);
        logger.info("要更新的comic数量为{},结束更新数据",updateComicList.size());


        List<ComicType>comicTypeList=comicTypeService.findAll(new ComicType());
        Integer comicTypeCount=comicTypeList.size();
        logger.info("根据comicType查找要整理的类型数据,comicType数量:{}", comicTypeCount);

        List<ComicType> updateComicTypeList=new ArrayList<>();
        for (int i = 0; i < comicTypeCount; i++) {
            ComicType comicType = comicTypeList.get(i);
            Comic comic = new Comic();
            comic.setId(comicType.getComicId());
            comic.setDeleted(false);
            comic = comicService.findOne(comic);
            Comic comicTemp = new Comic();
            comicTemp.setUrl(comic.getUrl());
            comicTemp.setDeleted(true);
            comicTemp.setName(comicTemp.getName());
            comicTemp.setCoverUrl(comicTemp.getCoverUrl());
            List<Comic> comicListTemp = comicService.findAll(comicTemp);
            for (Comic c : comicListTemp) {
                ComicType ct = new ComicType();
                ct.setComicId(c.getId());
                List<ComicType> ctList = comicTypeService.findAll(ct);
                for (ComicType cot : ctList) {
                    cot.setComicId(comic.getId());
                    cot.setUpdateTime(new Date());
                    updateComicTypeList.add(cot);
                }
            }
            logger.info("根据comicType查找要删除的数据,进度:{}", (float) i / comicTypeCount * 100 + "%");
        }
        logger.info("要更新的comicType数量为{},开始更新数据",updateComicTypeList.size());
        comicTypeService.saveAll(updateComicTypeList);
        logger.info("要更新的comicType数量为{},结束更新数据",updateComicTypeList.size());
    }
}
