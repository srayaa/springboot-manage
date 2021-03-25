package com.jesper.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Quan extends BaseObject{
    private Long id;

    private String batchnum;

    private Long cid;

    private Integer syr;

    private Byte status;
    
    
    private Date sendtime;

    private Date usetime;

    private Date expiretime;

    private Date created;

    private Date updated;

    private String updatedStr;
    
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date minSendtime;
    
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date maxSendtime;
    
    private String categoryName;
    private String syrstr;
}