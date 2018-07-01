package cn.itcast.erp.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.crypto.hash.Md5Hash;

import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.biz.exception.ErpException;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IRoleDao;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Role;
import cn.itcast.erp.entity.Tree;
import cn.itcast.erp.util.Const;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 员工业务逻辑类
 * 
 * @author Administrator
 *
 */
public class EmpBiz extends BaseBiz<Emp> implements IEmpBiz {

	private IEmpDao empDao;
	private IRoleDao roleDao;
	private JedisPool jedis;

	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
		super.setBaseDao(this.empDao);
	}

	@Override
	public Emp findByUsernameAndPwd(String username, String pwd) {
		pwd = encrypt(pwd, username);
		System.out.println(pwd);
		return empDao.findByUsernameAndPwd(username, pwd);
	}

	@Override
	public void add(Emp t) {
		// source：要加密的内容
		// salt: 盐：扰乱码
		// hashIteration: 散列(MD5)次数
		Md5Hash md5 = new Md5Hash(t.getUsername(), t.getUsername(), 2);
		// 得到加密后的密码
		String encryptedPwd = md5.toString();
		// 设置密码为加密后的密码
		t.setPwd(encryptedPwd);
		super.add(t);
	}

	/**
	 * 加密密码
	 * 
	 * @param src
	 * @param salt
	 * @return
	 */
	private String encrypt(String src, String salt) {
		// source：要加密的内容
		// salt: 盐：扰乱码
		// hashIteration: 散列(MD5)次数
		Md5Hash md5 = new Md5Hash(src, salt, 2);
		// 得到加密后的密码
		return md5.toString();
	}

	@Override
	public void updatePwd(String oldPwd, String newPwd, Long uuid) {
		// 查询员工信息
		Emp emp = empDao.get(uuid);
		// 加密原密码
		oldPwd = encrypt(oldPwd, emp.getUsername());
		// 校验原密码是否正确
		if (!oldPwd.equals(emp.getPwd())) {
			// 原密码不正确
			throw new ErpException("原密码不正确");
		}
		// 加密新密码
		newPwd = encrypt(newPwd, emp.getUsername());
		empDao.updatePwd(newPwd, uuid);
	}

	@Override
	public void updatePwd_reset(String newPwd, Long uuid) {
		// 查询员工信息
		Emp emp = empDao.get(uuid);
		// 加密新密码
		newPwd = encrypt(newPwd, emp.getUsername());
		// 更新密码
		empDao.updatePwd(newPwd, uuid);
	}

	@Override
	public List<Tree> readEmpRoles(Long uuid) {
		// 获取所有角色列表 进入持久化
		List<Role> roleList = roleDao.getList(null, null, null);
		// 获取用户 进入持久化
		Emp emp = empDao.get(uuid);
		// 取出用户拥有的角色 进入持久化
		List<Role> empRoles = emp.getRoles();

		// 返回的树节点集合
		List<Tree> treeList = new ArrayList<Tree>();
		// 一级菜单 进入持久化
		for (Role role : roleList) {
			Tree t = createTree(role);
			if(null != empRoles){
				// 用户是否拥有这个角色
				if (empRoles.contains(role)) {
					// 有的话就应该让它选中
					t.setChecked(true);
				}
			}
			treeList.add(t);
		}
		return treeList;
	}

	private Tree createTree(Role role) {
		Tree tree = new Tree();

		tree.setId(role.getUuid() + "");
		tree.setText(role.getName());
		tree.setChildren(new ArrayList<Tree>());

		return tree;
	}

	@Override
	public void updateEmpRoles(Long uuid, String ids) {
		// 获取用户 进入持久化
		Emp emp = empDao.get(uuid);
		// 清空关系 
		emp.setRoles(new ArrayList<Role>());
		
		//添加新的关系 
		String[] roleuuids = ids.split(",");
		for (String roleuuid : roleuuids) {
			//给用户添加角色
			emp.getRoles().add(roleDao.get(Long.valueOf(roleuuid)));
		}
		
		// 清除用户的权限缓存
		Jedis jds = jedis.getResource();
		jds.del(Const.MENU_CACHE + uuid);
		jds.close();

	}

	public void setRoleDao(IRoleDao roleDao) {
		this.roleDao = roleDao;
	}

	public void setJedis(JedisPool jedis) {
		this.jedis = jedis;
	}

}
