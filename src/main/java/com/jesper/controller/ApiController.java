package com.jesper.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.jesper.mapper.UserMapper;
import com.jesper.model.User;
import com.jesper.util.JwtUtils;

@RestController
public class ApiController {
	@Autowired
    private UserMapper userMapper;
	
	
	
	@RequestMapping("/api")
	public String version() {
		
		return "1.0";
	}
	
	@RequestMapping("/api/getTicks")
	public String getTicks(@RequestHeader String token) {
		JSONObject jo = new JSONObject();
		jo.put("code", "0");
		jo.put("tk", token);
		return jo.toJSONString();
	}
	
	@RequestMapping("/api/login")
	public String login(@RequestParam String username,@RequestParam String passwd){
			User user = new User();
			user.setUserName(username);
			user.setPassword(passwd);
			User user1 = userMapper.selectByNameAndPwd(user);
			JSONObject jo = new JSONObject();
			JwtUtils  ju = new JwtUtils();
			if (user1 != null) {
	            jo.put("code", 0);
	            jo.put("t", ju.sign(user1));
	        } else {
	        	jo.put("code", -1);
	        }
		
		return jo.toJSONString();
	}
}
