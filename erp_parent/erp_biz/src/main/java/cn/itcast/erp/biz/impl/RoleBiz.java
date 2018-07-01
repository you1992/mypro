package cn.itcast.erp.biz.impl;
import java.util.ArrayList;
import java.util.List;

import cn.itcast.erp.biz.IRoleBiz;
import cn.itcast.erp.dao.IMenuDao;
import cn.itcast.erp.dao.IRoleDao;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Menu;
import cn.itcast.erp.entity.Role;
import cn.itcast.erp.entity.Tree;
import cn.itcast.erp.util.Const;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
/**
 * 角色业务逻辑类
 * @author Administrator
 *
 */
public class RoleBiz extends BaseBiz<Role> implements IRoleBiz {

	private IRoleDao roleDao;
	private IMenuDao menuDao;
	private JedisPool jedis;
	
	public void setRoleDao(IRoleDao roleDao) {
		this.roleDao = roleDao;
		super.setBaseDao(this.roleDao);
	}

	@Override
	public List<Tree> readRoleMenus(Long roleuuid) {
		// 根的菜单， 进入持久化
		Menu rootMenu = menuDao.get("0");
		// 获取角色信息， 进入持久化
		Role role = roleDao.get(roleuuid);
		// 角色下拥有的权限 roleMeuns进入持久化
		List<Menu> roleMeuns = role.getMenus();
		
		// 返回的树节点集合
		List<Tree> treeList = new ArrayList<Tree>();
		// 一级菜单 进入持久化
		for (Menu l1 : rootMenu.getMenus()) {
			//创建一级节点
			Tree t1 = createTree(l1);
			//添加一级节点到返回结果中
			treeList.add(t1);
			//二级菜单 进入持久化
			for(Menu l2 : l1.getMenus()){
				
				//创建二级节点
				Tree t2 = createTree(l2);
				//如果这个角色下的菜单集合中包含有这个菜单，让它选中
				if(roleMeuns.contains(l2)){
					t2.setChecked(true);//让它选中
				}
				//一级节点加入二级节点
				t1.getChildren().add(t2);
			}
		}
		
		return treeList;
	}
	
	private Tree createTree(Menu menu){
		Tree tree = new Tree();
		
		tree.setId(menu.getMenuid());
		tree.setText(menu.getMenuname());
		tree.setChildren(new ArrayList<Tree>());
		
		return tree;
	}

	public void setMenuDao(IMenuDao menuDao) {
		this.menuDao = menuDao;
	}

	@Override
	public void updateRoleMenus(Long uuid, String ids) {
		//获取角色，进入持久化
		Role role = roleDao.get(uuid);
		// delete from role_menu where roleuuid=?
		role.setMenus(new ArrayList<Menu>());
		
		//添加新的关系
		String[] menuids = ids.split(",");
		for (String menuid : menuids) {
			// menu 进入持久化
			Menu menu = menuDao.get(menuid);
			role.getMenus().add(menu);
		}
		
		//****清空缓存
		
		//取出拥有这个角色的所有用户列表
		List<Emp> emps = role.getEmps();
		
		Jedis jds = jedis.getResource();
		//循环删除缓存
		for (Emp emp : emps) {
			//清除用户的权限缓存
			jds.del(Const.MENU_CACHE + emp.getUuid());
		}
		jds.close();
	}

	public void setJedis(JedisPool jedis) {
		this.jedis = jedis;
	}
	
}
