package com.watermelon.seimicrwaler.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by watermelon on 2019/04/05
 */
@Data
@Entity(name = "lesson")
@DynamicUpdate(true)
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "comicId")
    private Integer comicId;

    @Column(name = "chapterId")
    private Integer chapterId;

    private String name;

    private String path;

    @Column(name = "createTime")
    private Date createTime;

    @Column(name = "updateTime")
    private Date updateTime;

    @Column(name = "isDeleted")
    private boolean isDeleted;
}
