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
public class QuancatController {


    @Autowired
    private QuancatMapper quancatMapper;
    @Autowired
    private ShopMapper shopMapper;

    /*@Autowired
    private ReItemMapper reItemMapper;
	*/
    public static final String ROOT = "src/main/resources/static/img/item/";

    MongoUtil mongoUtil = new MongoUtil();

    private final ResourceLoader resourceLoader;

    
    
    @Autowired
    public QuancatController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @RequestMapping("/coup/quancatManage_{pageCurrent}_{pageSize}_{pageCount}")
    public String quancatManage(Quancat quancat, @PathVariable Integer pageCurrent,
                             @PathVariable Integer pageSize,
                             @PathVariable Integer pageCount,
                             Model model) {
        if (pageSize == 0) pageSize = 50;
        if (pageCurrent == 0) pageCurrent = 1;

        int rows = quancatMapper.count(quancat);
        if (pageCount == 0) pageCount = rows % pageSize == 0 ? (rows / pageSize) : (rows / pageSize) + 1;
        quancat.setStart((pageCurrent - 1) * pageSize);
        quancat.setEnd(pageSize);
        List<Quancat> quancatList = quancatMapper.list(quancat);
        for (Quancat qc : quancatList) {
        	String shops = qc.getValidshops().substring(1,qc.getValidshops().length()-1);
			qc.setValidshopstr(shopMapper.selectByIds(shops));
		}
        model.addAttribute("quancatList", quancatList);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        String pageHTML = PageUtil.getPageContent("quancatManage_{pageCurrent}_{pageSize}_{pageCount}?qname=" + quancat.getQname(), pageCurrent, pageSize, pageCount);
        model.addAttribute("pageHTML", pageHTML);
        return "quancat/quancatManage";
    }

    @GetMapping("/coup/quancatEdit")
    public String quancatEditGet(Model model, Quancat quancat) {
    	List<Shop> shopList = shopMapper.selectAll();
    	 model.addAttribute("shopList", shopList);
        if (quancat!=null&&quancat.getId()!=null&&quancat.getId() != 0) {
            Quancat quancatold = quancatMapper.selectByPrimaryKey(quancat.getId());
            model.addAttribute("quancat", quancatold);
        }
        return "quancat/quancatEdit";
    }

    @PostMapping("/coup/quancatEdit")
    public String quancatEditPost(Model model,  Quancat quancat,@RequestParam String[] validshoparr) {
        //根据时间和随机数生成id
        Date date = new Date();
        quancat.setCreated(date);
        quancat.setUpdated(date);
        quancat.setLastnum(999999);
        String validshop = ",";
        validshop = validshop+StringUtils.join(validshoparr, ",")+",";
        quancat.setValidshops(validshop);
        if (quancat.getId()!=null&&quancat.getId() != 0) {
        	quancatMapper.updateByPrimaryKey(quancat);
        } else {
        	quancatMapper.insert(quancat);
        }
        return "redirect:quancatManage_0_0_0";
    }
    
    
    @ResponseBody
    @PostMapping("/coup/quancatEditDel")
    public ResObject<Object>  quanEditDel(Model model,Quancat quancat) {
    	quancatMapper.deleteByPrimaryKey(quancat.getId());
    	ResObject<Object> object = new ResObject<Object>(Constant.Code01, Constant.Msg01, null, null);
        return object;
    	
    }
    
}
