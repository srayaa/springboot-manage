package com.jesper.controller;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.jesper.mapper.QuanMapper;
import com.jesper.mapper.UserMapper;
import com.jesper.model.Quan;
import com.jesper.model.User;
import com.jesper.util.JwtUtils;

@RestController
public class ApiController {
	@Autowired
    private UserMapper userMapper;
	
	@Autowired
	private QuanMapper quanMapper;
	
	
	
	@RequestMapping("/api")
	public String version() {
		
		return "1.0";
	}
	
	@RequestMapping("/api/hxTicks")
	public String hxTicks(@RequestHeader String token,@RequestParam String tickid,@RequestParam String shopid,@RequestParam String hxtime) {
		JSONObject jo = new JSONObject();
		//判断核销时间跟当前timestamp之间的差，大于1分钟的返回核销失败,
		//正常的，set tick state used ,
		//这里还要判断token中的用户ID是否等于传来的tickid对应的所有人id
		if(new Date().getTime()-Long.parseLong(hxtime)>60000) {
			//接口调用超时，不予核销
			jo.put("code", -1);
			return jo.toJSONString();
		}else{
			Quan quan = quanMapper.selectByPrimaryKey(Long.parseLong(tickid));
			JwtUtils  ju = new JwtUtils();
			if(ju.getUserId(token)!=quan.getSyr()) {
				//非本人券
				jo.put("code", -3);
				return jo.toJSONString();
			}
			if(quan.getStatus()==1) {
				quan.setHxsj(Integer.parseInt(shopid));
				quan.setUsetime(new Date(Long.parseLong(hxtime)));
				quanMapper.updateByPrimaryKey(quan);
				//这里要加上流水表记录，flow ftype:1核销
				
				jo.put("code", 0);
			}else {
				jo.put("code", -2);
				return jo.toJSONString();
			}
			
			
		}
		
		return jo.toJSONString();
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
