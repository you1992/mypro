package cn.itcast.erp.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import cn.itcast.erp.dao.IReportDao;
@SuppressWarnings("unchecked")
public class ReportDao extends HibernateDaoSupport implements IReportDao {
	
	@Override
	public List<Map<String,Object>> orderReport(Date startDate, Date endDate) {
		// new Map(), 对查询结果的封装
		// 把每行数据转成一个map，key=别名,value=行的值
		String hql = "select new Map(gt.name as name, sum(od.money) as y) "
				+ "from Goodstype gt, Orderdetail od, Orders o, Goods g "
				+ "where gt=g.goodstype and od.goodsuuid = g.uuid "
				+ "and o=od.orders "
				+ "and o.type='2' ";
		List<Date> params = new ArrayList<Date>();
		if(null != startDate){
			hql += "and o.createtime >=? "; 
			params.add(startDate);
		}
		if(null != endDate){
			hql += "and o.createtime <=? "; 
			params.add(endDate);
		}
		hql += "group by gt.name";
		return (List<Map<String,Object>>)getHibernateTemplate().find(hql, params.toArray());
	}

	@Override
	public Map<String, Object> trendReport(int year, int month) {
		//month(o.createtime) hibernate封装,=extract(month from o.createtime)
		String hql = "select new Map(month(o.createtime) as name,sum(od.money) as y) "
				+ "from Orders o,Orderdetail od "
				+ "where o=od.orders "
				+ "and o.type='2' and year(o.createtime)=? "
				+ "and month(o.createtime)=? "
				+ "group by month(o.createtime)";
		List<?> list = getHibernateTemplate().find(hql, year, month);
		if(list != null && list.size() > 0){
			return (Map<String, Object>)list.get(0);
		}
		return null;
	}
	
	@Override
	public List<Map<String, Object>> tr2(final int year) {
		//month(o.createtime) hibernate封装,=extract(month from o.createtime)
		final String hql = "select t1.mon,nvl(t2.y,0) from ( "
				+ "    select rownum as mon from dual connect by rownum<=12 "
				+ ") t1, ( "
				+ "    select extract(month from o.createtime) as mon,sum(od.money) as y from orders o,orderdetail od "
				+ "    where o.uuid=od.ordersuuid "
				+ "    and o.type='2' and extract(year from o.createtime)=? "
				+ "    group by extract(month from o.createtime) "
				+ ") t2 where t1.mon=t2.mon(+) "
				+ "order by 1";
		
		return (List<Map<String, Object>>) this.getHibernateTemplate().executeWithNativeSession(new HibernateCallback<List<Map<String,Object>>>(){

			@Override
			public List<Map<String,Object>> doInHibernate(Session session) throws HibernateException {
				// TODO Auto-generated method stub
				SQLQuery sqlQuery = session.createSQLQuery(hql);
				sqlQuery.setParameter(0, year);
				List<?> list = sqlQuery.list();
				List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
				Map<String,Object> data = null;
				for (Object object : list) {
					data = new HashMap<String,Object>();
					Object[] obj = (Object[]) object;
					data.put("name", obj[0] + "月");
					data.put("y", obj[1]);
					result.add(data);
				}
				return result;
			}});
	}

}
