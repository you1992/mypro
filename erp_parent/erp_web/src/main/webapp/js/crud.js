//提交的方法名称
var method = "";
var height = 200;
var listParam = "";
var saveParam = "";
$(function(){
	//加载表格数据
	$('#grid').datagrid({
		url:name + '_listByPage.action' + listParam,
		columns:columns,
		singleSelect: true,
		pagination: true,
		toolbar: [{
			text: '新增',
			iconCls: 'icon-add',
			handler: function(){
				//设置保存按钮提交的方法为add
				method = "add";
				//关闭编辑窗口
				$('#editDlg').dialog('open');
			}
		},'-',{
			text: '导出',
			iconCls: 'icon-excel',
			handler: function(){
				//下载的url，提交的参数加上类型
				var url = name + "_export.action" + listParam;
				//查询的条件
				var formData = $('#searchForm').serializeJSON();
				//下载
				$.download(url,formData);
			}
		},'-',{
			text: '导入',
			iconCls: 'icon-save',
			handler: function(){
				//打开导入窗口
				$('#importDlg').dialog('open');
			}
		}]
	});

	//点击查询按钮
	$('#btnSearch').bind('click',function(){
		//把表单数据转换成json对象
		var formData = $('#searchForm').serializeJSON();
		$('#grid').datagrid('load',formData);
	});

	//初始化编辑窗口
	$('#editDlg').dialog({
		title: '编辑',//窗口标题
		width: 300,//窗口宽度
		height: height,//窗口高度
		closed: true,//窗口是是否为关闭状态, true：表示关闭
		modal: true,//模式窗口
		buttons:[{
			text:'保存',
			iconCls: 'icon-save',
			handler:function(){
				if(!$('#editForm').form('validate')){
					return;
				}
				//用记输入的部门信息
				var submitData= $('#editForm').serializeJSON();
				$.ajax({
					url: name + '_' + method + saveParam,
					data: submitData,
					dataType: 'json',
					type: 'post',
					success:function(rtn){
						//{success:true, message: 操作失败}
						$.messager.alert('提示',rtn.message, 'info',function(){
							if(rtn.success){
								//关闭弹出的窗口
								$('#editDlg').dialog('close');
								//刷新表格
								$('#grid').datagrid('reload');
							}
						});
					}
				});
			}
		},{
			text:'关闭',
			iconCls:'icon-cancel',
			handler:function(){
				//关闭弹出的窗口
				$('#editDlg').dialog('close');
			}
		}]
	});

	//导入窗口
	$('#importDlg').dialog({
		title:'导入',
		width:340,
		height:106,
		closed:true,
		modal:true,
		buttons:[
		    {
		    	text:'导入',
		    	iconCls:'icon-save',
		    	handler:function(){
		    		$.ajax({
						url : 'supplier_doImport.action',
						data : new FormData($('#importForm')[0]),
						dataType : 'json',
						type : 'post',
						processData:false,//true,jquery把提交的数据做转成字符串处理，设置成false,不能转字符串，否则后端得到的是错误的文件
						contentType:false,//告诉服务端，不要转编码读取内容。保留字节流
						success : function(rtn) {
							$.messager.alert('提示', rtn.message, 'info',function() {
								if(rtn.success){
									//关闭导入窗口
									$('#importDlg').dialog('close');
									//刷新表格
									$('#grid').datagrid('reload');
								}
							});
						}
					});
		    	}
		    }
		]
	});
});


/**
 * 删除
 */
function del(uuid){
	$.messager.confirm("确认","确认要删除吗？",function(yes){
		if(yes){
			$.ajax({
				url: name + '_delete?id=' + uuid,
				dataType: 'json',
				type: 'post',
				success:function(rtn){
					$.messager.alert("提示",rtn.message,'info',function(){
						//刷新表格数据
						$('#grid').datagrid('reload');
					});
				}
			});
		}
	});
}

/**
 * 修改
 */
function edit(uuid){
	//弹出窗口
	$('#editDlg').dialog('open');

	//清空表单内容
	$('#editForm').form('clear');

	//设置保存按钮提交的方法为update
	method = "update";

	//加载数据
	$('#editForm').form('load',name + '_get?id=' + uuid);
	//{"t.address":"asdf","t.birthday":"2018-01-02","t.dep":{"name":"总裁办","tele":"111111","uuid":2},"t.email":"asdfas","t.gender":1,"t.name":"asdfa","t.tele":"asdf","t.username":"guangyu","t.uuid":9}
	/*var data = {"t.address":"asdf","t.birthday":"2018-01-02","t.dep.name":"总裁办","t.dep.tele":"111111","t.dep.uuid":2,"t.email":"asdfas","t.gender":1,"t.name":"asdfa","t.tele":"asdf","t.username":"guangyu","t.uuid":9};
	$('#editForm').form('load',data);*/
}