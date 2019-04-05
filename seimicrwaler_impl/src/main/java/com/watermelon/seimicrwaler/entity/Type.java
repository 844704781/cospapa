package com.watermelon.seimicrwaler.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by watermelon on 2019/04/04
 */
@Data
@Entity(name = "type")
@DynamicUpdate(true)
public class Type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String url;

    @Column(name = "createTime")
    private Date createTime;
    @Column(name = "updateTime")
    private Date updateTime;
    @Column(name = "isDeleted")
    private boolean isDeleted;

    public Type() {
    }

    public Type(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
