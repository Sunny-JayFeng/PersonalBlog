package com.lrabbitmx.personalblog.domain;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Tag {

    private Integer id;
    private String tagName;
    private Long ctime;
}
