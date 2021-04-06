package com.jesper.controller;

import com.jesper.mapper.FlowMapper;
import com.jesper.mapper.ItemCategoryMapper;
import com.jesper.mapper.ItemMapper;
import com.jesper.mapper.MemberMapper;
import com.jesper.mapper.QuanMapper;
import com.jesper.mapper.QuancatMapper;
import com.jesper.mapper.ReItemMapper;
import com.jesper.mapper.ShopMapper;
import com.jesper.model.Flow;
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
public class FlowController {


    @Autowired
    private FlowMapper flowMapper;
    @Autowired
    private ShopMapper shopMapper;
    
    Flow flow;

    public static final String ROOT = "src/main/resources/static/img/item/";

    MongoUtil mongoUtil = new MongoUtil();

    @RequestMapping("/coup/flowManage_{pageCurrent}_{pageSize}_{pageCount}")
    public String FlowManage(Flow flow, @PathVariable Integer pageCurrent,
                             @PathVariable Integer pageSize,
                             @PathVariable Integer pageCount,
                             Model model) {
        if (pageSize == 0) pageSize = 50;
        if (pageCurrent == 0) pageCurrent = 1;
        
        List<Shop> shoplist = shopMapper.selectAll();
        
        int rows = flowMapper.count(flow);
        if (pageCount == 0) pageCount = rows % pageSize == 0 ? (rows / pageSize) : (rows / pageSize) + 1;
        flow.setStart((pageCurrent - 1) * pageSize);
        flow.setEnd(pageSize);
        this.flow = flow;
        List<Flow> flowList = flowMapper.list(flow);
        /*for (Quancat qc : quancatList) {
        	String shops = qc.getValidshops().substring(1,qc.getValidshops().length()-1);
			qc.setValidshopstr(shopMapper.selectByIds(shops));
		}*/
        model.addAttribute("flowList", flowList);
        model.addAttribute("shoplist", shoplist);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String minsdtime = flow.getMinSendtime()==null?"":sdf.format(flow.getMinSendtime());
        String maxsdtime = flow.getMaxSendtime()==null?"":sdf.format(flow.getMaxSendtime());
        
        String pageHTML = PageUtil.getPageContent("flowManage_{pageCurrent}_{pageSize}_{pageCount}?shopid=" + flow.getShopid()+"&mname="+flow.getMname()+"&minSendtime" + minsdtime + "&maxSendtime" + maxsdtime, pageCurrent, pageSize, pageCount);
        model.addAttribute("pageHTML", pageHTML);
        return "flow/flowManage";
    }

    @GetMapping("/coup/flowEdit")
    public String flowEditGet(Model model, Flow flow) {
        if (flow!=null&&flow.getId()!=null&&flow.getId() != 0) {
        	Flow flowold = flowMapper.selectByPrimaryKey(flow.getId());
            model.addAttribute("flow", flowold);
        }
        return "flow/flowEdit";
    }

    @PostMapping("/coup/flowEdit")
    public String flowEditPost(Model model,  Flow flow) {
        //根据时间和随机数生成id
        Date date = new Date();
        if(flow.getCreated()==null) {
        	flow.setCreated(date);
        }
        if (flow.getId()!=null&&flow.getId() != 0) {
        	flowMapper.updateByPrimaryKey(flow);
        } else {
        	flowMapper.insert(flow);
        }
        return "redirect:flowManage_0_0_0";
    }
    
    
    @ResponseBody
    @PostMapping("/coup/flowEditDel")
    public ResObject<Object>  shopEditDel(Model model,Flow flow) {
    	flowMapper.deleteByPrimaryKey(flow.getId());
    	ResObject<Object> object = new ResObject<Object>(Constant.Code01, Constant.Msg01, null, null);
        return object;
    	
    }
    
    @RequestMapping("/coup/download1")
    public void postItemExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {

        //导出excel
        LinkedHashMap<String, String> fieldMap = new LinkedHashMap<String, String>();
        fieldMap.put("id", "id");
        fieldMap.put("tradecode", "交易号");
        fieldMap.put("ftype", "交易类型");
        fieldMap.put("quanid", "券id");
        fieldMap.put("mname", "消费者");
        fieldMap.put("shopName", "商店");
        fieldMap.put("created", "消费时间");
        String sheetName = "消费记录报表";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=XiaoFei.xls");//默认Excel名称
        response.flushBuffer();
        OutputStream fos = response.getOutputStream();
        Flow temflow = new Flow();
        BeanUtils.copyProperties(flow, temflow);
        temflow.setStart(0);
        temflow.setEnd(Integer.MAX_VALUE);
        List<Flow> flowList = flowMapper.list(temflow);
        try {
            ExcelUtil.listToExcel(flowList, fieldMap, sheetName, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
}
