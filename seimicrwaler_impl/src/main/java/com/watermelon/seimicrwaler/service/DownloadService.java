package com.watermelon.seimicrwaler.service;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
        FileUtils.copyURLToFile(new URL(url),FileUtils.getFile(path),10000,10000);
    }

    @Async
    public void downloadCover(String url, Integer id) {
        String path=comicResourcePath+"/"+id+"/"+"cover.jpg";
        logger.info("线程:{},正在下载",Thread.currentThread().getName());
        try {
            FileUtils.copyURLToFile(new URL(url),FileUtils.getFile(path),10000,10000);
        } catch (IOException e) {
            logger.info("线程:{},下载失败,正在重新下载,url:{}",Thread.currentThread().getName(),url);
            try {
                FileUtils.copyURLToFile(new URL(url),FileUtils.getFile(path),10000,10000);
            } catch (IOException e1) {
                logger.info("线程:{},下载失败,正在重新下载,url:{}",Thread.currentThread().getName(),url);
                try {
                    FileUtils.copyURLToFile(new URL(url),FileUtils.getFile(path),10000,10000);
                } catch (IOException e2) {
                    e2.printStackTrace();
                    logger.info("线程:{},下载失败,url:{}",Thread.currentThread().getName(),url);
                }

            }
        }
    }

}
