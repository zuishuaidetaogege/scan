package com.example.demo.dto;

import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.example.demo.entity.Palletinfo;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
* pallet信息 DTO定义
* @author MyName
* @version 1.0
* @date 2022-11-22
 * Copyright © MyCompany
*/
@Getter @Setter @Accessors(chain = true)
public class PalletinfoDTO extends Palletinfo  {
    private static final long serialVersionUID = -4659542212338722436L;

}