var url=location.search;
//http://localhost:8080/erp/updatePwd_reset.html?type=1&oper=add
//orders.html?abc=doCheck&type=1 => Request.abc
//url = ?oper=doCheck&type=1
//url=location.search=?type=1&oper=add
var Request = new Object();
if(url.indexOf("?")!=-1)
{
    var str = url.substr(1)
    //oper=doCheck&type=1
	//str = type=1&oper=add
    strs = str.split("&");
    
    //["oper=doCheck","type=1"]
    
	//["type=1", "oper=add"]
    for(var i=0;i<strs.length;i++)
    {
    	//i=0; "oper=doCheck" => ["oper","doCheck"] => Request["oper"] = "doCheck" => map.put("oper","doCheck"), js对象动态添加属性
    	
    	//js对象的取值：obj.属性(不能带.), []
    	//submitData['t.supplieruuid']
    	
    	
    	
        //Request[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
		var p = strs[i].split("=");
        Request[p[0]]=unescape(p[1]);
		//type=1, split => ["type","1"]
		//unescape去掉url转码
		//Request[strs[i].split("=")[0]] => Request["type"]="1";
    }
	//Request => {"type":"1", "oper":'add'}
}