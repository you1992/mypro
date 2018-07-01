package cn.itcast.erp.dao.impl;
import java.util.List;

import cn.itcast.erp.dao.IMenuDao;
import cn.itcast.erp.entity.Menu;
/**
 * 菜单数据访问类
 * @author Administrator
 *
 */
@SuppressWarnings("unchecked")
public class MenuDao extends BaseDao<Menu> implements IMenuDao {

	@Override
	public List<Menu> readMenusByEmpuuid(Long empuuid) {
		String hql = "select m from Emp e join e.roles r join r.menus m where e.uuid=?";
		return (List<Menu>) this.getHibernateTemplate().find(hql, empuuid);
	}

}
