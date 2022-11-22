package com.example.demo.vo;

import com.diboot.core.binding.annotation.*;
import com.example.demo.entity.Palletinfo;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
* pallet信息 ListVO定义
* @author MyName
* @version 1.0
* @date 2022-11-22
 * Copyright © MyCompany
*/
@Getter @Setter @Accessors(chain = true)
public class PalletinfoListVO extends Palletinfo  {
    private static final long serialVersionUID = -358226038630704206L;

}