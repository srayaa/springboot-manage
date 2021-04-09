package com.jesper.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
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

import com.auth0.jwt.interfaces.Claim;
import com.jesper.model.User;
import com.jesper.util.JwtUtils;

@Configuration
@WebFilter(urlPatterns = "/*", filterName = "Config")
@Order(value=2)
public class ConfigInterceptor implements Filter{

	JwtUtils jwtUtils = new JwtUtils();
	private static final Set<String> ALLOWED_PATHS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList(".*apk","/user/skin-config.html",".*jpg","/favicon.ico","/img/.*","/font-awesome/.*","/js/.*","/css/.*","/main/excludefilter", "/user/login", "/user/logout", "/user/register","/api","/api/login","/api/regist","/api/update")));

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
        	//不在白名单，判断是否以api开头
        	HttpSession session =  request.getSession();
        	//这里加入token校验
        	if(path.startsWith("/api/")) {
        		//以api开头的地址，判断token
        		String tk = request.getHeader("token");
            	if(tk!=null&&!"".equals(tk)) {
            		//这里需要redis判断是否存在未过期的token
            		//如果存在，则解码token获取用户ID
            		String username = jwtUtils.getUsername(tk);
            		Map<String,Claim> tkcl = jwtUtils.verify(tk, username);
            		if(tkcl==null) {
            			//写400，客户端需要先调登录
            			response.getWriter().write("401");
            		}else {
            			chain.doFilter(req, res);
            		}
            	}else {
            		response.getWriter().write("400");
            	}
        	}else {
        		User user = (User) session.getAttribute("user");
                if(user==null) {
                	response.sendRedirect("/user/login");
                }else {
                	chain.doFilter(req, res);
                }
        	}
            
            System.out.println("这里是需要处理的url进入的方法"+path);
        }

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
