package com.iwant.download2geodata.data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 
 * @author: whsgzcy
 * @date: 2016-12-17 下午2:07:25  
 *    
 */
public class ShopInfo implements Serializable{
	
	private String name;
	private String tel;
	private String address;
	private String longitude;//经度
	private String latitude;//纬度
	private List<TemplateData> templateData;// 图片
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public List<TemplateData> getTemplateData() {
		return templateData;
	}
	public void setTemplateData(List<TemplateData> templateData) {
		this.templateData = templateData;
	}
}
