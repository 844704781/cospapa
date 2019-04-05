package com.sohoer.cospapa.entity;

import lombok.Data;

import java.util.Date;

/**
 * 漫画内容
 *
 * @author watermelon
 * @date 20181216
 */
@Data
public class Content {
    private String id;
    private String comicId;
    private String name;
    private String parentId;
    private String path;
    private Date createTime;
    private Boolean isDeleted;

    private Comic comic;
    private Content parent;
}
