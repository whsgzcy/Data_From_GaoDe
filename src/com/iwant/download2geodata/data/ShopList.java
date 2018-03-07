package com.iwant.download2geodata.data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 
 * @author: whsgzcy
 * @date: 2016-12-17 下午2:40:12  
 *    
 */
public class ShopList implements Serializable{
	
	private List<ShopInfo> mShopInfoList;

	public List<ShopInfo> getmShopInfoList() {
		return mShopInfoList;
	}

	public void setmShopInfoList(List<ShopInfo> mShopInfoList) {
		this.mShopInfoList = mShopInfoList;
	}

}
