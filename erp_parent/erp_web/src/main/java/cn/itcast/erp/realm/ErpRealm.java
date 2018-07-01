package cn.itcast.erp.realm;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.biz.IMenuBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Menu;
import cn.itcast.erp.util.Const;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class ErpRealm extends AuthorizingRealm {
	
	private IEmpBiz empBiz;
	
	private IMenuBiz menuBiz;
	
	private JedisPool jedis;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		System.out.println("执行了授权方法");
		//保存了登陆用户的权限信息
		SimpleAuthorizationInfo sai = new SimpleAuthorizationInfo();
		//授权
		/*sai.addStringPermission("重置密码");
		sai.addStringPermission("采购订单查询");
		sai.addStringPermission("采购确认");*/
		//得到登陆用户
		Emp emp = (Emp)principals.getPrimaryPrincipal();
		//获取登陆用户的权限菜单
		List<Menu> menus = null;
		// 拼接缓存中的key值
		String key = Const.MENU_CACHE + emp.getUuid();
		
		Jedis jds = jedis.getResource();
		// 从redis取值
		String string = jds.get(key);
		
		if(StringUtils.isEmpty(string)){
			//获取登陆用户的权限菜单
			menus = menuBiz.readMenusByEmpuuid(emp.getUuid());
			//由于redis只接收基础数据类型,要转成字符串
			String jsonString = JSON.toJSONString(menus);
			//存入jedis中
			jds.set(key, jsonString);
		}else{
			//把json字符串转成java list集合
			menus = JSON.parseArray(string, Menu.class);
		}
		
		jds.close();
		//动态授权，不同的登陆用户，授不同的权限
		for (Menu menu : menus) {
			sai.addStringPermission(menu.getMenuname());
		}
		return sai;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		System.out.println("执行了认证方法");
		//令牌信息
		UsernamePasswordToken upt = (UsernamePasswordToken)token;
		//用户名
		String username = upt.getUsername();
		//密码
		String pwd = new String(upt.getPassword());
		Emp emp = empBiz.findByUsernameAndPwd(username, pwd);
		if(null != emp){
			//构建认证信息
			//principle 当事者
			//credentials 密码 凭证
			SimpleAuthenticationInfo sai = new SimpleAuthenticationInfo(emp,pwd,getName());
			return sai;
		}
		return null;
	}

	public void setEmpBiz(IEmpBiz empBiz) {
		this.empBiz = empBiz;
	}

	public void setMenuBiz(IMenuBiz menuBiz) {
		this.menuBiz = menuBiz;
	}

	public void setJedis(JedisPool jedis) {
		this.jedis = jedis;
	}

}
