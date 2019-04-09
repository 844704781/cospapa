package com.watermelon.seimicrwaler.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by watermelon on 2019/04/09
 */
public class ZimgUtils {

    private static CloseableHttpClient httpClient;

    static {
        httpClient = HttpClients.createDefault();
    }

    private static RequestConfig config = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();

    public static String upload(String url, String zimgUrl) {
        InputStream inputStream = download(url);
        String result = upload(zimgUrl, inputStream);
        closeInputStream(inputStream);
        return result;
    }

    private static InputStream download(String url) {
        CloseableHttpResponse response = null;
        HttpGet httpGet = null;
        try {
            httpGet = new HttpGet(url);

            httpGet.setConfig(config);
            response = httpClient.execute(httpGet);

            if (response.getStatusLine().getStatusCode()!=200) {
                throw new RuntimeException("获取图片出错,url:" + url);
            }
            InputStream inputStream= response.getEntity().getContent();
            return inputStream;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    private static void closeCloseableHttpResponse(CloseableHttpResponse response) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private static String upload(String zimgUrl, InputStream inputStream) {
        CloseableHttpResponse response = null;
        HttpEntity entity;
        HttpPost httpPost;
        try {
            httpPost = new HttpPost(zimgUrl);
            httpPost.setHeader("Content-Type", "jpeg");
            byte data[]=IOUtils.toByteArray(inputStream);
            ByteArrayEntity entity1=new ByteArrayEntity(data);
            httpPost.setEntity(entity1);
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode()!=200) {
                throw new RuntimeException("上传出错");
            }
            HttpEntity responseEntity = response.getEntity();
            return readStringFromStream(responseEntity.getContent());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            closeCloseableHttpResponse(response);
        }

    }

    private static void closeInputStream(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private static String readStringFromStream(InputStream inputStream) {
        try {
            return IOUtils.toString(inputStream, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            closeInputStream(inputStream);
        }
    }

    public static void main(String[] args) {
        String url="https://res.gufengmh8.com/images/cover/201807/1530935442Y6Tc2lgwA6XJPVum.jpg";
        String zimUrl="http://cospapa.cn:4869/upload";
        String result=upload(url,zimUrl);
        System.out.println(result);
    }
}
