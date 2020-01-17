package com.lrabbitmx.personalblog.filter;

import com.lrabbitmx.personalblog.domain.Author;
import com.lrabbitmx.personalblog.service.AuthorService;
import com.lrabbitmx.personalblog.util.Util;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class LoginFilter implements Filter {

    private AuthorService authorService;
    private Util util;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:config/applicationContext.xml");
        authorService = (AuthorService) applicationContext.getBean("authorService");
        util = (Util) applicationContext.getBean("util");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        Author author = null;
        String requestPath = request.getServletPath();
        if(requestPath.endsWith(".css") || requestPath.endsWith(".js")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return ;
        }
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length != 0) {
            for(Cookie cookie : cookies) {
                if("id".equals(cookie.getName())) author = authorService.getTheAuthor(Integer.parseInt(util.decodeCookieValue(cookie)));
                if(author != null) break;
            }
        }
        if( ("/author".equals(requestPath) || "/author/".equals(requestPath)) && author != null) {
            response.sendRedirect("/author/author.html?authorId=" + author.getId());
            return ;
        }else if(author == null && !("/author/login.html".equals(requestPath)) && !("/author".equals(requestPath))) {
            response.sendRedirect("/author/login.html");
            return ;
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
