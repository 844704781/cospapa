package com.watermelon.seimicrwaler.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by watermelon on 2019/04/09
 */
@Data
@Document
public class Content implements Serializable {
    private static final long serialVersionUID=1L;

    @Id
    private String _id;

    private Integer lessonId;
    private Integer comicId;
    private Integer chapterId;

    private List<Image> images;

    private Date createTime;

    private Date updateTime;

    private boolean isDeleted;


    @Data
    public static class Image implements Serializable{
        private static final long serialVersionUID=1L;

        private String hash;
        private Integer index;

    }
}
