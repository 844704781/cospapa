package com.watermelon.seimicrwaler.crawlers;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Response;
import com.watermelon.seimicrwaler.entity.Comic;
import com.watermelon.seimicrwaler.service.ComicService;
import com.watermelon.seimicrwaler.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by watermelon on 2019/04/09
 */
@Crawler(name = "crawler")
public class CoverCrawler extends BaseSeimiCrawler {

    @Autowired
    private ComicService comicService;

    @Autowired
    private DownloadService downloadService;

    @Override
    public String[] startUrls() {

        Comic comic=new Comic();
        List<Comic> comicList=comicService.findAll(comic);
        for(Comic c:comicList){
            downloadService.downloadCover(c.getUrl(),c.getId());
        }
        return null;
    }

    @Override
    public void start(Response response) {

    }
}
