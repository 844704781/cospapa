package com.watermelon.seimicrwaler.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by watermelon on 2019/04/04
 */
@Data
@Entity(name = "comic_type")
@DynamicUpdate(true)
public class ComicType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "comicId")
    private Integer comicId;

    @Column(name = "typeId")
    private Integer typeId;

    @Column(name = "createTime")
    private Date createTime;

    @Column(name = "updateTime")
    private Date updateTime;

    @Column(name = "isDeleted")
    private boolean isDeleted;

    public ComicType() {
    }

    public ComicType(Integer comicId, Integer typeId) {
        this.comicId = comicId;
        this.typeId = typeId;
    }
}
