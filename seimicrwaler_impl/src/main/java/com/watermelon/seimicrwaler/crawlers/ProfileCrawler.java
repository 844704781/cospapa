package com.watermelon.seimicrwaler.crawlers;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import com.watermelon.seimicrwaler.entity.Chapter;
import com.watermelon.seimicrwaler.entity.Comic;
import com.watermelon.seimicrwaler.entity.Lesson;
import com.watermelon.seimicrwaler.entity.Type;
import com.watermelon.seimicrwaler.service.ChapterService;
import com.watermelon.seimicrwaler.service.ComicService;
import com.watermelon.seimicrwaler.service.LessonService;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

/**
 * 获取漫画的url，第三步
 * Created by watermelon on 2019/04/04
 */
@Crawler(name = "profile")
public class ProfileCrawler extends BaseSeimiCrawler {

    @Autowired
    private ComicService comicService;

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private LessonService lessonService;
    @Value("${gufeng.base.url}")
    private String baseUrl;

    private Integer count;

    private Integer index = 0;

    @Override
    public String[] startUrls() {

        return null;
    }

    @Override
    public List<Request> startRequests() {
        List<Request> requests = new LinkedList<>();

        logger.info("comic表开始时间:{}", new Date());

        Comic c = new Comic();
        c.setDeleted(false);
        List<Comic> comicList = comicService.findAll(c);
        logger.info("comic表结束时间:{}", new Date());

        for (int i = 0; i < comicList.size(); i++) {
            Comic comic = comicList.get(i);
            Request request = Request.build(comic.getUrl(), "start");
            Map<String, Object> map = new HashMap<>();
            map.put("comicId", comic.getId());
            map.put("url", comic.getUrl());
            request.setMeta(map);

            /**
             * 判断是否爬取漫画的章节
             */
            Chapter chapter = new Chapter();
            List<Chapter> chapters = null;
            chapter.setComicId(comic.getId());
            chapters = chapterService.page(chapter, 0, 1).getContent();
            Lesson lesson = null;
            List<Lesson> lessons = null;
            if (chapters.size() != 0) {
                //判断是否爬取章节下的小结
                lesson = new Lesson();
                lesson.setChapterId(chapter.getId());
                lesson.setComicId(comic.getId());
                lessons = lessonService.page(lesson, 0, 1).getContent();
            }
            logger.info("查询chapter进度:{}", (double) i / comicList.size() * 100 + "%");

            /**
             * 如果没有爬过章节或者没有爬过小节，则放入爬虫队列
             */
            if (chapters.size() == 0 || lessons.size() == 0) {
                requests.add(request);
                logger.info("comic的数量:{}", requests.size());
            }

        }
        count = requests.size();
        logger.info("chapter表结束时间:{},comic的数量:{}", new Date(), requests.size());
        return requests;

    }

    @Override
    public void start(Response response) {
        String profileXpath = "/body[@class='clearfix']/div[@class='wrap']/div[@class='page-main']/div[@class='comic-view clearfix']/div[@class='w998 bc cf']/div[@class='fl w728']/div[@class='book-cont cf']/div[@class='book-detail pr fr']/div[@class='book-intro']/div[@id='intro-all']/p";
        String chaptersXpath = "//div[@id='chapters']//div[@class='comic-chapters ']";
        String chapterTitleXpath = "//div[@class='caption pull-left']/span";
        String lessonXpath = "//div[@class='chapter-body clearfix']//a";
        String profile = null;
        JXDocument doc = response.document();
        Map<String, Object> meta = response.getMeta();

        try {

            Comic comic = new Comic();
            comic.setId((int) meta.get("comicId"));
            comic = comicService.findOne(comic);
            Chapter chapter = new Chapter();
            chapter.setComicId(comic.getId());
            JXNode node = doc.selNOne(profileXpath);
            if (node != null) {
                profile = node.asElement().text().split("：")[1];
            }
            comic.setProfile(profile);

            List<JXNode> chapterJXNodeList = doc.selN(chaptersXpath);
            for (JXNode jxNode : chapterJXNodeList) {
                List<JXNode> jxNodeList = jxNode.sel(chapterTitleXpath);
                if (jxNodeList.size() == 0) {
                    continue;
                }
                if (jxNode.sel(chapterTitleXpath).size() == 0) {
                    continue;
                }
                JXNode titleNode = jxNode.sel(chapterTitleXpath).get(0);
                String title = titleNode.asElement().text();

                chapter.setName(title);
                List<JXNode> aList = jxNode.sel(lessonXpath);
                List<Lesson> lessons = new ArrayList<>();
                for (JXNode a : aList) {
                    Lesson lesson = new Lesson();
                    Element element = a.asElement();
                    String url = baseUrl + element.attr("href");
                    lesson.setPath(url);
                    Elements elements = element.getElementsByTag("span");
                    if (elements.size() == 0) {
                        continue;
                    }
                    String lessonName = elements.get(0).text();
                    lesson.setName(lessonName);
                    lesson.setStatus(0);
                    //if(lessonService.findAll(lesson).size()==0){
                    lessons.add(lesson);
                    //}
                }
                comicService.saveChapter(chapter, lessons);
                index++;
                logger.info("进度:{}", (double) index / count * 100 + "%");
            }
        } catch (Exception e) {
            logger.error("漫画ID:{},URL:{},异常:", meta.get("comicId"), meta.get("url"), e);
        }
    }
}
