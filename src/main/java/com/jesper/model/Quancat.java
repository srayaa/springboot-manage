package com.jesper.model;

import java.util.Date;

import lombok.Data;

@Data
public class Quancat extends BaseObject{
    private Long id;
    private String qname;
    private String qdesc;
    private String image;
    private String qprice;
    private Integer qtimes;
    private Float qcoup;
    private Integer lastnum;
    private Integer holdnum;
    private String validshops;
    private Byte status;
    private Date created;
    private Date updated;
    private String validshopstr;

    
}