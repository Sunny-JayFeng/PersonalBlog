package com.lrabbitmx.personalblog.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Book {

    private Integer id;
    private String bookName;
    private String authorName;
    private String version;
    private String recommendReasons;
    private String bookImage;
}
