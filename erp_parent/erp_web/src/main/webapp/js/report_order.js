$(function(){
	$('#grid').datagrid({
		url:'report_orderReport.action',
		columns:[[
		    {field:'name',title:'商品类型',width:100},
		    {field:'y',title:'销售额',width:100}
		]],
		singleSelect:true,
		onLoadSuccess:function(data){
			//在数据加载成功的时候触发。
			//alert(JSON.stringify(data));
			showChart(data.rows);
		}
	});
	
	//点击查询按钮
	$('#btnSearch').bind('click',function(){
		//把表单数据转换成json对象
		var formData = $('#searchForm').serializeJSON();
		$('#grid').datagrid('load',formData);
	});
	
	
});

function showChart(_data){
	$('#chart').highcharts({
        chart: {
            type: 'pie',
            options3d: {
                enabled: true,
                alpha: 45,
                beta: 0
            }
        },
        title: {
            text: '销售统计图'
        },
        tooltip: {
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                depth: 35,
                dataLabels: {
                    enabled: true,
                    format: '{point.name}:<b>{point.percentage:.1f}%</b>'
                },
                showInLegend: true
            }
        },
        credits: {
        	href:"http://www.itheima.com",
        	text: "itheima.com"
        },
        series: [{
            type: 'pie',
            name: '百分比',
            data: _data
        }]
    });
}