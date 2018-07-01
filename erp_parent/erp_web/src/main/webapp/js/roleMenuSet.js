$(function(){
	
	//角色表格
	$('#grid').datagrid({
		url : 'role_list.action',
		columns : [ [
			  {field:'uuid',title:'编号',width:100},
			  {field:'name',title:'名称',width:100}
		] ],
		singleSelect : true,
		onClickRow:function(rowIndex, rowData){
			$('#tree').tree({
				checkbox:true,//定义是否在每一个借点之前都显示复选框
				animate:true,//定义节点在展开或折叠的时候是否显示动画效果。
				url:'role_readRoleMenus.action?id=' + rowData.uuid
			});
		}
	});
	
	//id：节点ID，对加载远程数据很重要。
	//text：显示节点文本。
	//state：节点状态，'open' 或 'closed'，默认：'open'。如果为'closed'的时候，将不自动展开该节点。
	//checked：表示该节点是否被选中。
	//attributes: 被添加到节点的自定义属性。
	//children: 一个节点数组声明了若干节点。

	/*$('#tree').tree({
		checkbox:true,//定义是否在每一个借点之前都显示复选框
		animate:true,//定义节点在展开或折叠的时候是否显示动画效果。
		url:'role_readRoleMenus.action?id=1'
		data: [
		{
			text: '人事管理',
			children: [{
				text: '员工',
				id:'102',
				checked:true
			},{
				text: '部门'
			}]
		},{
			text: 'Item2',
			children: [{
				text: 'Item21'
			},{
				text: 'Item22'
			}]
		},{
			text: 'Item3'
		}]
	// list 对象 属性
	// text, children, id, checked
	});*/
	
	
	$('#btnSave').bind('click',function(){
		// 获取所有选中的节点
		var nodes = $('#tree').tree('getChecked');
		var ids = [];//菜单编号数组
		$.each(nodes, function(i,node){
			ids.push(node.id);//菜单的编号
		});
		//alert(ids.toString());//将 Array 的元素转换为字符串。结果字符串由逗号分隔，且连接起来。
		
		//获取选中的角色
		var row = $('#grid').datagrid('getSelected');
		
		$.ajax({
			url : 'role_updateRoleMenus.action',
			data : {id:row.uuid,ids:ids.toString()},
			dataType : 'json',
			type : 'post',
			success : function(rtn) {
				$.messager.alert('提示', rtn.message, 'info');
			}
		});
	});
});

function getChecked(){
	var nodes = $('#tree').tree('getChecked');
	alert(JSON.stringify(nodes));
}