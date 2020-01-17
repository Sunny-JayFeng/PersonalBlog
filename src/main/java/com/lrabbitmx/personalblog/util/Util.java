package com.lrabbitmx.personalblog.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lrabbitmx.personalblog.domain.Tag;
import com.lrabbitmx.personalblog.service.BlogService;
import com.lrabbitmx.personalblog.service.CommentService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Component
public class Util {

    @Autowired
    private BufferMap bufferMap;
    @Autowired
    private CommentService commentService;
    @Autowired
    private BlogService blogService;

    public Object createAnObject(Class clazz, HttpServletRequest request) {
        Constructor constructor = null;
        try {
            constructor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return createAnObject(constructor, request);
    }

    public Object createAnObject(Constructor constructor, HttpServletRequest request) {
        Object object = null;
        try {
            object = constructor.newInstance();
            setFieldValue(object, request);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return object;
    }

    private void setFieldValue(Object object, HttpServletRequest request) {
        Field[] fields = object.getClass().getDeclaredFields();
        for(Field field : fields) {
            String parameter = request.getParameter(field.getName());
            try {
                setValue(object, field, parameter);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void setValue(Object object, Field field, String parameter) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(object, null);
        String fieldName = field.getName();
        if("ctime".equals(fieldName)) {
            field.set(object, System.currentTimeMillis());
            return;
        }else if("readCount".equals(fieldName)) {
            field.set(object, 0);
            return;
        }else if("thumbs".equals(fieldName)) {
            field.set(object, 0);
            return;
        }
        if(parameter == null || "".equals(parameter)) return;
        if(field.getType() == String.class) field.set(object, parameter);
        else if(field.getType() == Long.class) field.set(object, Long.parseLong(parameter.trim()));
        else if(field.getType() == Integer.class) field.set(object, Integer.parseInt(parameter.trim()));
        else if(field.getType() == Short.class) field.set(object, Short.parseShort(parameter.trim()));
        else if(field.getType() == Byte.class) field.set(object, Byte.parseByte(parameter.trim()));
        else if(field.getType() == Double.class) field.set(object, Double.parseDouble(parameter.trim()));
        else if(field.getType() == Float.class) field.set(object, Float.parseFloat(parameter.trim()));
        else if(field.getType() == Boolean.class) field.set(object, Boolean.parseBoolean(parameter.trim()));
    }

    public Object updateObject(Object oldObject, Object newObject){
        Class clazz = newObject.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields) {
            field.setAccessible(true);
            try {
                if(field.get(newObject) != null) {
                    field.set(oldObject, field.get(newObject));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return oldObject;
    }

    // 对cookie的值进行加密
    public String encodeCookieValue(String arg) throws UnsupportedEncodingException, NoSuchFieldException, IllegalAccessException {
        StringBuilder str = new StringBuilder(arg);
        str.append("%");
        str.append(str.length());
        str.append("abc");
        while(str.length() < 64) {
            str.append((char)(new Random().nextInt(128) + 64));
        }
        return str.toString();
    }

    // 对cookie的值进行解密
    public String decodeCookieValue(Cookie cookie) {
        String value = cookie.getValue();
        int per = value.indexOf("%");
        return value.substring(0, per);
    }

    public void setCookie(String result, String id, HttpServletResponse response) {
        if(result.contains("SUCCESS")) {
            try {
                id = encodeCookieValue(id);
                Cookie idCookie = new Cookie("id", encodeCookieValue(id));
                idCookie.setMaxAge(3600 * 24 * 7);
                idCookie.setPath("/author");
                response.addCookie(idCookie);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return ;
        }
        return ;
    }

    // 删除一篇文章
    public String deleteBlog(int blogId) {
        bufferMap.removeOneBufferBlog(blogId);
        List<Integer> commentsIdList = commentService.getTheBlogCommentsId(blogId);
        commentService.deleteComments(commentsIdList); // 删除文章，那这篇文章对应的所有评论也都得删除
        bufferMap.removeComments(commentsIdList); // 删除这篇文章的缓存
        return blogService.deleteBlog(blogId);
    }

    // map去掉key转list
    public List mapToList(Map map) {
        List<Object> list = new LinkedList<>();
        Iterator<Object> it = map.keySet().iterator();
        while(it.hasNext()) {
            list.add(map.get(it.next()));
        }
        return list;
    }

    // 外界调用上传文件
    public String fileUpload(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Iterator<String> it = multipartRequest.getFileNames();
        JSONObject fileJsonObject = new JSONObject();
        while(it.hasNext()) {
            String originFileName = it.next();
            String realFileName = "";
            MultipartFile multipartFile = multipartRequest.getFile(originFileName);
            if(multipartFile == null) continue;
            String fileSuffix = originFileName.substring(originFileName.lastIndexOf(".") + 1);
            if("html".equalsIgnoreCase(fileSuffix)) realFileName = fileUpload("E:/personalblogfileupload/html/", multipartFile);
            else if("css".equalsIgnoreCase(fileSuffix)) realFileName = fileUpload("E:/personalblogfileupload/css/", multipartFile);
            else if("img".equalsIgnoreCase(fileSuffix)) realFileName = fileUpload("E:/personalblogfileupload/img/", multipartFile);
            else return null;
            fileJsonObject.put(fileSuffix, realFileName);
        }
        if(fileJsonObject.size() == 0) return "no new file";
        return fileJsonObject.toJSONString();
    }

    // 处理上传过来的html文件，套进模板
    private String htmlFileText(MultipartFile multipartFile) {
        String htmlTemplate;
        String originFileText = null;
        try {
            originFileText = new String(multipartFile.getBytes(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 判断是否带css文件，套对应模板
        int cssLinkStartIndex = originFileText.indexOf("<!-- css-link-start -->");
        int cssLinkEndIndex = originFileText.indexOf("<!-- css-link-end -->");
        if(cssLinkStartIndex != -1 && cssLinkEndIndex != -1) htmlTemplate = bufferMap.blogTemplateWithCssFile;
        else htmlTemplate = bufferMap.blogTemplateWithoutCssFile;

        // 判断是否带style标签。
        int styleTagStartIndex = originFileText.indexOf("<style>");
        int styleTagEndIndex = originFileText.indexOf("</style>");

        // 如果带style标签，则把模板里的<$style$>替换成style标签及标签里的内容，否则替换成空串。
        String styleText = "";
        if(styleTagStartIndex != -1 && styleTagEndIndex != -1) styleText = originFileText.substring(styleTagStartIndex, styleTagEndIndex + "</style>".length()).trim(); // 取得style标签及内容
        htmlTemplate = htmlTemplate.replaceAll("<\\$style\\$>", styleText);

        // 获取博客内容
        int blogStartIndex = originFileText.indexOf("<!-- blog-start -->");
        int blogEndIndex = originFileText.indexOf("<!-- blog-end -->");

        String blogText = "";
        if(blogStartIndex != -1 && blogEndIndex != -1) blogText = originFileText.substring(blogStartIndex + "<!-- blog-start -->".length(), blogEndIndex).trim();
        htmlTemplate = htmlTemplate.replaceAll("<\\$blog\\$>", blogText);

        return htmlTemplate;
    }

    // 底层上传文件
    private String fileUpload(String filePath, MultipartFile multipartFile) {
        String realFileName = filePath + UUID.randomUUID().toString();
        File newFile = new File(realFileName);
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            newFile.createNewFile();
            fileOutputStream = new FileOutputStream(newFile);
            // 如果是html文件，则要套模板
            if("E:/personalblogfileupload/html/".equals(filePath)) inputStream = IOUtils.toInputStream(htmlFileText(multipartFile), "UTF-8");
            else inputStream = multipartFile.getInputStream(); // 不是html文件，直接获取文件流就好了
            IOUtils.copy(inputStream, fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return realFileName;
    }

    // 删除文件
    public void deleteFile(String content, String name) {
        Object jsonObject = JSON.parseObject(content).get(name);
        if(jsonObject == null) return ;
        String fileRealName = jsonObject.toString();
        File file = new File(fileRealName);
        file.delete();
    }

    // 外界调用返回文件流
    public void responseFileStream(HttpServletResponse response, String content, String name, String noFileMessage) {
        Object jsonObject = JSON.parseObject(content).get(name);
        if(jsonObject == null) {
            try {
                response.getWriter().write(noFileMessage);
                response.getWriter().flush();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String fileRealName = jsonObject.toString();
        responseFileStream(fileRealName, response);
    }

    // 返回文件流
    private void responseFileStream(String fileRealName, HttpServletResponse response) {
        FileInputStream fileInputStream = null;
        OutputStream outputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(fileRealName));
            outputStream = response.getOutputStream();
            IOUtils.copy(fileInputStream, outputStream);
            response.getOutputStream().flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 给定标签字符串，返回其中所有标签的标签id
    public List<Integer> getTagsId(String tags) {
        List<Integer> tagList = new LinkedList<>();
        String[] allTags = tags.split(",");
        List<String> tempList = new LinkedList<>();
        for(String tag : allTags) {
            tempList.add(tag);
        }
        for(Tag tag : bufferMap.tagsList) {
            if(tempList.contains(tag.getTagName())) tagList.add(tag.getId());
        }
        return tagList;
    }

}
