package com.jesper.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.jesper.model.User;

@Configuration
@WebFilter(urlPatterns = "/*", filterName = "Config")
@Order(value=2)
public class ConfigInterceptor implements Filter{

	private static final Set<String> ALLOWED_PATHS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("/user/skin-config.html",".*jpg","/favicon.ico","/img/.*","/font-awesome/.*","/js/.*","/css/.*","/main/excludefilter", "/user/login", "/user/logout", "/user/register")));

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
        boolean allowedPath = false;//ALLOWED_PATHS.contains(path);
        
        for (String patern : ALLOWED_PATHS) {
        	Pattern pattern = Pattern.compile(patern);
        	Matcher matcher = pattern.matcher(path);
        	if(matcher.matches()) {
        		allowedPath = true;
        	}
		}
        
        if (allowedPath) {
            //System.out.println("这里是不需要处理的url进入的方法");
            chain.doFilter(req, res);
        }
        else {
        	HttpSession session =  request.getSession();
            User user = (User) session.getAttribute("user");
            if(user==null) {
            	response.sendRedirect("/user/login");
            }else {
            	chain.doFilter(req, res);
            }
            System.out.println("这里是需要处理的url进入的方法"+path);
        }

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
