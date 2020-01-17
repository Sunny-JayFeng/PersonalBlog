package test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lrabbitmx.personalblog.domain.Blog;
import com.lrabbitmx.personalblog.util.BufferMap;
import com.lrabbitmx.personalblog.util.Util;

import java.io.*;
import java.lang.reflect.Field;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Test {

    public static void main(String[] args) throws IllegalAccessException, IOException, NoSuchFieldException, URISyntaxException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("thumbs", 100);
        System.out.println(jsonObject.toJSONString());
    }

}
