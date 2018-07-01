 //品牌控制层 
app.controller('baseController' ,function($scope){	
	
    //重新加载列表 数据
    $scope.reloadList=function(){
    	//切换页码  
    	$scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);	   	
    }
    
	//分页控件配置 
	$scope.paginationConf = {
         currentPage: 1,
         totalItems: 10,
         itemsPerPage: 10,
         perPageOptions: [10, 20, 30, 40, 50],
         onChange: function(){
        	 $scope.reloadList();//重新加载
     	 }
	}; 
	
	$scope.selectIds=[];//选中的ID集合 

	//更新复选
	$scope.updateSelection = function($event, id) {		
		if($event.target.checked){//如果是被选中,则增加到数组
			$scope.selectIds.push( id);			
		}else{
			var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);//删除 
		}
	}

    /**
	 *返回 联想，华为，三星
     * @param jsonString 就是要提取的json字符串
     * @param key json中的对象中某一个属性的key
     */
	$scope.jsonToString=function (jsonString,key) {
		//[{},{}]
		var jsonObjectArray = angular.fromJson(jsonString);

		//var o = {key1:1,key2:2}
		//o.key1=3;o['key1']=3;
		var str="";
		for (var i=0;i<jsonObjectArray.length;i++){
			//{id,text}
            var obj = jsonObjectArray[i];
            str+=obj[key]+",";

		}
		if(str.length>=1){
            str = str.substring(0,str.length-1);
		}
		//console.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>"+str);
		return str;
    }

    /**
	 * [{"attributeName":"机身内存","attributeValue":["16G","32G"]}]
     * @param list  [{"attributeName":"机身内存","attributeValue":["16G","32G"]}]
     * @param key  attributeName
     * @param keyValue 机身内存
     */
    $scope.searchObjectByKey=function (list,key,keyValue) {
       for(var i=0;i<list.length;i++){
       		var object = list[i];
       		if(object[key]==keyValue){
       			return object;
			}
	   }
	   return null;
    }
});	