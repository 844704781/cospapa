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
    private static String imgBaseURL="http://res.gufengmh.com";

    public static class Action {
        private static String SEARCH = "/search/";
        private static String List = "/manhua/";
    }

    public static void main(String[] args) throws IOException {
        //获取所有漫画
        String url = baseURL + Action.List;
        List<Map<String, Object>> list = getAllComics(url);
        //System.out.println(list);
        //获取所有的简介,章节
        System.out.println("漫画总数:"+list.size());
        for(int i=0;i<list.size();i++)
        {
            Map<String,Object>map=list.get(i);
            map = captureProfileAndChapters(map);
            if(true)
            {
                break;
            }
        }
        //异步获取漫画内容
        for(int i=0;i<list.size();i++)
        {
            Map<String,Object>map=list.get(i);
            //获取漫画内容
            getContent(map);

            if(true){
                break;
            }
        }
        //下载

        System.out.println(list.get(0));
        //获取所有漫画类型

        //将漫画归类

    }

    public static void getContent(Map<String,Object>map){
        List<Map<String,Object>>list= (List<Map<String, Object>>) map.get("chapter");
        list.stream().forEach(chapter->{

            //漫画详情页面地址
            String href=(String)chapter.get("href");
            Document doc=null;
            try {
                doc=Jsoup.connect(href).get();
                Elements elements=doc.getElementsByTag("script");
                String scriptString=elements.get(2).toString();
                String chapterPath =RegexUtils.getChapterPath(scriptString);
                List<String>contentImages=RegexUtils.getImage(scriptString);
                for(int i=0;i<contentImages.size();i++)
                {
                    contentImages.set(i,imgBaseURL+"/"+chapterPath+contentImages.get(i));
                }

                chapter.put("path",contentImages);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 根据书名找简介
     *
     * @param map
     * @return
     */
    public static Map<String,Object> captureProfileAndChapters(Map<String,Object> map) {
        Document doc=null;

        try{
            doc= Jsoup.connect((String)map.get("href")).get();
            Element div=doc.getElementById("intro-all");
            Element p=div.getElementsByTag("p").first();
            String content=p.text();
            String [] contents=content.split("：");
            map.put("profile",contents[1]);
            Element ul=doc.getElementById("chapter-list-1");
            Elements lis=ul.getElementsByTag("li");
            List<Map<String,Object>>list=new ArrayList<>();
            lis.stream().forEach(li->{
                 Map<String,Object>chapter=new HashMap<>();

                 Element a=li.getElementsByTag("a").first();
                 chapter.put("href",CaptureUtils.baseURL+a.attr("href"));
                 String chapterTitle=li.getElementsByTag("span").first().text();
                 chapter.put("title",chapterTitle);
                 list.add(chapter);
            });
            map.put("chapter",list);
            return map;
        }catch (IOException ex){
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
            object.put("key",key);
            comicList.add(object);
        });
        return comicList;
    }
}
