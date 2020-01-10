package com.watermelon.seimicrwaler.crawlers;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import com.watermelon.seimicrwaler.entity.Comic;
import com.watermelon.seimicrwaler.entity.ComicType;
import com.watermelon.seimicrwaler.entity.Type;
import com.watermelon.seimicrwaler.entity.TypeInfo;
import com.watermelon.seimicrwaler.service.ComicService;
import com.watermelon.seimicrwaler.service.ComicTypeService;
import com.watermelon.seimicrwaler.service.TypeInfoService;
import com.watermelon.seimicrwaler.service.TypeService;
import org.jsoup.nodes.Element;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 获取每种类型的信息，第二步
 * Created by watermelon on 2019/04/04
 */
@Crawler(name = "typeInfo")
public class TypeInfoCrawler extends BaseSeimiCrawler {

    @Autowired
    private TypeInfoService typeInfoService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private ComicService comicService;

    @Autowired
    private ComicTypeService comicTypeService;

    private List<Type> typeList;

    @Override
    public String[] startUrls() {

        typeList = typeService.findAll(null);
        return getUrls(typeList);
    }

    @Override
    public void start(Response response) {
        String lastIndexXpath = "/body[@class='clearfix']/div[@class='wrap']/div[@class='page-main']/div[@class='w998 mt16 cf']/div[@id='w1']/div[@class='page-container']/ul[@class='pagination']/li[@class='last']/a";
        JXDocument doc = response.document();

        try {
            TypeInfo tmp = new TypeInfo();
            Type type = getType(typeList, response.getUrl());
            tmp.setTypeId(type.getId());
            TypeInfo typeInfo = typeInfoService.findOne(tmp);

            if (typeInfo == null) {
                typeInfo = new TypeInfo();
                typeInfo.setTypeId(tmp.getTypeId());
            }

            JXNode node = doc.selNOne(lastIndexXpath);
            Integer lastIndex = 1;
            if (node != null) {
                String tindex = node.asElement().attr("data-page");
                lastIndex = Integer.parseInt(tindex) + 1;
            }

            //#TODO 判断最后的页数是否更新,如果更新了，则获取最后一页的数据，将最后一页的数量与最后的页码更新进数据库

            //#TODO 获取最后一页的数据，判断最后一页的数据是否有更新，如果更新了，将新的最后一页的数据更新进数据库

            typeInfo.setLastIndex(lastIndex);
            typeInfoService.saveOrUpdate(typeInfo);

            logger.info("type:{}",type);
            logger.info("typeInfo:{}",typeInfo);
            for (int i = 0; i < lastIndex; i++) {
                String url = type.getUrl() + i + "/";
                Request request = Request.build(url, TypeInfoCrawler::handleEveryPage);
                HashMap<String, Object> map = new HashMap<>();
                map.put("typeId", type.getId());
                request.setMeta(map);
                push(request);
                logger.info("推入队列:(i:{},lastIndex:{})", i, lastIndex);
            }

        } catch (Exception e) {
            e.printStackTrace();
            //#TODO 记录抓取出错
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void handleEveryPage(Response response) {
        JXDocument doc = response.document();
        String listXpath = "/body[@class='clearfix']/div[@class='wrap']/div[@class='page-main']/div[@class='w998 mt16 cf']/div[@id='w1']/ul[@id='contList']/li[@class='item-lg']";
        try {
            List<JXNode> comicList = doc.selN(listXpath);
            for (JXNode jxNode : comicList) {
                Element comic = jxNode.asElement();
                Element a = comic.getElementsByTag("a").first();
                String url = a.attr("href");
                String name = a.attr("title");
                String coverURL = a.getElementsByTag("img").first().attr("src");
                Integer typeId = (Integer) response.getMeta().get("typeId");

                /**
                 * 更新comic
                 */
                Comic comicModel = new Comic();
                comicModel.setUrl(url);
                comicModel.setCoverUrl(coverURL);
                comicModel.setName(name);
                if (comicService.findAll(comicModel).size() == 0) {
                    logger.info("save comicModel:{}", comicModel);
                    comicModel = comicService.save(comicModel);

                    ComicType comicType = new ComicType(comicModel.getId(), typeId);
                    logger.info("save comic type:{}", comicType);
                    comicTypeService.save(comicType);
                }
            }
        } catch (Exception e) {
            logger.error("cause:", e);
            //#TODO 记录抓取出错
        }
    }

    private static String[] getUrls(List<Type> typeList) {
        List<String> urlList = new ArrayList<>();
        typeList.forEach((type) -> {
            urlList.add(type.getUrl());
        });

        return urlList.toArray(new String[urlList.size()]);
    }

    private Type getType(List<Type> typeList, String url) {
        for (Type type : typeList) {
            if (type.getUrl().equals(url)) {
                return type;
            }
        }
        return null;
    }
}
