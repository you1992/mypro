//当前编辑行的索引
var existEditIndex=-1;

$(function(){
	$('#ordersgrid').datagrid({
		columns:[[
			{field:'goodsuuid',title:'商品编号',width:100,editor:{
				type:'numberbox',
				options:{
					disabled:true
				}
			}},
			{field:'goodsname',title:'商品名称',width:100,editor:{
				type:'combobox',
				options:{
					url:'goods_list.action',
					valueField:'name',//数据库要保存的是商品的名称
					textField:'name',
					onSelect:function(goods){
						//在用户选中某一项时触发，选中的是一个商品
						//alert(JSON.stringify(goods));
						var price = goods.inprice;//采购价
						var goodsuuid = goods.uuid;//商品编号
						//获取指定单元格的编辑器,options包含2个属性：
						//index：行索引。field：字段名称。
						var goodsuuidEditor = getEditor('goodsuuid');
						//$(goodsuuidEditor.target).numberbox('setValue',goodsuuid);
						$(goodsuuidEditor.target).val(goodsuuid);
						
						var priceEditor = getEditor('price');
						$(priceEditor.target).numberbox('setValue',price);
						//$(priceEditor.target).val(price);
						
						var numEditor = getEditor('num');
						$(numEditor.target).select();
						//计算金额
						cal();
						// 合计
						sum();
					}
				}
			}},
			{field:'price',title:'价格',width:100,editor:{
				type:'numberbox',
				options:{
					precision:2,min:0,prefix:'￥',disabled:true
				}
			},formatter:function(value){
				if(value){//value不为null,不为undefined
					//value有值
					return "￥" + (value*1).toFixed(2);//保留小数位
				}
			}},
			{field:'num',title:'数量',width:100,editor:'numberbox'},
			{field:'money',title:'金额',width:100,editor:{
				type:'numberbox',
				options:{
					precision:2,min:0,prefix:'￥',disabled:true
				}
			},formatter:function(value){
				if(value){//value不为null,不为undefined
					//value有值
					return "￥" + (value*1).toFixed(2);//保留小数位
				}
			}},
			{field:'-',title:'操作',width:100,formatter:function(value,row,index){
				if(row.num == '合计'){
					return;
				}
				return '<a href="javascript:void(0)" onclick="delRow(' + index + ')">删除</a>'
			}}
		]],
		singleSelect:true,//单选行，只能选中一行
		showFooter:true,//显示行脚
		toolbar: [{
			text:'新增',
			iconCls: 'icon-add',
			handler: function(){
				if(existEditIndex > -1){
					//存在编辑的行
					$('#ordersgrid').datagrid('endEdit',existEditIndex);
				}
				
				//追加一个新行。新行将被添加到最后的位置。 
				//参数:行的数据
				$('#ordersgrid').datagrid('appendRow',{num:0,money:0});
				//返回当前页的所有行。数组
				var rows = $('#ordersgrid').datagrid('getRows');
				//最后一行的下标
				var index = rows.length  - 1;
				existEditIndex = index;
				//开启编辑
				$('#ordersgrid').datagrid('beginEdit',existEditIndex);
				//绑定事件
				bindGridEvent();
			}
		},'-',{
			text:'提交',
			iconCls: 'icon-save',
			handler: function(){
				var submitData = $('#addOrdersForm').serializeJSON();
				//判断 是否有选中供应商 
				if(submitData['t.supplieruuid'] == ''){
					$.messager.alert('提示', "请选择供应商!", 'info');
					return;
				}

				if(existEditIndex > -1){
					//存在编辑的行
					$('#ordersgrid').datagrid('endEdit',existEditIndex);
				}
				//所有的行 商品信息
				var rows = $('#ordersgrid').datagrid('getRows');
				//转成字符串
				var jsonString = JSON.stringify(rows);
				//加入json属性，action中必须有一个属性名跟它一样s
				submitData.json = jsonString;
				submitData['t.type']=type;//订单的类型
				$.ajax({
					url : 'orders_add.action',
					data : submitData,
					dataType : 'json',
					type : 'post',
					success : function(rtn) {
						$.messager.alert('提示', rtn.message, 'info',function(){
							if(rtn.success){
								//清空商品明细列表
								$('#ordersgrid').datagrid('loadData',{total:0,rows:[],footer:[{num:'合计',money:0}]});
								//清除供应商选择
								$('#supplier').combogrid('clear');
								//关闭采购申请窗口, 
								 $('#addOrdersDlg').dialog('close');
								//刷新订单表格, 
								$('#grid').datagrid('reload')
							}
						});
						
					}
				});
			}
		}],
		onClickRow:function(rowIndex, rowData){
			//在用户点击一行的时候触发，参数包括：
			//rowIndex：点击的行的索引值，该索引值从0开始。
			//rowData：对应于点击行的记录。
			if(existEditIndex > -1){
				//存在编辑的行
				$('#ordersgrid').datagrid('endEdit',existEditIndex);
			}
			existEditIndex = rowIndex;
			//开启编辑
			$('#ordersgrid').datagrid('beginEdit',existEditIndex);
			//绑定事件
			bindGridEvent();
		}
	});
	
	//加载行脚数据
	$('#ordersgrid').datagrid('reloadFooter',[{num:'合计',money:0}]);
	
	//供应商下拉表格
	$('#supplier').combogrid({    
	    panelWidth:750,//宽度
	    //value:'006',//默认选中的值 
	    idField:'uuid',//提交的内容,combobox.valueField   
	    textField:'name',//显示的名称
	    mode:'remote',//用户输入将会发送到名为'q'的http请求参数，向服务器检索新的数据。
	    url:'supplier_list.action?t1.type=' + type,//从服务器获取数据 , t1.type=只查询供应商的信息
	    columns:[[    
			{field:'uuid',title:'编号',width:100},
			{field:'name',title:'名称',width:100},
			{field:'address',title:'联系地址',width:100},
			{field:'contact',title:'联系人',width:100},
			{field:'tele',title:'联系电话',width:100},
			{field:'email',title:'邮件地址',width:100}   
	    ]]
	});
});


/**
 * 获取编辑器
 * @param _field
 * @returns
 */
function getEditor(_field){
	return $('#ordersgrid').datagrid('getEditor', {index:existEditIndex,field:_field});
}

/**
 * 计算金额
 */
function cal(){
	var priceEditor = getEditor('price');
	//价格
	var price = $(priceEditor.target).numberbox('getValue');
	//数量
	var numEditor = getEditor('num');
	var num = $(numEditor.target).val();//必须要用val()来取值
	
	var money = price * num;
	money = money.toFixed(2);//保留2位小数，返回的字符串
	
	var moneyEditor = getEditor('money');
	$(moneyEditor.target).numberbox('setValue',money);
	
	//获取所有的行
	var rows = $('#ordersgrid').datagrid('getRows');
	//让金额进入datagrid rows中
	rows[existEditIndex].money = money;
	
}

/**
 * 合计
 */
function sum(){
	//获取所有的行
	var rows = $('#ordersgrid').datagrid('getRows');
	var totalmoney = 0;
	$.each(rows,function(i,row){
		totalmoney += row.money * 1;
	});
	//加载行脚数据
	$('#ordersgrid').datagrid('reloadFooter',[{num:'合计',money:totalmoney.toFixed(2)}]);
}

/**
 * 绑定事件
 */
function bindGridEvent(){
	//数量的编辑器
	var numEditor = getEditor('num');
	//数量输入框绑弹起事件 接收的类型为方法，传方法名就可以了
	$(numEditor.target).bind('keyup',function(){
		// 计算金额
		cal();
		// 合计
		sum();
	});
}

/**
 * 删除行
 */
function delRow(idx){
	if(existEditIndex > -1){
		//存在编辑的行
		$('#ordersgrid').datagrid('endEdit',existEditIndex);
	}
	
	$('#ordersgrid').datagrid('deleteRow',idx);
	
	//获取datagrid的数据
	var data = $('#ordersgrid').datagrid('getData');
	//加载数据, 加载本地数据，旧的行将被移除。
	$('#ordersgrid').datagrid('loadData',data);
	// 合计
	sum();
}