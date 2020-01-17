package com.lrabbitmx.personalblog.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Comment {

    private Integer id;
    private String userName;
    private String userEmail;
    private String content;
    private Long ctime;
}
