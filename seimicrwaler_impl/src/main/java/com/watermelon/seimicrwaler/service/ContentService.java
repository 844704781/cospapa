package com.watermelon.seimicrwaler.service;

import com.watermelon.seimicrwaler.dao.ContentDao;
import com.watermelon.seimicrwaler.entity.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by watermelon on 2019/04/09
 */
@Service
public class ContentService {

    @Autowired
    private ContentDao contentDao;

    public Content save(Content content) {

        Date now =new Date();
        content.setCreateTime(now);
        content.setUpdateTime(now);
        content.setDeleted(false);

        return contentDao.save(content);
    }
}
