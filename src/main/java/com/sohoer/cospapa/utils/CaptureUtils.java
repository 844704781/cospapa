package com.sohoer.cospapa.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaptureUtils {

    private static String baseURL = "http://www.gufengmh.com";

    public static class Action {
        private static String SEARCH = "/search/";
        private static String List = "/manhua/";
    }

    public static void main(String[] args) throws IOException {
        //获取所有漫画
        String url = baseURL + Action.List;
        List<Map<String, String>> list = getAllComics(url);
        //System.out.println(list);
        //获取所有的简介,漫画类型
        list.stream().forEach(map -> {
            String profile = getProfile(map.get("name"));
            map.put("profile", profile);
        });
        //将漫画归类

    }

    /**
     * 根据书名找简介
     *
     * @param bookName
     * @return
     */
    public static String getProfile(String bookName) {
        String detailURL = baseURL + Action.List + bookName + "/";

        return null;
    }

    /**
     * 获取所有漫画
     *
     * @param url
     * @return
     */
    public static List<Map<String, String>> getAllComics(String url) {
        Elements bookList = getAnAlphabeticalListOfComicsByURL(url);
        List<Map<String, String>> list = new ArrayList<>();
        bookList.stream().forEach(bookEle -> {
            List<Map<String, String>> bookEleList = getAllComicsByLetter(bookEle);
            bookEleList.stream().forEach(book -> {
                list.add(book);
            });
        });
        return list;
    }

    /**
     * 获取所有漫画列表,得到的结果是按字母排序的漫画列表
     *
     * @param url
     * @return
     */
    public static Elements getAnAlphabeticalListOfComicsByURL(String url) {
        Document doc = null;
        Elements bookList = null;
        try {
            doc = Jsoup.connect(url).get();
            bookList = doc.getElementsByClass("book-list");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookList;
    }

    /**
     * 获取每个字母列表下的具体每本漫画
     *
     * @param element
     * @return
     */
    public static List<Map<String, String>> getAllComicsByLetter(Element element) {
        List<Map<String, String>> comicList = new ArrayList<>();
        Elements elements = element.getElementsByClass("item-lg");
        elements.stream().forEach(bookEle -> {
            Element imgEle = bookEle.getElementsByTag("img").first();
            Element a = bookEle.getElementsByTag("a").first();
            Element updateon = bookEle.getElementsByClass("updateon").first();
            Map<String, String> object = new HashMap<>();
            object.put("cn", a.attr("title"));
            object.put("href", a.attr("href"));
            object.put("coverSrc", imgEle.attr("src"));
            object.put("updateTime", RegexUtils.getDate(updateon.text()));
            object.put("name", RegexUtils.getBookName(a.attr("href")));
            comicList.add(object);
        });
        return comicList;
    }
}
