package com.sohoer.cospapa.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    private static String DATE = "\\b[0-9]{4}-[0-1][0-2]-[0-3][0-9]\\b";
    private static String NAME = "\\/([\\d\\w]*)\\/$";
    private static String IMAGE = "\"(.{1,40}\\.jpg)\"";
    private static String CHAPTERPATH="\"(images\\/comic\\/\\d+\\/\\d+\\/)\"";
    private static String CHAPTERDETAIL="manhua\\/.+\\/.+\\.html";

    /**
     * 根据正则regex去字符串content中过滤
     *
     * @param content
     * @param regex
     * @param groupIndex
     * @return
     */
    private static String filter(String content, String regex, Integer groupIndex) {
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(content);
        while (matcher.find()) {
            return matcher.group(groupIndex);
        }
        return null;
    }

    private static boolean find(String content,String regex){
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(content);
        while(matcher.find()){
            return true;
        }
        return false;
    }

    private static List<String> getArraysFilter(String content, String regex , Integer index) {
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(content);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group(index));
        }
        return list;
    }

    private static String filter(String content, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(content);
        while (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static String getChapterPath(String content){
        return filter(content,CHAPTERPATH,1);
    }

    public static List<String> getImage(String content) {
        return getArraysFilter(content, IMAGE ,1);
    }

    /**
     * 获取更新时间
     *
     * @param content
     * @return
     */
    public static String getDate(String content) {
        return filter(content, RegexUtils.DATE);
    }

    /**
     * 获取书的英文名称
     *
     * @param content
     * @return
     */
    public static String getBookName(String content) {
        return filter(content, RegexUtils.NAME, 1);
    }


    public static boolean validateChapterDetailPath(String content){
        return find(content,CHAPTERDETAIL);
    }

    public static void main(String[] args) {
        //System.out.println(getDate("更新于：2018-11-19 <em>1.0</em>"));
        //String str = "<script>;var siteName = \"\";var siteUrl = \"http://www.gufengmh.com\";;var chapterImages = [\"14817390643731529305b9a1263.jpg\"];var chapterPath = \"images/comic/8/15293/\";var pageTitle = \"游区.异人图412考试在线观看\";var comicUrl = \"http://www.gufengmh.com/manhua/youquyirentu/\";var pageUrl = \"http://www.gufengmh.com/manhua/youquyirentu/\";var pageImage = \"http://res.gufengmh.com/images/cover/201803/1522504001V8MPmiaW-p9r_UAt.jpg\";var pageDomain = \"http://www.gufengmh.com\";var pageId = \"comic.343\";var prevChapterData = {\"id\":15292,\"comic_id\":343,\"comic_name\":\"游区.异人图\",\"status\":1,\"vip\":0,\"is_end\":0,\"name\":\"411花样\",\"type\":0,\"rtl\":0,\"image_mode\":0,\"category\":1,\"link\":\"\",\"link_name\":\"\",\"image_type\":0,\"count\":1,\"sort\":999,\"price\":0,\"created_at\":1481645095,\"updated_at\":1481739062};var nextChapterData = {\"id\":15294,\"comic_id\":343,\"comic_name\":\"游区.异人图\",\"status\":1,\"vip\":0,\"is_end\":0,\"name\":\"413无色\",\"type\":0,\"rtl\":0,\"image_mode\":0,\"category\":1,\"link\":\"\",\"link_name\":\"\",\"image_type\":0,\"count\":1,\"sort\":999,\"price\":0,\"created_at\":1481645095,\"updated_at\":1481739075};</script>";
//        //String str = "\"14817390643731529305b9a1263.jpg\"";
        //System.out.println(getImage(str));

       // String str="jpg\"];var chapterPath = \"images/comic/332/663954/\";var pageTitle = \"傲娇王爷太难追预告在线观看\";var comi";
       // System.out.println(getChapterPath(str));
        System.out.println(validateChapterDetailPath("/manhua/shendunjutegongv1:javascript:void"));
    }

}
