package com.watermelon.seimicrwaler.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by watermelon on 2019/04/04
 */
@Data
@Entity(name = "comic")
@DynamicUpdate(true)
public class Comic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String url;

    private String cn;
    private String cover;
    @Column(name = "coverUrl")
    private String coverUrl;

    private String profile;

    @Column(name = "createTime")
    private Date createTime;
    @Column(name = "updateTime")
    private Date updateTime;
    @Column(name = "isDeleted")
    private boolean isDeleted;

    public Comic(Integer id) {
        this.id = id;
    }

    public Comic(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public Comic(){

    }
}
