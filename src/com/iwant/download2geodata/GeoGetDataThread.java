package com.iwant.download2geodata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.google.gson.Gson;
import com.iwant.download2geodata.data.POI;
import com.iwant.download2geodata.data.ShopInfo;
import com.iwant.download2geodata.data.ShopList;
import com.iwant.download2geodata.data.TemplateData;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * @Description: 获取高德所有数据
 * @author: whsgzcy
 * @date: 2016-12-17 下午1:04:31 首先是从高德copy一个链接，只要修改pagernum参数即可拼接成新的链接
 *        抓取方式较原始，但可维护性高
 */
public class GeoGetDataThread extends Thread {

	/******************************************************* 修改部分 *********************************************************************/
	// 共计多少页
	private int pages = 44;
	// 你想存储的Excel的名字 下次直接改一下就可以了
	private String excel_mingzi = "linyi_linmuxian.xls";
	// 长沙市 宠物店
	public String murl = "http://ditu.amap.com/service/poiInfo?query_type=TQUERY&pagesize="
			+ "20&pagenum=";
	// 从这开始 这两个双引号 一定得是 英文的 好的黏贴
	//public String nurl = "&qii=true&cluster_state=5&need_utd=true&utd_sceneid=1000&div=PC1000&addr_poi_merge=true&is_classify=true&zoom=15&city=320500&geoobj=120.716157%7C31.285445%7C120.737615%7C31.327098&keywords=%E7%90%86%E5%8F%91";
	public String nurl = "&qii=true&cluster_state=5&need_utd=true&utd_sceneid=1000&div=PC1000&addr_poi_merge=true&is_classify=true&zoom=10&city=371300&geoobj=118.271753%7C34.190622%7C118.958399%7C35.602995&keywords=%E8%B6%85%E5%B8%82";
	/******************************************************* 修改部分 *********************************************************************/	
	public int pagernum = 1;// 跳转到下一页参数
	private List<List<POI.DataBean.PoiListBean>> mPOIList = new ArrayList<List<POI.DataBean.PoiListBean>>();
	private List<POI.DataBean.PoiListBean> mList = new ArrayList<POI.DataBean.PoiListBean>();
	private HSSFWorkbook workbook = null;

	/**
	 * Ï
	 * 
	 * @Description: 根据pagernum跳转下一页
	 * @author: whsgzcy
	 * @date: 2016-12-17 下午1:17:08
	 * @param pagernum
	 */

	@Override
	public void run() {
		super.run();

		Row row;
		// 流
		FileOutputStream out = null;
		HSSFSheet sheet;

		/**
		 * 创建表格
		 */
		try {
			// 创建 表格
			String title[] = { "店铺名称", "地址", "座机1", "座机2", "手机1", "手机2" };
			// 文件的存储位置 我的电脑是mac的，所以是这个的文件位置，但，windows我已经默认设置为 桌面了 好的，现在开始抓取数据
			createExcel("/Users/super_yu/Desktop/" + excel_mingzi, "sheet1",
					title);
			workbook = new HSSFWorkbook(new FileInputStream(
					"/Users/super_yu/Desktop/" + excel_mingzi));
			sheet = workbook.getSheet("sheet1");
			// 获取表格的总行数
			int rowCount = sheet.getLastRowNum() + 1; // 需要加一
			// 获取表头的列数
			int columnCount = sheet.getRow(0).getLastCellNum();
			row = sheet.createRow(rowCount); // 最新要添加的一行
			HSSFRow titleRow = sheet.getRow(0);

			out = new FileOutputStream("/Users/super_yu/Desktop/"
					+ excel_mingzi);

		} catch (Exception e1) {
			return;
		}

		/**
		 * 按页写入数据
		 */
		int line = 0;
		for (int i = 0; i < pages; i++, pagernum++) {
			// 请求url
			String url = murl + pagernum + nurl;
			System.out.println(url);
			// 请求
			JSONObject jsonObject = new JSONObject();
			jsonObject = HttpRequestUtil.getJsonObject(url);
			// 转换成poi对象
			POI poi = new Gson().fromJson(jsonObject.toString(), POI.class);
			// 写入Excel
			if (poi.getData().getPoi_list() == null) {
				continue;
			}
			for (int m = 0; m < poi.getData().getPoi_list().size(); m++, line++) {
				Row w = sheet.createRow(line);
				for (int n = 0; n < 6; n++) {

					String name = poi.getData().getPoi_list().get(m).getName();
					Cell cellName = w.createCell(0);
					cellName.setCellValue(name);

					String address = poi.getData().getPoi_list().get(m)
							.getAddress();
					Cell cellAddress = w.createCell(1);
					cellAddress.setCellValue(address);

					String tel = poi.getData().getPoi_list().get(m).getTel();
					if (tel.equals("")) {
						Cell cellTel = w.createCell(2);
						cellTel.setCellValue(tel);
					}else{
						
						List<String> list = toBus(tel);
						Cell cellTel1 = w.createCell(2);
						cellTel1.setCellValue(list.get(0));
						
						Cell cellTel2 = w.createCell(3);
						cellTel2.setCellValue(list.get(1));
						
						Cell cellTel3 = w.createCell(4);
						cellTel3.setCellValue(list.get(2));
						
						Cell cellTel4 = w.createCell(5);
						cellTel4.setCellValue(list.get(3));
						
					}

				}

			}

		}
		// 写入Excel表格
		try {
			workbook.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void writeToExcel(String fileDir, String sheetName) {
		// 创建workbook
		File file = new File(fileDir);
		try {
			workbook = new HSSFWorkbook(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 流
		FileOutputStream out = null;
		HSSFSheet sheet = workbook.getSheet(sheetName);
		// 获取表格的总行数
		int rowCount = sheet.getLastRowNum() + 1; // 需要加一
		// 获取表头的列数
		int columnCount = sheet.getRow(0).getLastCellNum();
		try {
			Row row = sheet.createRow(rowCount); // 最新要添加的一行
			// 通过反射获得object的字段,对应表头插入
			// 获取该对象的class对象
			// Class class_ = object.getClass();
			// 获得表头行对象
			HSSFRow titleRow = sheet.getRow(0);
			if (titleRow != null) {
				for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) { // 遍历表头
					String title = titleRow.getCell(columnIndex).toString()
							.trim().toString().trim();
					Cell cell1 = row.createCell(0);
					cell1.setCellValue("111");
					Cell cell2 = row.createCell(1);
					cell2.setCellValue("222");
					Cell cell3 = row.createCell(2);
					cell3.setCellValue("333");
				}
			}
			out = new FileOutputStream(fileDir);
			workbook.write(out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 创建新excel.
	 * 
	 * @param fileDir
	 *            excel的路径
	 * @param sheetName
	 *            要创建的表格索引
	 * @param titleRow
	 *            excel的第一行即表格头
	 */
	public void createExcel(String fileDir, String sheetName, String titleRow[]) {
		// 创建workbook
		workbook = new HSSFWorkbook();
		// 添加Worksheet（不添加sheet时生成的xls文件打开时会报错)
		Sheet sheet1 = workbook.createSheet(sheetName);
		// 新建文件
		FileOutputStream out = null;
		try {
			// 添加表头
			Row row = workbook.getSheet(sheetName).createRow(0); // 创建第一行
//			for (int i = 0; i < titleRow.length; i++) {
//				Cell cell = row.createCell(i);
//				cell.setCellValue(titleRow[i]);
//			}
			out = new FileOutputStream(fileDir);
			workbook.write(out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @Description:读取本地文件---/Users/whsgzcy/Desktop/t.txt
	 * @author: whsgzcy
	 * @date: 2016-12-17 下午5:35:21
	 * @param filePath
	 *            void
	 * @throws
	 */
	public static void readTxtFile(String filePath) {
		try {
			String encoding = "UTF-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					System.out.println(lineTxt);
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}

	}

	public static List<String> toBus(String str1) {

		List<String> list = new ArrayList<String>();

		// String str1 = "0512-87772520;0512-87772620;18561242573;13771832367";
		// String str1 = "0512-87772520;0512-87772620;18561242573";
		// String str1 = "0512-87772520;0512-87772620";
		// String str1 = "18561242573;13771832367";

		// 拦截一个号码
		if (!str1.contains(";")) {
			if (str1.contains("-")) {
				list.add(str1);
				list.add("");
				list.add("");
				list.add("");
			} else {
				list.add("");
				list.add("");
				list.add(str1);
				list.add("");
			}
			System.out.println(list.toString());
			return list;
		}

		for (int i = 0; i < 4; i++) {

			if (str1.equals("")) {
				list.add("");
				continue;
			}

			if (!str1.contains(";")) {
				if (i == 3) {
					list.add(str1);
				} else {
					list.add("");
				}
				continue;
			}

			int m = str1.indexOf(";");
			String n = str1.substring(0, m);
			list.add(n);
			str1 = str1.substring(m + 1, str1.length());
		}

		System.out.println(list.toString());

		String g1 = "";
		String g2 = "";
		String m1 = "";
		String m2 = "";

		for (int m = 0; m < 4; m++) {
			if (list.get(m).contains("-")) {
				if (g1.equals("")) {
					g1 = list.get(m);
				}else if(g2.equals("")){
					g2 = list.get(m);
				}
			}
		}
		
		for (int m = 0; m < 4; m++) {
			if (!list.get(m).contains("-")) {
				if(!list.get(m).equals("")){
					if (m1.equals("")) {
						m1 = list.get(m);
					}else if(m2.equals("")){
						m2 = list.get(m);
					}
				}
			}
		}

		 List<String> list2 = new ArrayList<String>();
		 
		 list2.add(g1);
		 list2.add(g2);
		 list2.add(m1);
		 list2.add(m2);

		 System.out.println(list2.toString());
		 
		 return list2;
	}

	public static void main(String[] args) {
		GeoGetDataThread dg = new GeoGetDataThread();
		dg.start();
	}

}
