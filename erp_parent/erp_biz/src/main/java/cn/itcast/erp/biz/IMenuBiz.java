package cn.itcast.erp.biz;
import java.util.List;

import cn.itcast.erp.entity.Menu;
/**
 * 菜单业务逻辑层接口
 * @author Administrator
 *
 */
public interface IMenuBiz extends IBaseBiz<Menu>{
	
	/**
	 * 获取用户的菜单权限
	 * @param empuuid
	 * @return
	 */
	List<Menu> readMenusByEmpuuid(Long empuuid);
	
	/**
	 * 获取用户的菜单权限
	 * @param empuuid
	 * @return
	 */
	Menu getMenusByEmpuuid(Long empuuid);
}

