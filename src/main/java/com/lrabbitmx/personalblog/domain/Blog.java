package com.lrabbitmx.personalblog.domain;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Blog {

    private Integer id;
    private String blogTitle;
    private String contentHeader;
    private String contentUrl;
    private Integer isTop;
    private Long ctime;
    private Integer readCount;
    private Integer thumbs;
}
