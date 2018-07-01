package cn.itcast.erp.biz;
import java.util.List;

import cn.itcast.erp.entity.Role;
import cn.itcast.erp.entity.Tree;
/**
 * 角色业务逻辑层接口
 * @author Administrator
 *
 */
public interface IRoleBiz extends IBaseBiz<Role>{


	/**
	 * 读取角色菜单列表
	 * @param roleuuid
	 * @return
	 */
	List<Tree> readRoleMenus(Long uuid);
	
	/**
	 * 更新角色权限
	 * @param uuid
	 * @param ids
	 */
	void updateRoleMenus(Long uuid, String ids);
}

