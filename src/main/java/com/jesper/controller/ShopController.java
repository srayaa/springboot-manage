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
public class ShopController {


    @Autowired
    private ShopMapper shopMapper;

    public static final String ROOT = "src/main/resources/static/img/item/";

    MongoUtil mongoUtil = new MongoUtil();

    @RequestMapping("/coup/shopManage_{pageCurrent}_{pageSize}_{pageCount}")
    public String shopManage(Shop shop, @PathVariable Integer pageCurrent,
                             @PathVariable Integer pageSize,
                             @PathVariable Integer pageCount,
                             Model model) {
        if (pageSize == 0) pageSize = 50;
        if (pageCurrent == 0) pageCurrent = 1;

        int rows = shopMapper.count(shop);
        if (pageCount == 0) pageCount = rows % pageSize == 0 ? (rows / pageSize) : (rows / pageSize) + 1;
        shop.setStart((pageCurrent - 1) * pageSize);
        shop.setEnd(pageSize);
        List<Shop> shopList = shopMapper.list(shop);
        /*for (Quancat qc : quancatList) {
        	String shops = qc.getValidshops().substring(1,qc.getValidshops().length()-1);
			qc.setValidshopstr(shopMapper.selectByIds(shops));
		}*/
        model.addAttribute("shopList", shopList);
        
        String pageHTML = PageUtil.getPageContent("quancatManage_{pageCurrent}_{pageSize}_{pageCount}?sname=" + shop.getSname(), pageCurrent, pageSize, pageCount);
        model.addAttribute("pageHTML", pageHTML);
        return "shop/shopManage";
    }

    @GetMapping("/coup/shopEdit")
    public String shopEditGet(Model model, Shop shop) {
        if (shop!=null&&shop.getId()!=null&&shop.getId() != 0) {
            Shop shopold = shopMapper.selectByPrimaryKey(shop.getId());
            model.addAttribute("shop", shopold);
        }
        return "shop/shopEdit";
    }

    @PostMapping("/coup/shopEdit")
    public String shopEditPost(Model model,  Shop shop) {
        //根据时间和随机数生成id
        Date date = new Date();
        if(shop.getCreated()==null) {
        	shop.setCreated(date);
        }
        shop.setUpdated(date);
        if (shop.getId()!=null&&shop.getId() != 0) {
        	shopMapper.updateByPrimaryKey(shop);
        } else {
        	shopMapper.insert(shop);
        }
        return "redirect:shopManage_0_0_0";
    }
    
    
    @ResponseBody
    @PostMapping("/coup/shopEditDel")
    public ResObject<Object>  shopEditDel(Model model,Shop shop) {
    	shopMapper.deleteByPrimaryKey(shop.getId());
    	ResObject<Object> object = new ResObject<Object>(Constant.Code01, Constant.Msg01, null, null);
        return object;
    	
    }
    
}
