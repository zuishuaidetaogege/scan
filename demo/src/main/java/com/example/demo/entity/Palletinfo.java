package com.example.demo.entity;

import java.util.Date;
import java.lang.Double;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;
import java.util.List;
import com.baomidou.mybatisplus.annotation.*;
import com.example.demo.entity.BaseCustomEntity;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.util.D;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
* pallet信息 Entity定义
* @author MyName
* @version 1.0
* @date 2022-11-22
* Copyright © MyCompany
*/
@Getter @Setter @Accessors(chain = true)
@TableName("palletinfo")
public class Palletinfo extends BaseCustomEntity {
    private static final long serialVersionUID = -5718497060279607283L;

    /**
    * PALLETID 
    */
    @Length(max=255, message="PALLETID长度应小于255")
    @TableField()
    private String palletid;

    /**
    * BOXID 
    */
    @Length(max=40, message="BOXID长度应小于40")
    @TableField()
    private String boxid;

    /**
    * PANELID 
    */
    @Length(max=40, message="PANELID长度应小于40")
    @TableField()
    private String panelid;

    /**
    * PRODUCTID 
    */
    @Length(max=40, message="PRODUCTID长度应小于40")
    @TableField()
    private String productid;

    /**
    * STEPID 
    */
    @Length(max=40, message="STEPID长度应小于40")
    @TableField()
    private String stepid;

    /**
    * PRODTYPE 
    */
    @Length(max=40, message="PRODTYPE长度应小于40")
    @TableField()
    private String prodtype;

    /**
    * STATUS 
    */
    @Length(max=40, message="STATUS长度应小于40")
    @TableField()
    private String status;

    /**
    * OPTIONCODE 
    */
    @Length(max=255, message="OPTIONCODE长度应小于255")
    @TableField()
    private String optioncode;


} 
