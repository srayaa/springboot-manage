package com.jesper.controller;

import com.jesper.mapper.ItemCategoryMapper;
import com.jesper.mapper.ItemMapper;
import com.jesper.mapper.MemberMapper;
import com.jesper.mapper.QuanMapper;
import com.jesper.mapper.QuancatMapper;
import com.jesper.mapper.ReItemMapper;
import com.jesper.model.Item;
import com.jesper.model.ItemCategory;
import com.jesper.model.Member;
import com.jesper.model.Quan;
import com.jesper.model.Quancat;
import com.jesper.model.ReItem;
import com.jesper.model.ResObject;
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
public class QuanController {

    @Autowired
    private QuanMapper quanMapper;

    @Autowired
    private QuancatMapper quancatMapper;
    @Autowired
    private MemberMapper memberMapper;

    /*@Autowired
    private ReItemMapper reItemMapper;
	*/
    public static final String ROOT = "src/main/resources/static/img/item/";

    MongoUtil mongoUtil = new MongoUtil();

    private final ResourceLoader resourceLoader;

    @Autowired
    public QuanController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    List<Quan> quanList;
    

    File getFile = null;

    @RequestMapping("/coup/quanManage_{pageCurrent}_{pageSize}_{pageCount}")
    public String quanManage(Quan quan, @PathVariable Integer pageCurrent,
                             @PathVariable Integer pageSize,
                             @PathVariable Integer pageCount,
                             Model model) {
        if (pageSize == 0) pageSize = 50;
        if (pageCurrent == 0) pageCurrent = 1;

        int rows = quanMapper.count(quan);
        if (pageCount == 0) pageCount = rows % pageSize == 0 ? (rows / pageSize) : (rows / pageSize) + 1;
        quan.setStart((pageCurrent - 1) * pageSize);
        quan.setEnd(pageSize);
        quanList = quanMapper.list(quan);
        for (Quan i : quanList) {
            i.setUpdatedStr(DateUtil.getDateStr(i.getUpdated()));
        }
        Quancat quancat = new Quancat();
        quancat.setStart(0);
        quancat.setEnd(Integer.MAX_VALUE);
        List<Quancat> quancatList = quancatMapper.list(quancat);
        /*Integer minPrice = quan.getMinPrice();
        Integer maxPrice = quan.getMaxPrice();*/
        model.addAttribute("quancatList", quancatList);
        model.addAttribute("quanList", quanList);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String minsdtime = quan.getMinSendtime()==null?"":sdf.format(quan.getMinSendtime());
        String maxsdtime = quan.getMaxSendtime()==null?"":sdf.format(quan.getMaxSendtime());
        
        String pageHTML = PageUtil.getPageContent("quanManage_{pageCurrent}_{pageSize}_{pageCount}?syr=" + quan.getSyr() + "&cid=" + quan.getCid() + "&minSendtime" + minsdtime + "&maxSendtime" + maxsdtime, pageCurrent, pageSize, pageCount);
        model.addAttribute("pageHTML", pageHTML);
        model.addAttribute("quan", quan);
        return "quan/quanManage";
    }

    @GetMapping("/coup/quanEdit")
    public String quanEditGet(Model model, Quan quan) {
        Quancat quancat = new Quancat();
        quancat.setStart(0);
        quancat.setEnd(Integer.MAX_VALUE);
        
        List<Quancat> quancatList = quancatMapper.list(quancat);
        model.addAttribute("quancatList", quancatList);
        if (quan!=null&&quan.getId()!=null&&quan.getId() != 0) {
            Quan quanold = quanMapper.selectByPrimaryKey(quan.getId());
            /*String id = String.valueOf(quan.getId());
            GridFSDBFile fileById = mongoUtil.getFileById(id);
            if (fileById != null) {
                StringBuilder sb = new StringBuilder(ROOT);
                imageName = fileById.getFilename();
                sb.append(imageName);
                try {
                    getFile = new File(sb.toString());
                    fileById.writeTo(getFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                item1.setImage(imageName);
            }*/
            model.addAttribute("quan", quanold);
        }
        return "quan/quanEdit";
    }

    @PostMapping("/coup/quanEdit")
    public String quanEditPost(Model model,  Quan quan) {
        //根据时间和随机数生成id
        Date date = new Date();
        quan.setCreated(date);
        quan.setUpdated(date);
        quan.setSendtime(date);
        

        if (quan.getId()!=null&&quan.getId() != 0) {
        	quanMapper.updateByPrimaryKey(quan);
        } else {
        	quanMapper.insert(quan);
        }
        return "redirect:quanManage_0_0_0";
    }
    
    @GetMapping("/coup/quanEditBatch")
    public String quanEditBatchGet(Model model, Quan quan) {
    	 Quancat quancat = new Quancat();
         quancat.setStart(0);
         quancat.setEnd(Integer.MAX_VALUE);
         
         List<Quancat> quancatList = quancatMapper.list(quancat);
         model.addAttribute("quancatList", quancatList);
    	return "quan/quanEditBatch";
    }
    @PostMapping("/coup/quanEditBatch")
    public String quanEditBatchPost(Model model,Quan quan,@RequestParam int ffsl) {
    	Date date = new Date();
        quan.setCreated(date);
        quan.setUpdated(date);
        quan.setSendtime(date);
        List<Member> mems =  memberMapper.selectAll();
    	switch (quan.getSyr()) {
		case 0:
			//所有人
			List<Quan> qs = new ArrayList<Quan>();
			for (Member member : mems) {
				for(int i=0;i<ffsl;i++) {
					Quan qone = new Quan();
					BeanUtils.copyProperties(quan, qone);
					qone.setSyr(member.getId().intValue());
					qs.add(qone);
					//quanMapper.insert(qone);
				}
			}
			quanMapper.insertBatch(qs);
			break;
		case 1:
			//民警：pos大于特定值，民警为10，大队为20，支队为30，非民警为0
			
			break;
		case 2:
			//非民警：pos等于特定值，民警为10，大队为20，支队为30，非民警为0
			
			break;
		default:
			break;
		}
    	return "redirect:quanManage_0_0_0";
    }
    
    @ResponseBody
    @PostMapping("/coup/quanEditDel")
    public ResObject<Object>  quanEditDel(Model model,Quan quan) {
    	quanMapper.deleteByPrimaryKey(quan.getId());
    	ResObject<Object> object = new ResObject<Object>(Constant.Code01, Constant.Msg01, null, null);
        return object;
    	
    }
    
    /*@RequestMapping("/user/download1")
    public void postItemExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {

        //导出excel
        LinkedHashMap<String, String> fieldMap = new LinkedHashMap<String, String>();
        fieldMap.put("id", "商品id");
        fieldMap.put("title", "商品标题");
        fieldMap.put("sellPoint", "商品卖点");
        fieldMap.put("price", "商品价格");
        fieldMap.put("num", "库存数量");
        fieldMap.put("image", "商品图片");
        fieldMap.put("cid", "所属类目，叶子类目");
        fieldMap.put("status", "商品状态，1-正常，2-下架，3-删除");
        fieldMap.put("created", "创建时间");
        fieldMap.put("updated", "更新时间");
        String sheetName = "商品管理报表";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=ItemManage.xls");//默认Excel名称
        response.flushBuffer();
        OutputStream fos = response.getOutputStream();
        try {
            ExcelUtil.listToExcel(itemList, fieldMap, sheetName, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    String imageName = null;

    

    

    @ResponseBody
    @PostMapping("/user/itemEditState")
    public ResObject<Object> itemEditState(Item item1) {
        Item item = itemMapper.findById(item1);
        ReItem reItem = new ReItem();
        reItem.setId(item.getId());
        reItem.setBarcode(item.getBarcode());
        reItem.setCid(item.getCid());
        reItem.setImage(item.getImage());
        reItem.setPrice(item.getPrice());
        reItem.setNum(item.getNum());
        reItem.setSellPoint(item.getSellPoint());
        reItem.setStatus(item.getStatus());
        reItem.setTitle(item.getTitle());
        reItem.setRecovered(new Date());
        reItemMapper.insert(reItem);
        itemMapper.delete(item1);
        ResObject<Object> object = new ResObject<Object>(Constant.Code01, Constant.Msg01, null, null);
        return object;
    }*/
}
