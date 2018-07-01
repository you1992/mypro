package cn.itcast.erp.action;
import cn.itcast.erp.biz.IOrderdetailBiz;
import cn.itcast.erp.biz.exception.ErpException;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.util.WebUtil;

/**
 * 订单明细Action 
 * @author Administrator
 *
 */
public class OrderdetailAction extends BaseAction<Orderdetail> {

	private IOrderdetailBiz orderdetailBiz;
	private Long storeuuid;//仓库编号

	public void setOrderdetailBiz(IOrderdetailBiz orderdetailBiz) {
		this.orderdetailBiz = orderdetailBiz;
		super.setBaseBiz(this.orderdetailBiz);
	}
	
	/**
	 * 入库
	 */
	public void doInStore(){
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			WebUtil.ajaxReturn(false, "您还没有登陆!");
			return;
		}
		try {
			orderdetailBiz.doInStore(getId(), loginUser.getUuid(), storeuuid);
			WebUtil.ajaxReturn(true, "入库成功");
		} catch (ErpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, e.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "入库失败");
		}
	}
	
	/**
	 * 出库
	 */
	public void doOutStore(){
		Emp loginUser = WebUtil.getLoginUser();
		if(null == loginUser){
			WebUtil.ajaxReturn(false, "您还没有登陆!");
			return;
		}
		try {
			orderdetailBiz.doOutStore(getId(), loginUser.getUuid(), storeuuid);
			WebUtil.ajaxReturn(true, "出库成功");
		} catch (ErpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, e.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			WebUtil.ajaxReturn(false, "出库失败");
		}
	}

	public void setStoreuuid(Long storeuuid) {
		this.storeuuid = storeuuid;
	}

}
