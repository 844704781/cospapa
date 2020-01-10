package com.watermelon.seimicrwaler.crawlers;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;

import java.io.File;

import com.alibaba.druid.support.json.JSONUtils;
import com.watermelon.seimicrwaler.entity.Chapter;
import com.watermelon.seimicrwaler.entity.Comic;
import com.watermelon.seimicrwaler.entity.Content;
import com.watermelon.seimicrwaler.entity.Lesson;
import com.watermelon.seimicrwaler.service.*;
import com.watermelon.seimicrwaler.utils.JsonUtils;
import com.watermelon.seimicrwaler.utils.RegexUtils;

import org.apache.commons.io.FileUtils;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * 开始下载图片，最后一步
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

    @Autowired
    private ContentService contentService;

    @Autowired
    private DownloadService downloadService;

    @Value("${gufeng.rs.base.url}")
    private String rsBaseUrl;

    @Override
    public String[] startUrls() {
        return null;
    }

    @Override
    public List<Request> startRequests() {
        List<Request> requests = new LinkedList<>();
        Lesson l = new Lesson();
        l.setStatus(0);
        List<Lesson> lessons = lessonService.findAll(l);
        count = lessons.size();
        logger.info("开始加载lessons,请稍后...,总数量:{}", lessons.size());
        for (int i = 0; i < lessons.size(); i++) {

            Lesson lesson = lessons.get(i);
            String url = lesson.getPath();
            Request request = Request.build(url, "start");
            Map<String, Object> map = new HashMap<>();
            map.put("lesson", JsonUtils.toJson(lesson, lesson.getClass()));
            request.setMeta(map);
            requests.add(request);
            logger.info("进度:{}%", (float) i / lessons.size() * 100);
        }
        logger.info("加载lessons结束,开始请求数据...");
        return requests;
    }

    @Override
    public void start(Response response) {
        String scriptXpath = "//body/script";
        String CHAPTERPATH = "\"(images\\/comic\\/\\d+\\/\\d+\\/)\"";
        String IMAGE = "\"(.{1,40}\\.jpg)\"";
        JXDocument doc = response.document();
        Map<String, Object> meta = response.getMeta();

        try {

            JXNode script = doc.selNOne(scriptXpath);
            String chapterPath = RegexUtils.filter(script.toString(), CHAPTERPATH, 1);
            List<String> images = RegexUtils.getArraysFilter(script.toString(), IMAGE, 1);
            logger.info("meta:{}", meta);
            Lesson lesson = JsonUtils.fromJson((String) meta.get("lesson"), Lesson.class);
            lesson.setPage(images.size());

            Content content = new Content();
            content.setLessonId(lesson.getId());
            content.setComicId(lesson.getComicId());
            content.setChapterId(lesson.getChapterId());

            List<Content.Image> imageList = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                String url = rsBaseUrl + "/" + chapterPath + images.get(i);
                logger.info("url:{}", url);

                logger.info("开始下载图片,lesson:{}", lesson);
                String hash = downloadService.downloadImage(url);
                logger.info("保存成功");

                Content.Image image = new Content.Image();
                image.setHash(hash);
                image.setIndex(i);
                imageList.add(image);
            }
            content.setImages(imageList);
            contentService.save(content);
            logger.info("保存content,content:{}", content);
            lesson.setStatus(1);
            lessonService.save(lesson);
            logger.info("更新lesson,lesson:{}", lesson);
            index++;
            logger.info("进度:{},name:{},url:{}", (float) index / count * 100 + "%", lesson.getName(), lesson.getPage());
        } catch (Exception e) {
            logger.error("cause:", e);
        }
    }

}
