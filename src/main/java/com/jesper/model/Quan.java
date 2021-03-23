package com.jesper.model;

import java.util.Date;

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
    
    private Date minSendtime;
    private Date maxSendtime;
    private String categoryName;
    private String syrstr;
}