package com.sohoer.cospapa.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaptureUtils {

    private static String baseURL = "http://www.gufengmh.com";
    private static String imgBaseURL = "http://res.gufengmh.com";
    private static String comicPath = "C:/Users/watermelon/Documents/comic";
    //private static String comicPath="/home/watermelon/comic";
    private static Float count = null;

    private static String comisJson=comicPath+"/comics.json";
    private static String captureProfileAndChaptersJson=comicPath+"/captureProfileAndChapters.json";
    private static String contentJson=comicPath+"/content.json";

    private static Gson gson= new GsonBuilder().create();

    public static class Action {
        private static String SEARCH = "/search/";
        private static String List = "/manhua/";
    }

    public static void main(String[] args) throws IOException {
        //获取所有漫画
        String url = baseURL + Action.List;
        List<Map<String, Object>> list = null;
        File comisJsonFile=new File(comisJson);
        if(!comisJsonFile.exists())
        {
            list=getAllComics(url);
            FileUtils.writeStringToFile(comisJsonFile,gson.toJson(list
                    ,new TypeToken<List<Map<String, Object>>>(){}.getType()),"UTF-8");
        }else{
            list=gson.fromJson(FileUtils.readFileToString(comisJsonFile,"UTF-8")
                    ,new TypeToken<List<Map<String, Object>>>(){}.getType());
        }

        count = Float.parseFloat(String.valueOf(list.size()));
        //System.out.println(list);
        //获取所有的简介,章节
        System.out.println("漫画总数:" + list.size());
        System.out.println("开始获取漫画章节和简介");


        File captureProfileAndChaptersJsonFile=new File(captureProfileAndChaptersJson);
        if(!captureProfileAndChaptersJsonFile.exists())
        {
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                map = captureProfileAndChapters(map);
                System.out.println("进度:" + (i / count * 100) + "%");
                if (map == null) {
                    list.remove(i);
                    i = i - 1;
                    count = Float.parseFloat(String.valueOf(list.size()));
                }
            }
            FileUtils.writeStringToFile(captureProfileAndChaptersJsonFile,gson.toJson(list
                    ,new TypeToken<List<Map<String, Object>>>(){}.getType()),"UTF-8");
        }else{
            list=gson.fromJson(FileUtils.readFileToString(captureProfileAndChaptersJsonFile,"UTF-8")
                    ,new TypeToken<List<Map<String, Object>>>(){}.getType());
        }

        System.out.println("获取漫画章节和简介成功");
        System.out.println("开始获取漫画内容地址");
        //异步获取漫画内容

        File contentJsonFile=new File(contentJson);
        if(!contentJsonFile.exists())
        {
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                //获取漫画内容
                try {
                    map.put("chapter", getContent((List<Map<String, Object>>) map.get("chapter")));
                } catch (Exception e) {
                    System.err.println("出问题的map:");
                    System.out.println(map);
                    System.out.println("出问题的索引位置:" + i);
                    e.printStackTrace();
                }
                System.out.println("进度:" + (i / count * 100) + "%");
            }
            FileUtils.writeStringToFile(contentJsonFile,gson.toJson(list
                    ,new TypeToken<List<Map<String, Object>>>(){}.getType()),"UTF-8");
        }else{
            list=gson.fromJson(FileUtils.readFileToString(contentJsonFile,"UTF-8")
                    ,new TypeToken<List<Map<String, Object>>>(){}.getType());
        }
        System.out.println("获取漫画内容地址成功");
        System.out.println("开始获取漫画内容");
        //下载
        try {
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                List<Map<String, Object>> chapterList = (List<Map<String, Object>>) map.get("chapter");
                String bookName = (String) map.get("cn");
                for (int j = 0; j < chapterList.size(); j++) {
                    Map<String, Object> chapter = chapterList.get(j);
                    List<String> pathList = (List<String>) chapter.get("path");
                    String chapterName = (String) chapter.get("title");
                    for (int k = 0; k < pathList.size(); k++) {
                        String path = pathList.get(k);
                        String filePath = comicPath + "/" + bookName + "/" + chapterName + "/" + (k + 1) + ".jpg";
                        System.out.println(filePath);
                        IOUtils.downloadImage(path, filePath);
                    }
                }
                System.out.println("进度:" + (i / count * 100) + "%");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("获取漫画内容成功");

        //System.out.println(list.get(0));
        //获取所有漫画类型

        //将漫画归类

    }

    /**
     * 根据章节list获取内容地址
     *
     * @param list
     * @return
     */
    public static List<Map<String, Object>> getContent(List<Map<String, Object>> list) {
        list.stream().forEach(chapter -> {

            //漫画详情页面地址
            String href = (String) chapter.get("href");
            if (href != null) {
                Document doc = null;
                try {
                    doc = Jsoup.connect(href).get();
                    Elements elements = doc.getElementsByTag("script");
                    String scriptString = elements.get(2).toString();
                    String chapterPath = RegexUtils.getChapterPath(scriptString);
                    List<String> contentImages = RegexUtils.getImage(scriptString);
                    for (int i = 0; i < contentImages.size(); i++) {
                        contentImages.set(i, imgBaseURL + "/" + chapterPath + contentImages.get(i));
                    }

                    chapter.put("path", contentImages);

                } catch (IOException e) {
                    System.out.println("出问题的map:");
                    System.out.println(list);
                    e.printStackTrace();
                }
            }
        });
        return list;
    }

    /**
     * 根据书名找简介
     *
     * @param map
     * @return
     */
    public static Map<String, Object> captureProfileAndChapters(Map<String, Object> map) {
        Document doc = null;

        try {

            doc = Jsoup.connect((String) map.get("href")).get();
            Element div = doc.getElementById("intro-all");
            Element p = div.getElementsByTag("p").first();
            String content = p.text();
            String[] contents = content.split("：");
            map.put("profile", contents[1]);
            Element ul = doc.getElementById("chapter-list-1");
            if (ul == null) {
                return null;
            }
            Elements lis = ul.getElementsByTag("li");
            List<Map<String, Object>> list = new ArrayList<>();
            lis.stream().forEach(li -> {
                Map<String, Object> chapter = new HashMap<>();

                //章节路径
                Element a = li.getElementsByTag("a").first();
                if (RegexUtils.validateChapterDetailPath(a.attr("href"))) {
                    chapter.put("href", CaptureUtils.baseURL + a.attr("href"));
                }
                String chapterTitle = li.getElementsByTag("span").first().text();
                chapter.put("title", chapterTitle);
                list.add(chapter);
            });
            map.put("chapter", list);
            return map;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * 获取所有漫画
     *
     * @param url
     * @return
     */
    public static List<Map<String, Object>> getAllComics(String url) {
        Elements bookList = getAnAlphabeticalListOfComicsByURL(url);
        List<Map<String, Object>> list = new ArrayList<>();
        bookList.stream().forEach(bookEle -> {
            List<Map<String, Object>> bookEleList = getAllComicsByLetter(bookEle);
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
    public static List<Map<String, Object>> getAllComicsByLetter(Element element) {
        List<Map<String, Object>> comicList = new ArrayList<>();
        Elements elements = element.getElementsByClass("item-lg");
        elements.stream().forEach(bookEle -> {
            Element imgEle = bookEle.getElementsByTag("img").first();
            Element a = bookEle.getElementsByTag("a").first();
            Element updateon = bookEle.getElementsByClass("updateon").first();
            String key = bookEle.attr("data-key");
            Map<String, Object> object = new HashMap<>();
            object.put("cn", a.attr("title"));
            object.put("href", a.attr("href"));
            object.put("coverSrc", imgEle.attr("src"));
            object.put("updateTime", RegexUtils.getDate(updateon.text()));
            object.put("name", RegexUtils.getBookName(a.attr("href")));
            object.put("key", key);
            comicList.add(object);
        });
        return comicList;
    }

    public static void main1(String[] args) {
        String str = "<ul id=\"chapter-list-1\" data-sort=\"asc\"> \n"
                + " <li> <a href=\"/manhua/ailisijiadenvpuxiaojie/653683.html\" class=\"active\"> <span>短篇</span> </a> </li> \n"
                + "</ul>";
        Document doc = Jsoup.parse(str);

        Element ul = doc.getElementById("chapter-list-1");

        Elements lis = ul.getElementsByTag("li");
        System.out.println(lis);
    }
}
