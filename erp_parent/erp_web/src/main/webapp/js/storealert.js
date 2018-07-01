$(function(){
	$('#grid').datagrid({
		url:'storedetail_storealertList.action',
		columns:[[
		    {field:'uuid',title:'商品编号',width:100},
		    {field:'name',title:'商品名称',width:100},
		    {field:'storenum',title:'库存数量',width:100},
		    {field:'outnum',title:'待发货数量',width:100}
		]],
		singleSelect:true,
		toolbar:[
		     {
		    	 text:'发送预警邮件',
		    	 iconCls:'icon-alert',
		    	 handler:function(){
		    		 $.ajax({
						url : 'storedetail_sendStorealertMail.action',
						dataType : 'json',
						type : 'post',
						success : function(rtn) {
							$.messager.alert('提示', rtn.message, 'info');
						}
					});
		    	 }
		     }
		]
	});
});