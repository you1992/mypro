package cn.itcast.erp.action;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;
import com.redsun.bos.ws.Waybilldetail;
import com.redsun.bos.ws.impl.IWaybillWs;

import cn.itcast.erp.biz.IOrdersBiz;
import cn.itcast.erp.biz.exception.ErpException;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.util.WebUtil;

/**
 * 订单Action 
 * @author Administrator
 *
 */
public class OrdersAction extends BaseAction<Orders> {

	private IOrdersBiz ordersBiz;
	private String json;
	private IWaybillWs waybillWs;
	
	@Override
	public void add() {
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			WebUtil.ajaxReturn(false, "您还没有登陆!");
			return;
		}
		
		Orders orders = getT();
		orders.setCreater(loginUser.getUuid());//下单员
		System.out.println("supplieruuid:" + orders.getSupplieruuid());
		System.out.println(json);
		//把json字符串转java 的list集合
		//[{},{}] name:sdf,id:sdf split(","),
		//{t1:{},t2:{}} => Map<Sring,Object>.get("t1")
		List<Orderdetail> orderdetails = JSON.parseArray(json, Orderdetail.class);
		//订单下的明细
		orders.setOrderdetails(orderdetails);
		super.add();
		
	}
	
	/**
	 * 订单审核
	 */
	public void doCheck(){
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			WebUtil.ajaxReturn(false, "您还没有登陆!");
			return;
		}
		long uuid = getId();//获取订单编号
		try {
			ordersBiz.doCheck(uuid, loginUser.getUuid());
			WebUtil.ajaxReturn(true, "审核成功");
		} catch (ErpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, e.getMessage());
		} catch (UnauthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "没有权限");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "审核失败");
		}
	}
	
	/**
	 * 订单确认
	 */
	public void doStart(){
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			WebUtil.ajaxReturn(false, "您还没有登陆!");
			return;
		}
		long uuid = getId();//获取订单编号
		try {
			ordersBiz.doStart(uuid, loginUser.getUuid());
			WebUtil.ajaxReturn(true, "确认成功");
		}catch (ErpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, e.getMessage());
		} catch (UnauthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "没有权限");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "确认失败");
		}
	}
	
	/**
	 * 我的订单
	 * 只查登陆用户创建的订单
	 */
	public void myListByPage(){
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			return;
		}
		if(null == getT1()){
			//构建查询条件
			setT1(new Orders());
		}
		//下单员-登陆用户的条件
		getT1().setCreater(loginUser.getUuid());
		super.listByPage();
	}
	
	/**
	 * 导出订单信息
	 */
	public void exportById(){
		//字符串格式化
		String filename = String.format("orders_%d.xls", getId());
		// orders_10.xls
		
		HttpServletResponse res = ServletActionContext.getResponse();
		try {
			//告诉浏览器，响应的是一个附件，字节流来接收并且保存成文件		
			res.setHeader("Content-Disposition","attachment;filename=" + filename);
			ordersBiz.exportById(getId(),res.getOutputStream());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Long waybillsn;//运单号
	
	/**
	 * 查询物流路径信息
	 */
	public void waybilldetailList(){
		List<Waybilldetail> list = waybillWs.waybilldetailList(waybillsn);
		WebUtil.write(list);
	}

	public void setOrdersBiz(IOrdersBiz ordersBiz) {
		this.ordersBiz = ordersBiz;
		super.setBaseBiz(this.ordersBiz);
	}

	public void setJson(String json) {
		this.json = json;
	}
	
	public static void main(String[] args){
		System.out.println(String.format("%03d",99));
	}

	public void setWaybillWs(IWaybillWs waybillWs) {
		this.waybillWs = waybillWs;
	}

	public void setWaybillsn(Long waybillsn) {
		this.waybillsn = waybillsn;
	}

}
