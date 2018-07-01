package cn.itcast.erp.action;
import cn.itcast.erp.biz.IStoreBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Store;
import cn.itcast.erp.util.WebUtil;

/**
 * 仓库Action 
 * @author Administrator
 *
 */
public class StoreAction extends BaseAction<Store> {

	private IStoreBiz storeBiz;

	public void setStoreBiz(IStoreBiz storeBiz) {
		this.storeBiz = storeBiz;
		super.setBaseBiz(this.storeBiz);
	}
	
	/**
	 * 我的仓库
	 */
	public void myList(){
		Emp loginUser = WebUtil.getLoginUser();
		if(null != loginUser){
			if(null == getT1()){
				//构建查询条件
				setT1(new Store());
			}
			getT1().setEmpuuid(loginUser.getUuid());
			super.list();
		}
	}

}
