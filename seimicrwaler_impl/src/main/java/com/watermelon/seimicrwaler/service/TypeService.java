package com.watermelon.seimicrwaler.service;

import com.watermelon.seimicrwaler.dao.TypeDao;
import com.watermelon.seimicrwaler.entity.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by watermelon on 2019/04/04
 */
@Service
public class TypeService {

    @Autowired
    private TypeDao typeDao;

    public Type findOne(Type type) {
        Example<Type> example = Example.of(type);
        Optional<Type> optional = typeDao.findOne(example);
        return optional.orElse(null);
    }

    public Type save(Type type) {
        type.setCreateTime(new Date());
        type.setUpdateTime(new Date());
        return typeDao.save(type);
    }

    public void saveIfNotExist(Type type) {
        Type old = this.findOne(type);
        if (old == null) {
            this.save(type);
        }
    }

    public List<Type> findAll(Type type) {
        if (type == null) {
            return typeDao.findAll();
        }
        return typeDao.findAll(Example.of(type));
    }
}
