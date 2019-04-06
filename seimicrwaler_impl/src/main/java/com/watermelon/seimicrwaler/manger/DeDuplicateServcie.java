package com.watermelon.seimicrwaler.manger;

import com.watermelon.seimicrwaler.dao.ComicDao;
import com.watermelon.seimicrwaler.entity.Chapter;
import com.watermelon.seimicrwaler.entity.Comic;
import com.watermelon.seimicrwaler.entity.Lesson;
import com.watermelon.seimicrwaler.service.ChapterService;
import com.watermelon.seimicrwaler.service.ComicService;
import com.watermelon.seimicrwaler.service.LessonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


    public void deDup() {
        int start = 1;
        int end = 54198;

        List<Integer> comicIdList = new ArrayList<>();

        for (int i = start; i < end; i++) {
            Comic current = comicService.findOne(new Comic(i));
            List<Comic> group = comicService.findAll(new Comic(current.getName(), current.getUrl()));
            for (int j = 0; j < group.size(); j++) {
                if (j == 0) {
                    continue;
                }
                Comic temp = group.get(i);
                temp.setUpdateTime(new Date());
                temp.setDeleted(true);
                comicService.save(temp);
                comicIdList.add(temp.getId());

            }
        }

        for (Integer comicId : comicIdList) {
            Chapter chapter = new Chapter();
            chapter.setComicId(comicId);
            List<Chapter> group = chapterService.findAll(chapter);
            for (Chapter g : group) {
                g.setUpdateTime(new Date());
                g.setDeleted(true);
            }
            chapterService.saveAll(group);
        }

        for (Integer comicId : comicIdList) {
            Lesson lesson = new Lesson();
            lesson.setComicId(comicId);
            List<Lesson> group = lessonService.findAll(lesson);
            for (Lesson g : group) {
                g.setUpdateTime(new Date());
                g.setDeleted(true);
            }
            lessonService.saveAll(group);
        }
    }
}
