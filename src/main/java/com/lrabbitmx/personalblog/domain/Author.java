package com.lrabbitmx.personalblog.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Author {

    private Integer id;
    private String authorName;
    private String password;
    private Long ctime;
}
