package cn.itcast.erp.test;

import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.redsun.bos.ws.Waybilldetail;
import com.redsun.bos.ws.impl.IWaybillWs;
import com.redsun.bos.ws.impl.WaybillWsService;

public class WsClientTest {

	@Test
	public void t1(){
		
		WaybillWsService ws = new WaybillWsService();
		IWaybillWs ws2 = ws.getWaybillWsPort();
		List<Waybilldetail> waybilldetailList = ws2.waybilldetailList(6l);
		for (Waybilldetail waybilldetail : waybilldetailList) {
			System.out.println(waybilldetail.toString());
		}
	}
	
	@Test
	public void t2(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext_cxf.xml");
		IWaybillWs ws = (IWaybillWs)ac.getBean("waybillClient");
		List<Waybilldetail> waybilldetailList = ws.waybilldetailList(6l);
		for (Waybilldetail waybilldetail : waybilldetailList) {
			System.out.println(waybilldetail.toString());
		}
	}
}
