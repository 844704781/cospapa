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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CaptureUtils {

    private static String baseURL = "http://www.gufengmh.com";
    private static String imgBaseURL = "http://res.gufengmh.com";
    //private static String comicPath = "/home/watermelon/comic";
    private static String comicPath = "C:/Users/watermelon/Documents/comic";
    //漫画总数量
    private static Float count = null;
    //最后一页数量
    private static int lastPageCount = 0;

    private static String comisJson = comicPath + "/comics.json";
    private static String captureProfileAndChaptersJson = comicPath + "/captureProfileAndChapters.json";
    private static String contentJson = comicPath + "/content.json";
    private static String captureError = comicPath + "/captureError.json";
    private static String contentError = comicPath + "/contentError.json";

    private static Gson gson = new GsonBuilder().create();

    public static class Action {
        private static String SEARCH = "/search/";
        private static String MANHUA = "/manhua/";
        private static String List = "/list/";
        //按更新顺序查找全部漫画
        private static String UPDATE = "/list/update/";
    }

    public static void main(String[] args) throws IOException {
        //获取所有漫画
        String url = baseURL + Action.List;
        List<Map<String, Object>> list = null;
        File comisJsonFile = new File(comisJson);
        if (!comisJsonFile.exists()) {
            list = getAllComics(url);
            FileUtils.writeStringToFile(comisJsonFile, gson.toJson(list
                    , new TypeToken<List<Map<String, Object>>>() {
                    }.getType()), "UTF-8");
        } else {
            list = gson.fromJson(FileUtils.readFileToString(comisJsonFile, "UTF-8")
                    , new TypeToken<List<Map<String, Object>>>() {
                    }.getType());
        }

        count = Float.parseFloat(String.valueOf(list.size()));
        //System.out.println(list);
        //获取所有的简介,章节
        System.out.println("漫画总数:" + list.size());
        System.out.println("开始获取漫画章节和简介");

        File captureProfileAndChaptersJsonFile = new File(captureProfileAndChaptersJson);
        if (!captureProfileAndChaptersJsonFile.exists()) {
            List<String> errorList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                map = captureProfileAndChapters(map);
                System.out.println("进度:" + (i / count * 100) + "%");
                if (map == null || map.size() == 0) {
                    errorList.add("获取章节出错,索引位置:" + i + ",数据为:" + list.get(i));
                    list.remove(i);
                    i = i - 1;
                    count = Float.parseFloat(String.valueOf(list.size()));
                }
            }

            FileUtils.writeStringToFile(captureProfileAndChaptersJsonFile, gson.toJson(list
                    , new TypeToken<List<Map<String, Object>>>() {
                    }.getType()), "UTF-8");
            FileUtils.writeStringToFile(new File(captureError), gson.toJson(errorList
                    , new TypeToken<List<String>>() {
                    }.getType()), "UTF-8");
        } else {
            list = gson.fromJson(FileUtils.readFileToString(captureProfileAndChaptersJsonFile, "UTF-8")
                    , new TypeToken<List<Map<String, Object>>>() {
                    }.getType());
        }

        System.out.println("获取漫画章节和简介成功");
        System.out.println("开始获取漫画内容地址");
        //异步获取漫画内容

        File contentJsonFile = new File(contentJson);
        if (!contentJsonFile.exists()) {
            List<String> errorList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                //获取漫画内容
                try {
                    map.put("chapter", getContent((List<Map<String, Object>>) map.get("chapter")));
                } catch (Exception e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("获取漫画内容地址出错/n");
                    sb.append("出问题的map:/n");
                    sb.append(gson.toJson(map, new TypeToken<Map<String, Object>>() {
                    }.getType()) + "/n");
                    sb.append("出问题的索引位置:" + i + "/n");
                    errorList.add(errorList.toString());
                    e.printStackTrace();
                }
                System.out.println("进度:" + (i / count * 100) + "%");
            }
            FileUtils.writeStringToFile(contentJsonFile, gson.toJson(list
                    , new TypeToken<List<Map<String, Object>>>() {
                    }.getType()), "UTF-8");
            FileUtils.writeStringToFile(new File(contentError), gson.toJson(errorList, new TypeToken<List<String>>() {
            }.getType()), "UTF-8");
        } else {
            list = gson.fromJson(FileUtils.readFileToString(contentJsonFile, "UTF-8")
                    , new TypeToken<List<Map<String, Object>>>() {
                    }.getType());
        }
        System.out.println("获取漫画内容地址成功");
        System.out.println("开始获取漫画内容");
        //下载
        ExecutorService pool = Executors.newFixedThreadPool(100);
        for (int i = list.size(); i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            Callable<Map<String,Object>> run = new Callable<Map<String,Object>>(){
                @Override
                public Map<String,Object> call() throws InterruptedException {
                    List<List<Map<String, Object>>> chaptersList = (List<List<Map<String, Object>>>) map.get("chapters");
                    for (int h = 0; h < chaptersList.size(); h++) {
                        List<Map<String, Object>> chapterList = chaptersList.get(h);
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
                    }
                    return map;
                }
            };
            pool.submit(run);
        }
        pool.shutdown();
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
            //Element ul = doc.getElementById("chapter-list-1");
            Elements eles = doc.getElementsByClass("comic-chapters");
            if (eles.size() == 0) {
                return null;
            }
            List<List<Map<String, Object>>> book = new ArrayList<>();
            for (int i = 0; i < eles.size(); i++) {

                List<Map<String, Object>> bookList = new ArrayList<>();

                Element bookElement = eles.get(0);
                Elements lis = bookElement.getElementsByTag("li");
                if (lis.size() == 0) {
                    continue;
                }
                lis.stream().forEach(li -> {
                    Map<String, Object> chapter = new HashMap<>();

                    //章节路径
                    Element a = li.getElementsByTag("a").first();
                    if (RegexUtils.validateChapterDetailPath(a.attr("href"))) {
                        chapter.put("href", CaptureUtils.baseURL + a.attr("href"));
                    }
                    String chapterTitle = li.getElementsByTag("span").first().text();
                    chapter.put("title", chapterTitle);
                    bookList.add(chapter);
                });
                book.add(bookList);
            }
            map.put("chapters", book);
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
    public static List<Map<String, Object>> getAllComics(String url) throws IOException {
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

    public static int getLastPageCount(String url) throws IOException {
        Document doc = null;
        doc = Jsoup.connect(url).get();
        Element lastElement = doc.getElementsByClass("pagination").first().getElementsByClass("last").first();

        String lastString = lastElement.getElementsByTag("a").first().attr("data-page");
        return Integer.parseInt(lastString);

    }

    /**
     * 获取所有漫画列表,得到的结果是按字母排序的漫画列表
     *
     * @param url
     * @return
     */

    public static Elements getAnAlphabeticalListOfComicsByURL(String url) throws IOException {
        lastPageCount = getLastPageCount(url);
        System.out.println("总页数为:" + lastPageCount);
        Elements bookList = new Elements();
        String href = baseURL + Action.UPDATE + "?page=";
        for (int i = 1; i <= lastPageCount; i++) {
            href = href + i;
            Document doc = Jsoup.connect(href).get();
            Elements bookListAPage = doc.getElementsByClass("book-list");
            bookList.addAll(bookListAPage);
            System.out.println("当前爬取页数:" + i);
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

    }
}
