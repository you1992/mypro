package cn.itcast.erp.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报表数据访问接口
 *
 */
public interface IReportDao {

	/**
	 * 销售统计报表
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<Map<String,Object>> orderReport(Date startDate, Date endDate);
	
	/**
	 * 趋势报表
	 * @param year
	 * @param month
	 * @return
	 */
	Map<String,Object> trendReport(int year, int month);
	
	 List<Map<String, Object>> tr2(int year);
}
