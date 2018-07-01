$(function(){
	$('#grid').datagrid({
		url:'emp_listByPage',
		columns:[[
  		    {field:'uuid',title:'编号',width:100},
  		    {field:'username',title:'登陆名',width:100},
  		    {field:'name',title:'真实姓名',width:100},
  		    {field:'gender',title:'性别',width:100,formatter: function(value,row,index){
  		    	if(value * 1 == 1){//转成数值
  		    		return '男';
  		    	}
  		    	if(value * 1 == 0){
  		    		return '女';
  		    	}
  		    }},
  		    {field:'email',title:'邮件地址',width:100},
  		    {field:'tele',title:'联系电话',width:100},
  		    {field:'address',title:'联系地址',width:100},
  		    {field:'birthday',title:'出生年月日',width:100,formatter:function(value){
  		    	return new Date(value).Format('yyyy-MM-dd');
  		    }},
  		    {field:'dep',title:'部门',width:100,formatter:function(value){
  		    	return value.name;
  		    }},

			{field:'-',title:'操作',width:200,formatter: function(value,row,index){
				var oper = "<a href=\"javascript:void(0)\" onclick=\"updatePwd_reset(" + row.uuid + ')">重置密码</a>';
				return oper;
			}}
		]],
		singleSelect: true,
		pagination: true
	});
	
	$('#editDlg').dialog({
		title:'重置密码',
		width:260,
		heigth:120,
		closed:true,
		modal:true,
		buttons:[{
			text:'保存',
			iconCls: 'icon-save',
			handler:function(){
				//用记输入的部门信息
				var submitData= $('#editForm').serializeJSON();
				$.ajax({
					url: "emp_updatePwd_reset.action",
					data: submitData,
					dataType: 'json',
					type: 'post',
					success:function(rtn){
						//{success:true, message: 操作失败}
						$.messager.alert('提示',rtn.message, 'info',function(){
							if(rtn.success){
								//关闭弹出的窗口
								$('#editDlg').dialog('close');
							}
						});
					}
				});
			}
		}]
	});
});

/**
 * 打开重置密码窗口
 */
function updatePwd_reset(uuid){
	$('#editDlg').dialog('open');
	//设置员工编号
	$('#id').val(uuid);
}