package com.sohoer.cospapa.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 漫画类型
 *
 * @author watermelon
 * @date 20181216
 */
@Data
public class Type {
    private String id;
    private String name;
    private Date createTime;
    private Boolean isDeleted;

    private List<Comic> comicList;
}
