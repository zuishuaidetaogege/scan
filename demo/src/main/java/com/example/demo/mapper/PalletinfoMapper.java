package com.example.demo.mapper;

import com.diboot.core.mapper.BaseCrudMapper;
import com.example.demo.entity.Palletinfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.io.Serializable;

/**
* pallet信息Mapper
* @author MyName
* @version 1.0
* @date 2022-11-22
 * Copyright © MyCompany
*/
@Mapper
public interface PalletinfoMapper extends BaseCrudMapper<Palletinfo> {

}

