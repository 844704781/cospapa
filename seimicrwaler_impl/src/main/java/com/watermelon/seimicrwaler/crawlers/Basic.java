package com.watermelon.seimicrwaler.crawlers;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Response;
import com.watermelon.seimicrwaler.entity.Type;
import com.watermelon.seimicrwaler.service.TypeService;
import org.jsoup.nodes.Element;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * Created by watermelon on 2019/04/04
 */
@Crawler(name = "basic")
public class Basic extends BaseSeimiCrawler {

    @Autowired
    private TypeService typeService;

    @Value("${gufeng.base.url}")
    private String baseUrl;

    @Override
    public String[] startUrls() {
        return new String[] { "https://www.gufengmh8.com/list/" };
    }

    @Override
    public void start(Response response) {
        JXDocument doc = response.document();

        try {
            String typeXpath = "/body[@class='clearfix']/div[@class='wrap']/div[@class='page-main']/div[@class='w998 mt10']/div[@class='box-gray shadow-gray']/div[@id='w0']/div[@class='filter-nav clearfix']/div[@class='filter-item clearfix'][3]/ul/li/a[@href!='/list/']";

            List<JXNode> urls = doc.selN(typeXpath);
            logger.info("{}", urls.size());
            for (JXNode s : urls) {
                Element element = s.asElement();
                logger.info("type:{},url:{}", element.text(), element.attr("href"));
                String name = element.text();
                String url = baseUrl + element.attr("href");
                typeService.saveIfNotExist(new Type(name, url));
                //  push(Request.build(s.toString(),Basic::getTitle));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTitle(Response response) {
        JXDocument doc = response.document();
        try {
            logger.info("url:{} {}", response.getUrl(), doc.sel("//h1[@class='postTitle']/a/text()|//a[@id='cb_post_title_url']/text()"));
            //do something
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
