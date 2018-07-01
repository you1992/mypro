package cn.itcast.erp.biz.impl;
import java.util.ArrayList;
import java.util.List;

import cn.itcast.erp.biz.IMenuBiz;
import cn.itcast.erp.dao.IMenuDao;
import cn.itcast.erp.entity.Menu;
/**
 * 菜单业务逻辑类
 * @author Administrator
 *	
 */
public class MenuBiz extends BaseBiz<Menu> implements IMenuBiz {

	private IMenuDao menuDao;
	
	public void setMenuDao(IMenuDao menuDao) {
		this.menuDao = menuDao;
		super.setBaseDao(this.menuDao);
	}

	@Override
	public List<Menu> readMenusByEmpuuid(Long empuuid) {
		
		return menuDao.readMenusByEmpuuid(empuuid);
	}

	@Override
	public Menu getMenusByEmpuuid(Long empuuid) {
		// 获取用户的菜单
		List<Menu> empMenus = readMenusByEmpuuid(empuuid);
		//复制的根菜单(模板), 进入 持久化
		Menu root = menuDao.get("0");
		//复制的结果
		Menu result = cloneMenu(root);
		
		//循环一级菜单
		for(Menu l1 : root.getMenus()){
			// _变量代表的是复制得到, 此时一级菜单进入复制状态
			Menu _l1 = cloneMenu(l1);
			
			//循环二级菜单， 复制二菜单
			for(Menu l2 : l1.getMenus()){
				//判断用户下是否包含这个菜单
				if(empMenus.contains(l2)){
					//复制二级菜单
					Menu _l2 = cloneMenu(l2);
					_l1.getMenus().add(_l2);
				}
			}
			
			if(_l1.getMenus().size() > 0){
				// 把复制后的一级菜单放进来
				result.getMenus().add(_l1);
			}
			
		}
		
		return result;
	}
	
	/**
	 * 复制菜单，
	 * @param src
	 * @return 创建的新对象
	 */
	private Menu cloneMenu(Menu src){
		Menu menu = new Menu();
		
		menu.setIcon(src.getIcon());
		menu.setMenuid(src.getMenuid());
		menu.setMenuname(src.getMenuname());
		menu.setUrl(src.getUrl());
		
		menu.setMenus(new ArrayList<Menu>());
		
		return menu;
	}
	
}
