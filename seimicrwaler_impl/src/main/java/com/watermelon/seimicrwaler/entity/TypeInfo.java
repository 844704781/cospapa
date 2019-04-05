package com.watermelon.seimicrwaler.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by watermelon on 2019/04/04
 */
@Data
@Entity(name = "typeInfo")
@DynamicUpdate(true)
public class TypeInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer typeId;

    private Integer lastIndex;

    private Integer lastNumber;

    @Column(name = "createTime")
    private Date createTime;
    @Column(name = "updateTime")
    private Date updateTime;
    @Column(name = "isDeleted")
    private boolean isDeleted;
}
