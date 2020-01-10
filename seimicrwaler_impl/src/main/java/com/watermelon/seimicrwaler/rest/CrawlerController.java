package com.watermelon.seimicrwaler.rest;

import cn.wanghaomiao.seimi.core.Seimi;
import cn.wanghaomiao.seimi.spring.common.CrawlerCache;
import cn.wanghaomiao.seimi.struct.CrawlerModel;
import com.watermelon.seimicrwaler.manger.DeDuplicateServcie;
import com.watermelon.seimicrwaler.utils.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by watermelon on 2019/04/05
 */
@RestController
@RequestMapping("/crawler")
@ResponseBody
public class CrawlerController {

    @Autowired
    private DeDuplicateServcie deDuplicateServcie;

    @GetMapping("/comic")
    public ResponseObject comic(String name) {
        CrawlerModel model = CrawlerCache.getCrawlerModel(name);
        if (model == null) {
            return new ResponseObject(-1, "model 没有找到");
        }
        model.startRequest();
        return new ResponseObject(0);
    }

    @GetMapping("/dedup")
    public ResponseObject deDup() {
        deDuplicateServcie.deDup();
        return new ResponseObject(0);
    }
}
