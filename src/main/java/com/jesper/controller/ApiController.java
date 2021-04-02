package com.jesper.controller;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jesper.mapper.FlowMapper;
import com.jesper.mapper.MemberMapper;
import com.jesper.mapper.QuanMapper;
import com.jesper.mapper.ShopMapper;
import com.jesper.mapper.UserMapper;
import com.jesper.model.Flow;
import com.jesper.model.Member;
import com.jesper.model.Quan;
import com.jesper.model.Shop;
import com.jesper.model.User;
import com.jesper.redis.RedisService;
import com.jesper.redis.TradeKey;
import com.jesper.util.JwtUtils;
@CrossOrigin
@RestController
public class ApiController {
	@Autowired
    private UserMapper userMapper;
	@Autowired
	private FlowMapper flowMapper;
	@Autowired
	private ShopMapper shopMapper;
	@Autowired
	private MemberMapper memberMapper;
	
	@Autowired
    private RedisService redisService;
	
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
			if(quan==null) {
				jo.put("code", -4);
				return jo.toJSONString();
			}
			JwtUtils  ju = new JwtUtils();
			if(ju.getUserId(token)!=quan.getSyr()) {
				//非本人券
				jo.put("code", -3);
				return jo.toJSONString();
			}
			if(quan.getStatus()==1) {
				/*Long tn = redisService.get(TradeKey.trade, "TN", Long.class);
				if(tn==null) {
					tn = 0l;
				}else {
					tn = tn+1;
				}
				redisService.set(TradeKey.trade, "TN", tn);*/
				Shop shop = shopMapper.selectByPrimaryKey(Long.parseLong(shopid));
				if(shop==null) {
					jo.put("code", -5);
					return jo.toJSONString();
				}
				String tradenum = new Date().getTime()+"";
				String endfix = "U"+Integer.parseInt(shopid)+"M"+quan.getSyr()+"N"+tickid;
				while(endfix.length()<17) {
					endfix = endfix+"0";
				}
				tradenum = "T"+endfix +tradenum;
				quan.setHxsj(Integer.parseInt(shopid));
				quan.setUsetime(new Date(Long.parseLong(hxtime)));
				quan.setStatus(Quan.unbilled);//3-已核销未结算
				quan.setTn(tradenum);
				quanMapper.updateByPrimaryKey(quan);
				//这里要加上流水表记录，flow ftype:1核销
				Flow flow = new Flow();
				flow.setTradecode(tradenum);
				flow.setFtype((byte) 1);
				flow.setQuanid(quan.getId());
				flow.setMemberid(quan.getSyr().longValue());
				flow.setCreated(new Date());
				flow.setShopid(Long.parseLong(shopid));
				flowMapper.insert(flow);
				jo.put("code", 0);
				jo.put("tn", tradenum);
				jo.put("sp",shop.getSname());
			}else {
				jo.put("code", -2);
				return jo.toJSONString();
			}
			
			
		}
		
		return jo.toJSONString();
	}
	
	@RequestMapping("/api/getTicks")
	public String getTicks(@RequestHeader String token) {
		//取得人员ID
		JwtUtils  ju = new JwtUtils();
		int mid = ju.getUserId(token);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Quan> qus =  quanMapper.selectByMid(mid);
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		for (Quan quan : qus) {
			if(quan.getExpiretime().before(new Date())) {
				String tradenum = new Date().getTime()+"";
				String endfix = "U"+"0"+"M"+mid+"N"+quan.getId();
				while(endfix.length()<17) {
					endfix = endfix+"0";
				}
				tradenum = "D"+endfix +tradenum;
				quan.setStatus((byte) 3);
				quan.setTn(tradenum);
				quanMapper.updateByPrimaryKey(quan);
				Flow flow = new Flow();
				flow.setCreated(new Date());
				flow.setFtype((byte) 3);
				flow.setMemberid((long) mid);
				flow.setQuanid(quan.getId());
				
				flow.setTradecode(tradenum);
				flowMapper.insert(flow);
			}
			JSONObject j = new JSONObject();
			j.put("id", quan.getId());
			j.put("title", quan.getCategoryName());
			j.put("termStart", sdf.format(quan.getStarttime()));
			j.put("termEnd", sdf.format(quan.getExpiretime()));
			j.put("ticket", "1次");
			j.put("criteria", "无限制条件");
			j.put("state",quan.getStatus());
			j.put("validshop", quan.getValidshops());
			j.put("sysj", quan.getUsetime()==null?"":sdf.format(quan.getUsetime()));
			j.put("tn", quan.getTn());
			j.put("sp", quan.getSname());
			ja.add(j);
		}
		jo.put("qs",ja);
		jo.put("code", "0");
		
		return jo.toJSONString();
	}
	
	@RequestMapping("/api/login")
	public String login(@RequestParam String username,@RequestParam String passwd){
		Member member = new Member();
		member.setPname(username);
		member.setPwd(passwd);
		Member member1 = memberMapper.selectByNameAndPwd(member);
			
		JSONObject jo = new JSONObject();
		JwtUtils  ju = new JwtUtils();
		if (member1 != null) {
			if(member1.getState()==0) {
				jo.put("code", -2);
				jo.put("i", member1.getId());
			}else {
				jo.put("code", 0);
				jo.put("i", member1.getId());
				jo.put("t", ju.sign(member1));
			}
	    } else {
	        jo.put("code", -1);
	    }
		
		return jo.toJSONString();
	}
	
	@RequestMapping("/api/regist")
	public String regist(@RequestParam String i,@RequestParam String code,@RequestParam String pname,@RequestParam String passwd) {
		JSONObject jo = new JSONObject();
		Member member = memberMapper.selectByPrimaryKey(Long.parseLong(i));
		if(member.getState()!=0) {
			jo.put("code", -1);
			//无需激活
		}else {
			if(member.getPcode().equals(code)&&member.getRealname().equals(pname)) {
				//警号，姓名符合，激活
				member.setPwd(passwd);
				member.setState((byte) 1);
				member.setUpdated(new Date());
				memberMapper.updateByPrimaryKey(member);
				jo.put("code", 1);
			}else {
				//不符合
				jo.put("code", -2);
			}
		}
		return jo.toJSONString();
	}
}
