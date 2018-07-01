package cn.itcast.erp.dao.impl;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.entity.Emp;
/**
 * 员工数据访问类
 * @author Administrator
 *
 */
@SuppressWarnings("unchecked")
public class EmpDao extends BaseDao<Emp> implements IEmpDao {

	/**
	 * 构建查询条件
	 * @param dep1
	 * @param dep2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Emp emp1,Emp emp2,Object param){
		DetachedCriteria dc=DetachedCriteria.forClass(Emp.class);
		if(emp1!=null){
			if(null != emp1.getUsername() && emp1.getUsername().trim().length()>0){
				dc.add(Restrictions.like("username", emp1.getUsername(), MatchMode.ANYWHERE));
			}
			if(null != emp1.getName() && emp1.getName().trim().length()>0){
				dc.add(Restrictions.like("name", emp1.getName(), MatchMode.ANYWHERE));
			}
			if(null != emp1.getEmail() && emp1.getEmail().trim().length()>0){
				dc.add(Restrictions.like("email", emp1.getEmail(), MatchMode.ANYWHERE));
			}
			if(null != emp1.getTele() && emp1.getTele().trim().length()>0){
				dc.add(Restrictions.like("tele", emp1.getTele(), MatchMode.ANYWHERE));
			}
			if(null != emp1.getAddress() && emp1.getAddress().trim().length()>0){
				dc.add(Restrictions.like("address", emp1.getAddress(), MatchMode.ANYWHERE));
			}
			
			//按部门查询
			if(null != emp1.getDep() && null != emp1.getDep().getUuid()){
				dc.add(Restrictions.eq("dep", emp1.getDep()));
			}
			//按性别查询
			if(null != emp1.getGender()){
				dc.add(Restrictions.eq("gender", emp1.getGender()));
			}
			
			//开始日期
			if(null != emp1.getBirthday()){
				// >=
				dc.add(Restrictions.ge("birthday", emp1.getBirthday()));
			}
		}
		//有结束日期
		if(null != emp2){
			if(emp2.getBirthday() != null){
				// <=
				dc.add(Restrictions.le("birthday", emp2.getBirthday()));
			}
		}
		
		return dc;
	}

	@Override
	public Emp findByUsernameAndPwd(String username, String pwd) {
		String hql = "from Emp where username=? and pwd=?";
		
		List<Emp> list = (List<Emp>)getHibernateTemplate().find(hql, username,pwd);
		if(null != list && list.size() > 0){
			//用户和密码匹配
			return list.get(0);
		}
		return null;
	}

	@Override
	public void updatePwd(String newPwd, Long uuid) {
		String hql = "update Emp set pwd=? where uuid=?";
		//更新
		getHibernateTemplate().bulkUpdate(hql, newPwd, uuid);
	}

}
