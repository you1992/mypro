package cn.itcast.erp.biz.impl;
import java.util.Date;
import java.util.List;

import com.redsun.bos.ws.impl.IWaybillWs;

import cn.itcast.erp.biz.IOrderdetailBiz;
import cn.itcast.erp.biz.exception.ErpException;
import cn.itcast.erp.dao.IOrderdetailDao;
import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.dao.IStoreoperDao;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.entity.Storedetail;
import cn.itcast.erp.entity.Storeoper;
import cn.itcast.erp.entity.Supplier;
/**
 * 订单明细业务逻辑类
 * @author Administrator
 *
 */
public class OrderdetailBiz extends BaseBiz<Orderdetail> implements IOrderdetailBiz {

	private IOrderdetailDao orderdetailDao;
	private IStoredetailDao storedetailDao;
	private IStoreoperDao storeoperDao;
	private IWaybillWs waybillWs;
	private ISupplierDao supplierDao;
	
	public void setOrderdetailDao(IOrderdetailDao orderdetailDao) {
		this.orderdetailDao = orderdetailDao;
		super.setBaseDao(this.orderdetailDao);
	}

	@Override
	public void doInStore(Long uuid, Long empuuid, Long storeuuid) {
		//********************* 1. 明细表(orderdetail)，查询持久化状态 ************************
		Orderdetail od = orderdetailDao.get(uuid);
		//	1.0 状态的判断(不能重复入库)
		if(!Orderdetail.STATE_NOT_IN.equals(od.getState())){
			throw new ErpException("不能重复入库!");
		}
		//	1.1 结束日期  系统时间
		od.setEndtime(new Date());
		//	1.2 库管员    登陆用户的编号
		od.setEnder(empuuid);
		//	1.3 仓库编号  前端传过来
		od.setStoreuuid(storeuuid);
		//	1.4 状态      1：已入库
		od.setState(Orderdetail.STATE_IN);
		//****************************************************************
		
		//********************* 2. 库存表(storedetail) ************************		
		//	2.1 判断是否存在库存信息,查询
		Storedetail sd = new Storedetail();
		//		条件：
		//			仓库编号，前端传过来
		sd.setStoreuuid(storeuuid);
		//			商品编号, 明细中商品编号
		sd.setGoodsuuid(od.getGoodsuuid());
		List<Storedetail> list = storedetailDao.getList(sd, null, null);
		//	2.2 存在：数量更新 库存信息进入持久化状态，setNum(已有+明细里的数量)
		if(list.size() > 0){
			sd = list.get(0);//持久化状态
			//setNum(已有+明细里的数量)
			sd.setNum(sd.getNum() + od.getNum());
		}else{
		//	2.3 不存在：插入数据，构建对象进行保存
			//解bug的地方，要把明细的数量给它
			sd.setNum(od.getNum());
			storedetailDao.add(sd);
		}
		//****************************************************************
		
		//********************* 3. 库存变更记录表(storeoper) ************************
		Storeoper log = new Storeoper();
		//	插入数据
		//		操作员工编号 登陆用户的编号
		log.setEmpuuid(empuuid);
		//		操作日期 系统时间
		log.setOpertime(od.getEndtime());
		//		仓库编号 前端传过来
		log.setStoreuuid(storeuuid);
		//		商品编号 明细中商品编号
		log.setGoodsuuid(od.getGoodsuuid());
		//		数量     明细里的数量
		log.setNum(od.getNum());
		//		操作类型 1：入库
		log.setType(Storeoper.TYPE_IN);
		storeoperDao.add(log);
		//****************************************************************
		
		//********************* 4. 订单表(orders) ************************
		Orders orders = od.getOrders();//进入持久化
		//	4.1. 判断是否所有的明细都入库(
		//    条件：
		Orderdetail queryParam = new Orderdetail();
		//	订单编号，
		queryParam.setOrders(orders);
		//	状态为0 未入库 
		queryParam.setState(Orderdetail.STATE_NOT_IN);
		//   
		long count = orderdetailDao.getCount(queryParam, null, null);
		//		查询明细表中属于这个订单中状态为0的个数，查询未入库的个数
		if(count == 0){
		//	4.2 都入库了 
		//		入库日期    系统时间
			orders.setEndtime(od.getEndtime());
		//		库管员		登陆用户的编号
			orders.setEnder(empuuid);
		//		订单状态    3:已入库
			orders.setState(Orders.STATE_END);
		}
		
		//	4.3 只要有一个没有入库，不管
		
	}

	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
	}

	public void setStoreoperDao(IStoreoperDao storeoperDao) {
		this.storeoperDao = storeoperDao;
	}

	@Override
	public void doOutStore(Long uuid, Long empuuid, Long storeuuid) {
		//********************* 1. 明细表(orderdetail)，查询持久化状态 ************************
		Orderdetail od = orderdetailDao.get(uuid);
		//	1.0 状态的判断(不能重复出库)
		if(!Orderdetail.STATE_NOT_OUT.equals(od.getState())){
			throw new ErpException("不能重复出库!");
		}
		//	1.1 结束日期  系统时间
		od.setEndtime(new Date());
		//	1.2 库管员    登陆用户的编号
		od.setEnder(empuuid);
		//	1.3 仓库编号  前端传过来
		od.setStoreuuid(storeuuid);
		//	1.4 状态      1：已出库
		od.setState(Orderdetail.STATE_OUT);
		//****************************************************************
		
		//********************* 2. 库存表(storedetail) ************************		
		//	2.1 判断是否存在库存信息,查询
		Storedetail sd = new Storedetail();
		//		条件：
		//			仓库编号，前端传过来
		sd.setStoreuuid(storeuuid);
		//			商品编号, 明细中商品编号
		sd.setGoodsuuid(od.getGoodsuuid());
		List<Storedetail> list = storedetailDao.getList(sd, null, null);
		//	2.2 存在：数量更新 库存信息进出持久化状态，setNum(已有+明细里的数量)
		long num = -1;
		if(list.size() > 0){
			sd = list.get(0);//持久化状态
			//setNum(已有+明细里的数量)
			num = sd.getNum() - od.getNum();
			sd.setNum(num);
		}
		if(sd.getNum() < 0){
			throw new ErpException("库存不足");
		}
		//****************************************************************
		
		//********************* 3. 库存变更记录表(storeoper) ************************
		Storeoper log = new Storeoper();
		//	插出数据
		//		操作员工编号 登陆用户的编号
		log.setEmpuuid(empuuid);
		//		操作日期 系统时间
		log.setOpertime(od.getEndtime());
		//		仓库编号 前端传过来
		log.setStoreuuid(storeuuid);
		//		商品编号 明细中商品编号
		log.setGoodsuuid(od.getGoodsuuid());
		//		数量     明细里的数量
		log.setNum(od.getNum());
		//		操作类型 1：出库
		log.setType(Storeoper.TYPE_OUT);
		storeoperDao.add(log);
		//****************************************************************
		
		//********************* 4. 订单表(orders) ************************
		Orders orders = od.getOrders();//进出持久化
		//	4.1. 判断是否所有的明细都出库(
		//    条件：
		Orderdetail queryParam = new Orderdetail();
		//	订单编号，
		queryParam.setOrders(orders);
		//	状态为0 未出库 
		queryParam.setState(Orderdetail.STATE_NOT_OUT);
		//   
		long count = orderdetailDao.getCount(queryParam, null, null);
		//		查询明细表中属于这个订单中状态为0的个数，查询未出库的个数
		if(count == 0){
		//	4.2 都出库了 
		//		出库日期    系统时间
			orders.setEndtime(od.getEndtime());
		//		库管员		登陆用户的编号
			orders.setEnder(empuuid);
		//		订单状态    3:已出库
			orders.setState(Orders.STATE_OUT);
			
			//客户编号
			Long supplieruuid = orders.getSupplieruuid();
			//客户信息
			Supplier customer = supplierDao.get(supplieruuid);
			// String toaddress, String addressee, String tele, String info
			Long sn = waybillWs.add(1l, customer.getAddress(), customer.getContact(), customer.getTele(), "--");
			//设置运单号
			orders.setWaybillsn(sn);
		}
		
		//	4.3 只要有一个没有出库，不管
		
	}

	public void setWaybillWs(IWaybillWs waybillWs) {
		this.waybillWs = waybillWs;
	}

	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
	}
	
}
