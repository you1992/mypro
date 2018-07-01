package cn.itcast.erp.biz.impl;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;

import cn.itcast.erp.biz.IOrdersBiz;
import cn.itcast.erp.biz.exception.ErpException;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IOrdersDao;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
/**
 * 订单业务逻辑类
 * @author Administrator
 *
 */
public class OrdersBiz extends BaseBiz<Orders> implements IOrdersBiz {

	private IOrdersDao ordersDao;
	private IEmpDao empDao;
	private ISupplierDao supplierDao;
	
	public void setOrdersDao(IOrdersDao ordersDao) {
		this.ordersDao = ordersDao;
		super.setBaseDao(this.ordersDao);
	}
	
	@Override
	public void add(Orders t) {
		//生成日期
		t.setCreatetime(new Date());
		//订单类型, 1=采购订单, 由前端传过来
		//t.setType(Orders.TYPE_IN);
		//得到主题
		Subject subject = SecurityUtils.getSubject();
		
		if(Orders.TYPE_IN.equals(t.getType())){
			//采购申请权限
			if(!subject.isPermitted("我的采购订单")){
				throw new ErpException("没有权限");
			}
		}
		if(Orders.TYPE_OUT.equals(t.getType())){
			//采购申请权限
			if(!subject.isPermitted("我的销售订单")){
				throw new ErpException("没有权限");
			}
		}
		
		double totalMoney = 0;
		//明细
		List<Orderdetail> orderdetails = t.getOrderdetails();
		for (Orderdetail od : orderdetails) {
			totalMoney += od.getMoney();
			//明细的状态
			od.setState(Orderdetail.STATE_NOT_IN);
			//设置订单
			od.setOrders(t);
		}
		//合计金额
		t.setTotalmoney(totalMoney);
		//状态
		if(Orders.TYPE_IN.equals(t.getType())){
			t.setState(Orders.STATE_CREATE);//未审核
		}
		if(Orders.TYPE_OUT.equals(t.getType())){
			t.setState(Orders.STATE_NOT_OUT);//未出库
		}
		
		//级联保存
		super.add(t);
	}
	
	@Override
	public List<Orders> getListByPage(Orders t1, Orders t2, Object param, int firstResult, int maxResults) {
		List<Orders> list = super.getListByPage(t1, t2, param, firstResult, maxResults);
		
		for (Orders orders : list) {
			//设置下单人名称
			orders.setCreaterName(getEmpName(orders.getCreater()));
			orders.setCheckerName(getEmpName(orders.getChecker()));
			orders.setStarterName(getEmpName(orders.getStarter()));
			orders.setEnderName(getEmpName(orders.getEnder()));
			
			//供应商名称
			orders.setSupplierName(supplierDao.get(orders.getSupplieruuid()).getName());
		}
		
		return list;
	}
	
	private String getEmpName(Long uuid){
		if(null == uuid){
			return null;
		}
		return empDao.get(uuid).getName();
	}

	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
	}

	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
	}

	@RequiresPermissions("采购审核")
	@Override
	public void doCheck(Long uuid, Long empuuid) {
		//订单进入持久态
		Orders orders = ordersDao.get(uuid);
		//状态的判断
		if(!Orders.STATE_CREATE.equals(orders.getState())){
			throw new ErpException("该订单已经审核过了，不能重复审核");
		}
		//1. 审核日期   系统时间
		orders.setChecktime(new Date());
		//2. 审核人     登陆用户的编号
		orders.setChecker(empuuid);
		//3. 状态       1：已审核
		orders.setState(Orders.STATE_CHECK);
	}
	
	@Override
	@RequiresPermissions("采购确认")
	public void doStart(Long uuid, Long empuuid) {
		//订单进入持久态
		Orders orders = ordersDao.get(uuid);
		//状态的判断
		if(!Orders.STATE_CHECK.equals(orders.getState())){
			throw new ErpException("该订单已经确认过了，不能重复确认");
		}
		//1. 确认日期   系统时间
		orders.setStarttime(new Date());
		//2. 确认人     登陆用户的编号
		orders.setStarter(empuuid);
		//3. 状态       1：已确认
		orders.setState(Orders.STATE_START);
	}

	@Override
	public void exportById(Long uuid, OutputStream os) throws Exception {
		//创建一个excel
		Workbook wk = null;
		try{
			wk = new HSSFWorkbook();
			//获取订单
			Orders o = ordersDao.get(uuid);
			//明细信息
			List<Orderdetail> orderdetails = o.getOrderdetails();
			
			String title = "";
			if(Orders.TYPE_IN.equals(o.getType())){
				title = "采 购 单";
			}
			if(Orders.TYPE_OUT.equals(o.getType())){
				title = "销 售 单";
			}
			
			//创建工作表
			Sheet sht = wk.createSheet(title);
			
			//标题的样式
			CellStyle style_title = wk.createCellStyle();
			
			
			//创建样式
			CellStyle style_content = wk.createCellStyle();
			
			style_content.setAlignment(CellStyle.ALIGN_CENTER);//水平居中
			style_content.setVerticalAlignment(CellStyle.VERTICAL_CENTER);//垂直居中
			
			//样式复制,主要是复制居中
			style_title.cloneStyleFrom(style_content);
			//标题的字体
			Font font_title = wk.createFont();
			font_title.setFontName("黑体");
			font_title.setFontHeightInPoints((short)18);
			style_title.setFont(font_title);
			
			style_content.setBorderTop(CellStyle.BORDER_THIN);//上边框，细边框
			style_content.setBorderBottom(CellStyle.BORDER_THIN);//下边框，细边框
			style_content.setBorderLeft(CellStyle.BORDER_THIN);//左边框，细边框
			style_content.setBorderRight(CellStyle.BORDER_THIN);//右边框，细边框
			
			//内容的字体
			Font font_content = wk.createFont();
			font_content.setFontName("宋体");//字体名称
			font_content.setFontHeightInPoints((short)11);//字体大小
			style_content.setFont(font_content);
			
			//日期的格式
			CellStyle style_date = wk.createCellStyle();
			style_date.cloneStyleFrom(style_content);
			//创建格式化器
			DataFormat dateFormat = wk.createDataFormat();
			style_date.setDataFormat(dateFormat.getFormat("yyyy-MM-dd HH:mm"));
			
			//创建行
			Row row = sht.createRow(0);//行下标从0开始
			row.setHeight((short)1000);//标题的行高
			
			//需要创建行
			int rowCnt = 9 + orderdetails.size();
			//创建行
			for(int i = 2; i <= rowCnt; i++){
				row = sht.createRow(i);
				row.setHeight((short)500);//设置行高
				//四列
				for(int c = 0; c < 4; c++){
					row.createCell(c).setCellStyle(style_content);
				}
			}
			//列宽
			for(int c = 0; c < 4; c++){
				sht.setColumnWidth(c, 5000);
			}
			
			//合并单元格, 标题
			sht.addMergedRegion(new CellRangeAddress(0,0,0,3));
			//供应商内容
			sht.addMergedRegion(new CellRangeAddress(2,2,1,3));
			//明细
			sht.addMergedRegion(new CellRangeAddress(7,7,0,3));
			
			//设置值
			//标题单元格
			Cell cell = sht.getRow(0).createCell(0);
			//设置标题的样式
			cell.setCellStyle(style_title);
			cell.setCellValue(title);
			//供应商
			sht.getRow(2).getCell(0).setCellValue("供应商");
			//供应商名称
			sht.getRow(2).getCell(1).setCellValue(supplierDao.get(o.getSupplieruuid()).getName());
			
			sht.getRow(3).getCell(0).setCellValue("下单日期");
			sht.getRow(3).getCell(1).setCellValue(o.getCreatetime());
			
			sht.getRow(4).getCell(0).setCellValue("审核日期");
			setDateValue(sht.getRow(4).getCell(1),o.getChecktime());
			
			sht.getRow(5).getCell(0).setCellValue("采购日期");
			setDateValue(sht.getRow(5).getCell(1),o.getStarttime());
			
			sht.getRow(6).getCell(0).setCellValue("入库日期");
			setDateValue(sht.getRow(6).getCell(1),o.getEndtime());
					
			sht.getRow(3).getCell(2).setCellValue("经办人");
			sht.getRow(4).getCell(2).setCellValue("经办人");
			sht.getRow(5).getCell(2).setCellValue("经办人");
			sht.getRow(6).getCell(2).setCellValue("经办人");
			//人
			sht.getRow(3).getCell(3).setCellValue(empDao.get(o.getCreater()).getName());
			sht.getRow(4).getCell(3).setCellValue(getEmpName(o.getChecker()));
			sht.getRow(5).getCell(3).setCellValue(getEmpName(o.getStarter()));
			sht.getRow(6).getCell(3).setCellValue(getEmpName(o.getEnder()));
			
			//订单明细
			sht.getRow(7).getCell(0).setCellValue("订单明细");
			sht.getRow(8).getCell(0).setCellValue("商品名称");
			sht.getRow(8).getCell(1).setCellValue("数量");
			sht.getRow(8).getCell(2).setCellValue("价格");
			sht.getRow(8).getCell(3).setCellValue("金额");
			
			//设置日期样式
			for(int i = 3; i < 7; i++){
				sht.getRow(i).getCell(1).setCellStyle(style_date);
			}
			
			//设置明细内容
			int i = 9;
			for(Orderdetail od : orderdetails){
				row = sht.getRow(i);
				row.getCell(0).setCellValue(od.getGoodsname());//商品名称
				row.getCell(1).setCellValue(od.getNum());//	数量
				row.getCell(2).setCellValue(od.getPrice());//	价格
				row.getCell(3).setCellValue(od.getMoney());//	金额
				i++;
			}
			//合计金额
			sht.getRow(i).getCell(0).setCellValue("合计");
			sht.getRow(i).getCell(3).setCellValue(o.getTotalmoney());
			
			wk.write(os);
		} finally{
			try {
				wk.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 日期不为空，才需要设置值
	 * @param cell
	 * @param date
	 */
	private void setDateValue(Cell cell, Date date){
		if(null != date){
			cell.setCellValue(date);
		}
	}
	
}
