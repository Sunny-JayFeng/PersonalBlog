package com.lrabbitmx.personalblog.controller;

import com.lrabbitmx.personalblog.util.BufferMap;
import com.lrabbitmx.personalblog.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Controller
public class BaseController {

    @Autowired
    protected BufferMap bufferMap;
    @Autowired
    protected Util util;

    public void setEncoding(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=utf-8");
    }

    public void responseMessageToBrowser(HttpServletResponse response, String responseMessage) {
        try {
            response.getWriter().write(responseMessage);
            response.getWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
