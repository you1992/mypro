package cn.itcast.erp.biz.impl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import cn.itcast.erp.biz.ISupplierBiz;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Supplier;
/**
 * 供应商业务逻辑类
 * @author Administrator
 *
 */
public class SupplierBiz extends BaseBiz<Supplier> implements ISupplierBiz {

	private ISupplierDao supplierDao;
	
	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
		super.setBaseDao(this.supplierDao);
	}

	@Override
	public void export(Supplier t1, OutputStream os) throws Exception {
		
		Workbook wk = null;
		try {
			List<Supplier> list = supplierDao.getList(t1, null, null);
			//创建工作簿
			wk = new HSSFWorkbook();
			//工作表名称
			String sheetName = "";
			if(Supplier.TYPE_SUPPLIER.equals(t1.getType())){
				sheetName = "供应商";
			}
			if(Supplier.TYPE_CUSTOMER.equals(t1.getType())){
				sheetName = "客户";
			}
			//创建工作表
			Sheet sht = wk.createSheet(sheetName);
			//创建行
			Row row = sht.createRow(0);//行下标从0开始
			//表头
			String[] headerNames = {"名称","联系地址","联系人","联系电话","邮件地址"};
			//宽度
			int[] widths = {4000,8000,2000,3000,8000};
			int i = 0;
			for(; i < headerNames.length; i++){
				 row.createCell(i).setCellValue(headerNames[i]);
				 //设置列的宽度
				 sht.setColumnWidth(i, widths[i]);
			}
			//写入
			i = 1;
			for (Supplier supplier : list) {
				row = sht.createRow(i);
				row.createCell(0).setCellValue(supplier.getName());//名称
				row.createCell(1).setCellValue(supplier.getAddress());//联系地址
				row.createCell(2).setCellValue(supplier.getContact());//联系人
				row.createCell(3).setCellValue(supplier.getTele());//联系电话
				row.createCell(4).setCellValue(supplier.getEmail());//邮件地址
				i++;
			}
			wk.write(os);
		} finally{
			if(null != wk){
				try {
					wk.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	@Override
	public void doImport(InputStream is) throws IOException {
		//读取上传的excel
		Workbook wk = null;
		try {
			wk = new HSSFWorkbook(is);
			//工作表
			Sheet sht = wk.getSheetAt(0);
			//工作表名
			String sheetName = sht.getSheetName();
			//类型
			String type = "";
			if("供应商".equals(sheetName)){
				type = "1";
			}
			if("客户".equals(sheetName)){
				type = "2";
			}
			//工作表最后一行的下标
			int lastRowNum = sht.getLastRowNum();
			Row row = null;
			Supplier supplier = null;
			for(int i = 1; i <= lastRowNum; i++){
				//从下标为1开始
				row = sht.getRow(i);
				//得供应商名称
				String supplierName = row.getCell(0).getStringCellValue();
				//构建查询条件
				supplier = new Supplier();
				//设置查询条件
				supplier.setName(supplierName);
				//通过名称查询
				List<Supplier> list = supplierDao.getList(null, supplier, null);
				if(list.size() > 0){
					//存在
					supplier = list.get(0);//进入持久化
				}
				supplier.setAddress(row.getCell(1).getStringCellValue());//地址
				supplier.setContact(row.getCell(2).getStringCellValue());//联系人
				supplier.setTele(row.getCell(3).getStringCellValue());//联系 电话
				supplier.setEmail(row.getCell(4).getStringCellValue());//邮件地址
				if(list.size() == 0){
					//不存在,新增
					//设置类型
					supplier.setType(type);
					supplierDao.add(supplier);
				}
			}
		} finally {
			if(null != wk){
				try {
					wk.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
}
