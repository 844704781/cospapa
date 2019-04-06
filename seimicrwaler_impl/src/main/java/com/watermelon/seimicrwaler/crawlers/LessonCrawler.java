package com.watermelon.seimicrwaler.crawlers;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import java.io.File;
import com.watermelon.seimicrwaler.entity.Chapter;
import com.watermelon.seimicrwaler.entity.Comic;
import com.watermelon.seimicrwaler.entity.Lesson;
import com.watermelon.seimicrwaler.service.ChapterService;
import com.watermelon.seimicrwaler.service.ComicService;
import com.watermelon.seimicrwaler.service.LessonService;
import com.watermelon.seimicrwaler.utils.RegexUtils;

import org.seimicrawler.xpath.JXDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by watermelon on 2019/04/05
 */
@Crawler(name = "lesson")
public class LessonCrawler extends BaseSeimiCrawler {

    private Integer count;

    private Integer index = 0;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private ComicService comicService;

    @Value("${gufeng.rs.base.url}")
    private String rsBaseUrl;

    @Value("${comic.resource.path}")
    private String comicResourcePath;

    @Override
    public String[] startUrls() {
        return null;
    }

    @Override
    public List<Request> startRequests() {
        List<Request> requests = new LinkedList<>();

        count = lessonService.count(null);

        for (int i = 0; i < count; i++) {
            List<Lesson> lessonList = lessonService.page(null, i, 1).getContent();

            Lesson lesson = lessonList.get(0);
            String url = lesson.getPath();
            logger.info("进度:{},name:{},url:{}", (double) i / count * 100 + "%", lesson.getName(), url);
            Request request = Request.build(url, "start");
            Map<String, Object> map = new HashMap<>();
            map.put("lessonId", lesson.getId());
            map.put("url", lesson.getPath());
            request.setMeta(map);
            requests.add(request);
        }
        return requests;
    }

    @Override
    public void start(Response response) {
        String scriptXpath = "//script";
        String CHAPTERPATH="\"(images\\/comic\\/\\d+\\/\\d+\\/)\"";
        String IMAGE = "\"(.{1,40}\\.jpg)\"";
        JXDocument doc = response.document();
        Map<String,Object>meta=response.getMeta();

        try {


            Lesson lesson=lessonService.findOne(new Lesson((Integer) meta.get("lessonId")));
            Chapter chapter=chapterService.findOne(new Chapter(lesson.getChapterId()));
            Comic comic = comicService.findOne(new Comic(lesson.getComicId()));
            Object script = doc.selOne(scriptXpath);
            String chapterPath = RegexUtils.filter(script.toString(), CHAPTERPATH, 1);
            List<String> images = RegexUtils.getArraysFilter(script.toString(), IMAGE, 1);

            lesson.setPage(images.size());

            for (int i = 0; i < images.size(); i++) {
                String url =rsBaseUrl + "/" + chapterPath + images.get(i);
                Request request = Request.build(url, LessonCrawler::downloadImage);
                Map<String,Object>map=new HashMap<>();
                map.put("comicId",comic.getId());
                map.put("chapterId",chapter.getId());
                map.put("lessonId",lesson.getId());
                request.setMeta(map);
                push(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadImage(Response response){

        Map<String,Object>meta = response.getMeta();
        String path=comicResourcePath+"/"+meta.get("comicId")+"/"+meta.get("chapterId")+"/"+meta.get("lessonId");
        response.saveTo(new File(path));
    }



}