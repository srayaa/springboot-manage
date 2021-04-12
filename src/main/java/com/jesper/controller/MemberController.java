package com.jesper.controller;

import com.jesper.mapper.ItemCategoryMapper;
import com.jesper.mapper.ItemMapper;
import com.jesper.mapper.MemberMapper;
import com.jesper.mapper.QuanMapper;
import com.jesper.mapper.QuancatMapper;
import com.jesper.mapper.ReItemMapper;
import com.jesper.mapper.ShopMapper;
import com.jesper.model.Item;
import com.jesper.model.ItemCategory;
import com.jesper.model.Member;
import com.jesper.model.Quan;
import com.jesper.model.Quancat;
import com.jesper.model.ReItem;
import com.jesper.model.ResObject;
import com.jesper.model.Shop;
import com.jesper.util.*;
import com.mongodb.gridfs.GridFSDBFile;
import com.sun.tools.classfile.StackMapTable_attribute.same_frame;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;


/**
 * 商品管理
 */
@Controller
public class MemberController {


    @Autowired
    private MemberMapper memberMapper;

    public static final String ROOT = "src/main/resources/static/img/item/";

    MongoUtil mongoUtil = new MongoUtil();

    private final ResourceLoader resourceLoader;

    
    
    @Autowired
    public MemberController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @RequestMapping("/coup/memberManage_{pageCurrent}_{pageSize}_{pageCount}")
    public String memberManage(Member member, @PathVariable Integer pageCurrent,
                             @PathVariable Integer pageSize,
                             @PathVariable Integer pageCount,
                             Model model) {
        if (pageSize == 0) pageSize = 50;
        if (pageCurrent == 0) pageCurrent = 1;

        int rows = memberMapper.count(member);
        if (pageCount == 0) pageCount = rows % pageSize == 0 ? (rows / pageSize) : (rows / pageSize) + 1;
        member.setStart((pageCurrent - 1) * pageSize);
        member.setEnd(pageSize);
        List<Member> memberList = memberMapper.list(member);
        model.addAttribute("memberList", memberList);
        
        String pageHTML = PageUtil.getPageContent("quancatManage_{pageCurrent}_{pageSize}_{pageCount}?pname=" + member.getPname(), pageCurrent, pageSize, pageCount);
        model.addAttribute("pageHTML", pageHTML);
        return "member/memberManage";
    }

    @GetMapping("/coup/info")
    public String memberDown() {
    	return "member/info";
    }
    
    @GetMapping("/coup/memberEdit")
    public String memberEditGet(Model model, Member member) {
    	List<Member> memberList = memberMapper.selectAll();
    	 model.addAttribute("memberList", memberList);
        if (member!=null&&member.getId()!=null&&member.getId() != 0) {
        	Member memberold = memberMapper.selectByPrimaryKey(member.getId());
            model.addAttribute("member", memberold);
        }
        return "member/memberEdit";
    }

    @PostMapping("/coup/memberEdit")
    public String memberEditPost(Model model,  Member member) {
        //根据时间和随机数生成id
        Date date = new Date();
        if(member.getCreated()==null) {
        	member.setCreated(date);
        }
        
        member.setUpdated(date);
        
        if (member.getId()!=null&&member.getId() != 0) {
        	memberMapper.updateByPrimaryKey(member);
        } else {
        	member.setPwd(member.getPcode());
        	memberMapper.insert(member);
        }
        return "redirect:memberManage_0_0_0";
    }
    
    
    @ResponseBody
    @PostMapping("/coup/memberEditDel")
    public ResObject<Object>  memberEditDel(Model model,Member member) {
    	memberMapper.deleteByPrimaryKey(member.getId());
    	ResObject<Object> object = new ResObject<Object>(Constant.Code01, Constant.Msg01, null, null);
        return object;
    	
    }
    
}
