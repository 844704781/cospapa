package com.watermelon.seimicrwaler.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by watermelon on 2019/04/05
 */
@Data
@Entity(name = "chapter")
@DynamicUpdate(true)
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "comicId")
    private Integer comicId;

    private String name;

    @Column(name = "createTime")
    private Date createTime;

    @Column(name = "updateTime")
    private Date updateTime;

    @Column(name = "isDeleted")
    private boolean isDeleted;

    public Chapter(Integer id) {
        this.id = id;
    }

    public Chapter() {

    }
}
