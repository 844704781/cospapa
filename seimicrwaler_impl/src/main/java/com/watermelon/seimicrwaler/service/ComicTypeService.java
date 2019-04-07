package com.watermelon.seimicrwaler.service;

import com.watermelon.seimicrwaler.dao.ComicTypeDao;
import com.watermelon.seimicrwaler.entity.ComicType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.net.ContentHandler;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by watermelon on 2019/04/04
 */
@Service
public class ComicTypeService {

    @Autowired
    private ComicTypeDao comicTypeDao;

    public ComicType findOne(ComicType type) {
        Example<ComicType> example = Example.of(type);
        Optional<ComicType> optional = comicTypeDao.findOne(example);
        return optional.orElse(null);
    }

    public ComicType save(ComicType type) {
        type.setCreateTime(new Date());
        type.setUpdateTime(new Date());
        type.setDeleted(false);
        return comicTypeDao.save(type);
    }

    public List<ComicType> findAll(ComicType type) {
        if (type == null) {
            return comicTypeDao.findAll();
        }
        return comicTypeDao.findAll(Example.of(type));
    }

    public Integer count(ComicType comicType) {
        if (comicType == null) {
            return (int) comicTypeDao.count();
        }
        return (int) comicTypeDao.count(Example.of(comicType));
    }

    public Page<ComicType> page(ComicType comicType, int page, int size) {
        if (comicType == null) {
            return comicTypeDao.findAll(PageRequest.of(page, size));
        }
        return comicTypeDao.findAll(Example.of(comicType), PageRequest.of(page, size));
    }
}
