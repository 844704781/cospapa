package com.sohoer.cospapa.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 漫画
 *
 * @author watermelon
 * @date 20181216
 */
@Data
public class Comic {
    private String id;
    private String name;
    private String cn;
    private String cover;
    private String profile;
    private Date updateTime;
    private Date createTime;
    private Boolean isDeleted;

    private List<Type> typeList;

}
