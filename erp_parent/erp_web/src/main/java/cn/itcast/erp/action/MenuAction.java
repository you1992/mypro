package cn.itcast.erp.action;
import cn.itcast.erp.biz.IMenuBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Menu;
import cn.itcast.erp.util.WebUtil;

/**
 * 菜单Action 
 * @author Administrator
 *
 */
public class MenuAction extends BaseAction<Menu> {

	private IMenuBiz menuBiz;

	public void setMenuBiz(IMenuBiz menuBiz) {
		this.menuBiz = menuBiz;
		super.setBaseBiz(this.menuBiz);
	}
	
	/**
	 * 显示菜单
	 */
	public void getMenuTree(){
		//根菜单, 进入持久化状态, 循环遍历，是fastJSON把java对象转jsonstring时触发
		//json转时做的动作：{menuid:getMenuid(),menuname:getMenuname(),menus:getMenus()}
		//一级菜单,getMenus()，发生对象导航,当前对象 是1，导航的是多.
		//发送查询多方:select * from menu where pId='0'的主键; 基础数据,人事管理
		//L1Menus里的所有对象进入持久化状态
		/*List<Menu> l1Menus = menu.getMenus();
		for(Menu l1 : l1Menus){
			List<Menu> l2Menus = l1.getMenus();//1:基础数据 100, 多:select * from menu where pId='100'
			for(Menu l2 : l2Menus){
				l2.getMenus();//1:商品类型 101, 多：select * from menu where pId='101'
			}
		}*/
		/*Menu menu = menuBiz.get("0");
		WebUtil.write(menu);*/
		
		Emp loginUser = WebUtil.getLoginUser();
		if(null != loginUser){
			//登陆用户的权限菜单
			/*List<Menu> menuList = menuBiz.readMenusByEmpuuid(loginUser.getUuid());
			WebUtil.write(menuList);*/
			Menu menu = menuBiz.getMenusByEmpuuid(loginUser.getUuid());
			WebUtil.write(menu);
		}
	}

}
