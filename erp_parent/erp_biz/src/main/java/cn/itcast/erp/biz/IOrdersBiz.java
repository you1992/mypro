package cn.itcast.erp.biz;
import java.io.OutputStream;

import cn.itcast.erp.entity.Orders;
/**
 * 订单业务逻辑层接口
 * @author Administrator
 *
 */
public interface IOrdersBiz extends IBaseBiz<Orders>{
	
	/**
	 * 审核
	 * @param uuid
	 * @param empuuid
	 */
	void doCheck(Long uuid, Long empuuid);
	
	/**
	 * 确认
	 * @param uuid
	 * @param empuuid
	 */
	void doStart(Long uuid, Long empuuid);
	
	/**
	 * 导出订单
	 * @param uuid
	 * @param os
	 * @throws Exception
	 */
	void exportById(Long uuid, OutputStream os) throws Exception;

}

