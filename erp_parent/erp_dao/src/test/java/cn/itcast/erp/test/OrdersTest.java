package cn.itcast.erp.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext_test.xml"})
public class OrdersTest {
	
	/*@Autowired
	private IOrdersDao ordersDao;*/
	
	@Test
	public void testLogic(){
		//ordersDao.trendReport(2018);
	}
	

}
