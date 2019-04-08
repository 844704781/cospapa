package com.watermelon.seimicrwaler.service;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Map;

/**
 * Created by watermelon on 2019/04/06
 */
@Service
public class DownloadService {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${comic.resource.path}")
    private String comicResourcePath;

    public void downloadImage(Map<String,Object> meta,String url,Integer i) throws Exception {
        String path=comicResourcePath+"/"+meta.get("comicId")+"/"+meta.get("chapterId")+"/"+meta.get("lessonId")+"/"+i+".jpg";
        logger.info("线程:{},正在下载",Thread.currentThread().getName());
        FileUtils.copyURLToFile(new URL(url),FileUtils.getFile(path));
    }
}
