package com.watermelon.seimicrwaler.service;

import com.watermelon.seimicrwaler.dao.TypeDao;
import com.watermelon.seimicrwaler.dao.TypeInfoDao;
import com.watermelon.seimicrwaler.entity.TypeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

/**
 * Created by watermelon on 2019/04/04
 */
@Service
public class TypeInfoService {

    @Autowired
    private TypeInfoDao typeInfoDao;

    public TypeInfo findOne(TypeInfo typeInfo) {
        Example<TypeInfo> example = Example.of(typeInfo);
        Optional<TypeInfo> optional = typeInfoDao.findOne(example);
        return optional.orElse(null);
    }

    public TypeInfo save(TypeInfo typeInfo) {
        typeInfo.setCreateTime(new Date());
        typeInfo.setUpdateTime(new Date());
        return typeInfoDao.save(typeInfo);
    }

    public void saveOrUpdate(TypeInfo typeInfo) {
        TypeInfo old = this.findOne(typeInfo);
        if (old == null) {
            this.save(typeInfo);
        } else {
            typeInfo.setId(old.getId());
            this.save(typeInfo);
        }
    }

}
