package cn.itcast.erp.dao;

import java.util.List;

import cn.itcast.erp.entity.Menu;
/**
 * 菜单数据访问接口
 * @author Administrator
 *
 */
public interface IMenuDao extends IBaseDao<Menu>{

	/**
	 * 获取用户的菜单权限
	 * @param empuuid
	 * @return
	 */
	List<Menu> readMenusByEmpuuid(Long empuuid);
}
