package com.watermelon.seimicrwaler.service;

import com.watermelon.seimicrwaler.utils.JsonUtils;
import com.watermelon.seimicrwaler.utils.ZimgUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;

/**
 * Created by watermelon on 2019/04/06
 */
@Service
public class DownloadService {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${comic.resource.path}")
    private String comicResourcePath;




    @Value("${zimg.base.url}")
    private String zimpUrl;

    public  String downloadImage(String url) throws Exception {
        logger.info("线程:{},正在下载",Thread.currentThread().getName());
        String result=ZimgUtils.upload(url,zimpUrl+"/upload");
        ZimgUtils.Response response= JsonUtils.fromJson(result,ZimgUtils.Response.class);
        if(response.getRet().equals(false))
        {
            logger.error("将图片存入zimp失败,图片url:{},zimp响应信息为:{}",url,JsonUtils.toJson(response.getError(),ZimgUtils.Response.Error.class));
            throw new RuntimeException("将图片存入zimp失败");
        }

        return response.getInfo().getMd5();
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
