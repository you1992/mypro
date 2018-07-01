package cn.itcast.erp.biz.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.itcast.erp.biz.IReportBiz;
import cn.itcast.erp.dao.IReportDao;

public class ReportBiz implements IReportBiz {
	
	private IReportDao reportDao;

	@Override
	public List<Map<String,Object>> orderReport(Date startDate, Date endDate) {
		return reportDao.orderReport(startDate, endDate);
	}

	public void setReportDao(IReportDao reportDao) {
		this.reportDao = reportDao;
	}

	@Override
	public List<Map<String, Object>> trendReport(int year) {
		//返回的数据
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		//1年有12个月
		for(int i = 1; i <= 12; i++){
			Map<String, Object> monthData = reportDao.trendReport(year, i);
			if(null == monthData){
				//没有数据就要补数据
				monthData = new HashMap<String,Object>();
				monthData.put("name", i);//月份
				monthData.put("y", 0);//销售额, 没有就补0
			}
			result.add(monthData);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> tr2(int year) {
		
		return reportDao.tr2(year);
	}

}
