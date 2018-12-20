package com.sohoer.cospapa.utils;



import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author watermelon
 * @date 20181220
 */
public class IOUtils {

    public static void downloadImage(String href,String dir){
        try {
            FileUtils.copyURLToFile(new URL(href),FileUtils.getFile(dir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[]args){
        String href="http://res.gufengmh.com/images/comic/332/663954/1544783035EYQuLIxLNLvmko0k.jpg";
        String dir="C:/Users/watermelon/Documents/comic/a/1544783035EYQuLIxLNLvmko0k.jpg";
        downloadImage(href,dir);
    }
}
