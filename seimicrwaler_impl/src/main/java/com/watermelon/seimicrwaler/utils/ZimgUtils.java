package com.watermelon.seimicrwaler.utils;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import lombok.Data;

/**
 * Created by watermelon on 2019/04/09
 */
public class ZimgUtils {

    public static String upload(String url, String zimgUrl) {
        HttpResponse response = download(url);
        return upload(zimgUrl, response.bodyBytes(),response.contentType());
    }

    private static HttpResponse download(String url) {
        HttpRequest httpRequest = HttpRequest.get(url);
        HttpResponse httpResponse = httpRequest.send();
        return httpResponse;
    }

    private static String upload(String zimgUrl, byte[] bytes,String contentType) {
        HttpRequest httpRequest = HttpRequest.post(zimgUrl)
                .body(bytes, contentType);
        HttpResponse httpResponse = httpRequest.send();
        return httpResponse.bodyText();
    }

    @Data
    public class Response {
        private Boolean ret;
        private Error error;
        private Info info;

        @Data
        public class Info {
            private String md5;
            private Long size;
        }

        @Data
        public class Error {
            private Integer code;
            private String message;
        }
    }

    public static void main(String[] args) {
        String url = "https://res.gufengmh8.com/images/comic/180/359320/1524308695jzqQNpGY9Juu3CU-.jpg";
        String zimUrl = "http://cospapa.cn:4869/upload";
        String result = upload(url, zimUrl);
        System.out.println(result);
    }
}
