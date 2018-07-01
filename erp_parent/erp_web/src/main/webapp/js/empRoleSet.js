$(function(){
	
	//用户表格
	$('#grid').datagrid({
		url : 'emp_list.action',
		columns : [ [
			  {field:'uuid',title:'编号',width:100},
			  {field:'name',title:'名称',width:100}
		] ],
		singleSelect : true,
		onClickRow:function(rowIndex, rowData){
			$('#tree').tree({
				checkbox:true,//定义是否在每一个借点之前都显示复选框
				animate:true,//定义节点在展开或折叠的时候是否显示动画效果。
				url:'emp_readEmpRoles.action?id=' + rowData.uuid
			});
		}
	});
	
	$('#btnSave').bind('click',function(){
		// 获取所有选中的节点
		var nodes = $('#tree').tree('getChecked');
		var ids = [];//菜单编号数组
		$.each(nodes, function(i,node){
			ids.push(node.id);//菜单的编号
		});
		//alert(ids.toString());//将 Array 的元素转换为字符串。结果字符串由逗号分隔，且连接起来。
		
		//获取选中的用户
		var row = $('#grid').datagrid('getSelected');
		
		$.ajax({
			url : 'emp_updateEmpRoles.action',
			data : {id:row.uuid,ids:ids.toString()},
			dataType : 'json',
			type : 'post',
			success : function(rtn) {
				$.messager.alert('提示', rtn.message, 'info');
			}
		});
	});
});
