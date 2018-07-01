//业务类型标记
var oper = Request.oper;
//标记订单类型
var type = Request.type * 1;
var btnText = "";//按钮的文本
$(function(){
	var title = "";
	if(type == 1){
		btnText = '采购申请';
		$('#addOrdersSupplier').html("供应商");
		title = "入库";
	}
	if(type == 2){
		btnText = '销售订单录入';
		$('#addOrdersSupplier').html("客户");
		title = "出库";
	}
	//初始化订单表格
	initOrdersGrid();
	
	//初始化订单详情窗口
	initOrdersDlg();
	
	//入库窗口
	$('#itemDlg').dialog({
		title:title,
		width:300,
		height:200,
		closed:true,
		modal:true,
		buttons:[
		     {
		    	 text:title,
		    	 iconCls:'icon-save',
		    	 handler:doInOutStore
		     }
		]
	});
	
	//初始化添加订单窗口
	initAddOrdersDlg();
	
	//物流详情窗口
	$('#waybillDlg').dialog({
		title:'物流详情',
		width:500,
		height:300,
		closed:true,
		modal:true
	});
});

/**
 * 日期格式化器
 * @param value
 * @returns
 */
function formatDate(value){
	if(value){
		return new Date(value).Format('yyyy-MM-dd');
	}
	return "";
}

/**
 * 订单状态格式化器
 * @param value
 */
function formatState(value){
	//采购: 0:未审核 1:已审核, 2:已确认, 3:已入库；
	//销售：0:未出库 1:已出库
	if(type == 1){
		switch(value * 1){
			case 0: return '未审核';
			case 1: return '已审核';
			case 2: return '已确认';
			case 3: return '已入库';
			default: return '';
		}
	}
	if(type == 2){
		switch(value * 1){
			case 0: return '未出库';
			case 1: return '已出库';
			default: return '';
		}
	}
}

/**
 * 明细状态格式化器
 * @param value
 */
function formatDetailState(value){
	if(type == 1){
		switch(value * 1){
			case 0: return '未入库';
			case 1: return '已入库';
			default: return '';
		}
	}
	if(type == 2){
		switch(value * 1){
			case 0: return '未出库';
			case 1: return '已出库';
			default: return '';
		}
	}
	
}

/**
 * 初始化订单详情窗口
 */
function initOrdersDlg(){
	//订单详情窗口的配置信息
	var ordersDlgCfg = {
			title:'订单详情',
			width:700,
			height:340,
			closed:true,
			modal:true
		};	
	//订单详情窗口的顶部工具栏 
	var ordersDlgCfgToolbar  = [];
	//明细表格的配置信息
	var itemgridCfg = {
			title:'商品明细',
			columns : [ [
				{field:'uuid',title:'编号',width:60},
				{field:'goodsuuid',title:'商品编号',width:100},
				{field:'goodsname',title:'商品名称',width:100},
				{field:'price',title:'价格',width:60},
				{field:'num',title:'数量',width:60},
				{field:'money',title:'金额',width:100},
				{field:'state',title:'状态',width:60,formatter:formatDetailState}
			] ],
			singleSelect : true
		};
	
	//如果是审核业务
	if(oper == 'doCheck'){
		//加入审核按钮
		ordersDlgCfgToolbar.push({
			text:'审核',
			iconCls:'icon-search',
			handler:doCheck
		});
	}
	//如果是确认业务
	if(oper == 'doStart'){
		//加入确认按钮
		ordersDlgCfgToolbar.push({
			text:'确认',
			iconCls:'icon-search',
			handler:doStart
		});
	}
	
	//如果是入库业务
	if(oper == 'doInStore' || oper == 'doOutStore'){
		//双击事件
		itemgridCfg.onDblClickRow = function(rowIndex, rowData){
			//打开入库的窗口
			$('#itemDlg').dialog('open');
			//入库窗口赋值
			$('#id').val(rowData.uuid);
			$('#goodsuuid').html(rowData.goodsuuid);
			$('#goodsname').html(rowData.goodsname);
			$('#num').html(rowData.num);
		}
	}
	
	//导出按钮
	ordersDlgCfgToolbar.push({
		text:'导出',
		iconCls:'icon-excel',
		handler:function(){
			$.download('orders_exportById.action',{id:$('#uuid').html()});
		}
	});
	
	//物流路径信息按钮
	ordersDlgCfgToolbar.push({
		text:'物流详情',
		iconCls:'icon-search',
		handler:function(){
			var waybillsn = $('#waybillsn').html();
			if(!waybillsn){
				$.messager.alert('提示', "没有物流信息", 'info');
				return;
			}
			
			//弹出物流详情窗口
			$('#waybillDlg').dialog('open');
			$('#waybillgrid').datagrid({
				url:'orders_waybilldetailList.action?waybillsn=' + $('#waybillsn').html(),
				columns:[[
					{field:'exedate',title:'执行日期',width:100},
					{field:'exetime',title:'执行时间',width:100},
					{field:'info',title:'执行信息',width:100}
				]],
				singleSelect:true
			});
		}
	});
	
	//订单详情窗口的顶部工具栏 有内容，长度>0
	if(ordersDlgCfgToolbar.length > 0){
		//显示工具栏
		ordersDlgCfg.toolbar = ordersDlgCfgToolbar;
	}
	
	//订单详情窗口
	$('#ordersDlg').dialog(ordersDlgCfg);
	
	//明细表格
	$('#itemgrid').datagrid(itemgridCfg);
	
}


/**
 * 审核
 */
function doCheck(){
	$.messager.confirm('确认',"确认要审核吗?",function(yes){
		if(yes){
			$.ajax({
				url : 'orders_doCheck.action',
				data : {id:$('#uuid').html()},
				dataType : 'json',
				type : 'post',
				success : function(rtn) {
					$.messager.alert('提示', rtn.message, 'info', function() {
						if(rtn.success){
							//关闭窗口
							$('#ordersDlg').dialog('close');
							//刷新表格
							$('#grid').datagrid('reload');
						}
					});
				}
			});
		}
	});
}

/**
 * 确认
 */
function doStart(){
	$.messager.confirm('确认',"确定要确认吗?",function(yes){
		if(yes){
			$.ajax({
				url : 'orders_doStart.action',
				data : {id:$('#uuid').html()},
				dataType : 'json',
				type : 'post',
				success : function(rtn) {
					$.messager.alert('提示', rtn.message, 'info', function() {
						if(rtn.success){
							//关闭窗口
							$('#ordersDlg').dialog('close');
							//刷新表格
							$('#grid').datagrid('reload');
						}
					});
				}
			});
		}
	});
}

/**
 * 出入库
 */
function doInOutStore(){
	var url = 'orderdetail_doInStore.action'
	var msg = "确定要入库吗？";
	if(type == 2){
		msg = "确定要出库吗？";
		url = 'orderdetail_doOutStore.action'
	}
	$.messager.confirm('确认',msg,function(yes){
		if(yes){
			var submitData = $('#itemForm').serializeJSON();
			$.ajax({
				url : url,
				data : submitData,
				dataType : 'json',
				type : 'post',
				success : function(rtn) {
					$.messager.alert('提示', rtn.message, 'info', function() {
						if(rtn.success){
							//关闭入库窗口
							$('#itemDlg').dialog('close');
							//获取选中的行
							var row = $('#itemgrid').datagrid('getSelected');
							//更新明细的状态,
							row.state= '1';
							
							//刷新明细表格,data:{total:,rows:[]}
							var data = $('#itemgrid').datagrid('getData');
							//更新明细的状态
							$('#itemgrid').datagrid('loadData',data);
							
							var flg = true;
							//循环判断明细的状态
							$.each(data.rows, function(i,r){
								if(r.state * 1 == 0){
									//有明细没有入库
									flg = false;
									return false;//退出循环 break;
								}
							});
							
							if(flg==true){
								//关闭订单详情窗口
								$('#ordersDlg').dialog('close');
								//刷新订单列表
								$('#grid').datagrid('reload');
							}
							
						}
					});
				}
			});
		}
	});
}


/**
 * 初始化订单表格
 */
function initOrdersGrid(){
	//订单表格的配置信息
	var ordersgridCfg={
		title:'订单列表',
		url : getOrdersGridUrl(),//采购订单
		columns : getGridColumns(),
		singleSelect : true,
		pagination : true,
		onDblClickRow:ordersGridDblClickRow//事件，接收的类型是方法
	};
	if(oper == 'myorders'){
		ordersgridCfg.toolbar = [
		     {
		    	 text:btnText,
		    	 iconCls:'icon-add',
		    	 handler:function(){
		    		 //打开 采购申请窗口
		    		 $('#addOrdersDlg').dialog('open');
		    	 }
		     }
		];
	}
	//订单列表
	$('#grid').datagrid(ordersgridCfg);
	
}

/**
 * 获取订单表格列的定义
 * @returns {Array}
 */
function getGridColumns(){
	if(type == 1){//采购
		return [ [
			{field:'uuid',title:'编号',width:100},
			{field:'createtime',title:'生成日期',width:100,formatter:formatDate},
			{field:'checktime',title:'审核日期',width:100,formatter:formatDate},
			{field:'starttime',title:'确认日期',width:100,formatter:formatDate},
			{field:'endtime',title:'入库日期',width:100,formatter:formatDate},
			{field:'createrName',title:'下单员',width:100},
			{field:'checkerName',title:'审核员',width:100},
			{field:'starterName',title:'采购员',width:100},
			{field:'enderName',title:'库管员',width:100},
			{field:'supplierName',title:'供应商',width:100},
			{field:'totalmoney',title:'合计金额',width:100},
			{field:'state',title:'状态',width:100,formatter:formatState},
			{field:'waybillsn',title:'运单号',width:100}
		] ];
	}
	
	if(type == 2){//销售
		return [ [
			{field:'uuid',title:'编号',width:100},
			{field:'createtime',title:'生成日期',width:100,formatter:formatDate},
			{field:'endtime',title:'入库日期',width:100,formatter:formatDate},
			{field:'createrName',title:'下单员',width:100},
			{field:'enderName',title:'库管员',width:100},
			{field:'supplierName',title:'客户',width:100},
			{field:'totalmoney',title:'合计金额',width:100},
			{field:'state',title:'状态',width:100,formatter:formatState},
			{field:'waybillsn',title:'运单号',width:100}
		] ];
	}
	
}

/**
 * 订单表格的双击行事件
 * @param rowIndex
 * @param rowData
 */
function ordersGridDblClickRow(rowIndex, rowData){
	/*  在用户双击一行的时候触发，参数包括：
		rowIndex：点击的行的索引值，该索引值从0开始。
		rowData：对应于点击行的记录。
	 */
	$('#ordersDlg').dialog('open');
	//订单的信息
	$('#uuid').html(rowData.uuid);
	$('#createtime').html(formatDate(rowData.createtime));
	$('#checktime').html(formatDate(rowData.checktime));
	$('#starttime').html(formatDate(rowData.starttime));
	$('#endtime').html(formatDate(rowData.endtime));
	$('#createrName').html(rowData.createrName);
	$('#checkerName').html(rowData.checkerName);
	$('#starterName').html(rowData.starterName);
	$('#enderName').html(rowData.enderName);
	$('#supplierName').html(rowData.supplierName);
	$('#state').html(formatState(rowData.state));
	
	//运单号
	$('#waybillsn').html(rowData.waybillsn);
	//加载明细的数据
	$('#itemgrid').datagrid('loadData',rowData.orderdetails);
}

/**
 * 订单表格的url
 * @returns {String}
 */
function getOrdersGridUrl(){
	var url = 'orders_listByPage.action?t1.type=1';
	switch(oper){
		case "doCheck": 
			url = 'orders_listByPage.action?t1.type=1&t1.state=0';
			document.title="订单审核";
			break;
		case "doStart": 
			url = 'orders_listByPage.action?t1.type=1&t1.state=1';
			document.title="订单确认";
			break;
		case "doInStore": 
			url = 'orders_listByPage.action?t1.type=1&t1.state=2';
			document.title="采购订单入库";
			break;
		case "doOutStore": 
			url = 'orders_listByPage.action?t1.type=2&t1.state=0';
			document.title="销售订单出库";
			break;
		case "myorders": 
			url = 'orders_myListByPage.action?t1.type=' + type;
			document.title=btnText;
			break;
		case "orders": 
			url = 'orders_listByPage.action?t1.type=' + type;
			if(type == 1){
				document.title="采购订单查询";
			}
			if(type == 2){
				document.title="销售订单查询";
			}
			break;
		default: 
			break;
	}
	return url;
}

/**
 * 初始化添加订单窗口
 */
function initAddOrdersDlg(){
	
	$('#addOrdersDlg').dialog({
		title:btnText,
		width:700,
		height:400,
		closed:true,
		modal:true
	});
}
