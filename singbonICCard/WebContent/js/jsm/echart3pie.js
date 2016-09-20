require.config({
	paths : {
		echarts : 'js'
	}
});
require(
		[ 'echarts', 'echarts/chart/bar', 'echarts/chart/line',
				'echarts/chart/pie', 'echarts/chart/funnel', ],
		function(ec) {
			/*
			 * alert(window.screen.height); alert(window.screen.width);
			 * alert(window.screen.availHeight);
			 * alert(window.screen.availWidth);
			 */
			var onedp = document.getElementById("twodp").value.replace('[', '')
					.replace(']', '').split(',');
			var onedpfare = document.getElementById("twofare").value;

			var indexdata = {
				category : document.getElementById("twodp").value.replace('[',
						'').replace(']', '').split(','),
				total : document.getElementById("twofare").value.replace('[',
						'').replace(']', '').split(',')
			};

			var labelFromatter = {
				normal : {
					labelLine : {
						length : 5,
					/*lineStyle:{
						type:'solid'	//线条样可选'solid''dotted''dashed'树图还可以选：'curve' | 'broken'
					},*/
					},
					label : {
						/*
						 * formatter : function (params){ return params.value; },
						 */
						formatter : '{b} : \n{c} ({d}%)',
						textStyle : {
							baseline : 'top'
						}
					}
				},
			};

			var myChart = ec.init(document.getElementById('main'), 'macarons');
			option = {
				title : {
					show : true,
					text : '部门营业额',
					link : '',
					target : 'blank',
					textStyle : {
						color : 'black',
						fontStyle : 'normal',
						fontWeight : 'normal',
						fontFamily : 'sans-serief',
						fontSize : 18,
					},
					subtext : '兴邦科技',
					sublink : 'http://www.singbon.com/',
					subtarget : 'blank',
					subtextStyle : {
						color : '#aaa',
						fontStyle : 'normal',
						fontWeight : 'normal',
						fontFamily : 'sans-serief',
						fontSize : 12,
					},
					padding : 5,
					//itemGap : 5,
					x : 'center',
					backgroundColor : 'transparent',
					borderColor : '#ccc',
					borderWidth : 0,
					itemStyle : {
						normal : {
							shadowColor : 'rgba(0, 0, 0, 0.5)',
							shadowBlur : 10
						}
					},
				},
				tooltip : {},

				toolbox : {
					//itemGap : 10,
					itemSize : 20,
					show : true,
					orient : 'vertical', // 布局方式，默认为水平布局，可选为'horizontal' ¦
					// 'vertical'
					x : 'left', // 水平安放位置，默认为全图右对齐，可选为：'center' ¦ 'left' ¦
					// 'right'¦ {number}（x坐标，单位px）
					y : 'center', // 垂直安放位置，默认为全图顶端，可选为：'top' ¦ 'bottom' ¦
					// 'center'¦ {number}（y坐标，单位px）
					// color : ['#1e90ff','#22bb22','#4b0082','#d2691e'],
					backgroundColor : 'rgba(0,0,0,0)', // 工具箱背景颜色
					borderColor : '#ccc', // 工具箱边框颜色
					borderWidth : 2, // 工具箱边框线宽，单位px，默认为0（无边框）
					padding : 5, // 工具箱内边距，单位px，默认各方向内边距为5，
					showTitle : true,
					feature : {
						
						mark : {
							show : true,
							title : {
								mark : '辅助线-开关',
								markUndo : '辅助线-删除',
								markClear : '辅助线-清空'
							},
						},
						dataZoom : {

							show : true,
							title : {
								dataZoom : '区域缩放',
								dataZoomReset : '区域缩放-后退'
							}
						},
						magicType : {
							show : true,
							type : [ /*'line', 'bar',*/'pie', 'funnel' ],
						},
						restore : {
							show : true,
							title : '还原',
							color : 'black'
						},
						saveAsImage : {
							show : true,
							title : '保存为图片',
							type : 'jpeg',
							lang : [ '点击本地保存' ]
						},
						myTools : {
							show : true,
							title : '有疑问？',
							icon : 'image://img/echart/question_mark_16.png',
							onclick : function() {
								window
										.open("http://www.singbon.com/article/article_18342.html");
							}
						},
						dataView : {
							show : false,
							readOnly : true
						},

					}
				},
				calculable : true,
				legend : {
					orient : 'vertical', // 'vertical' 'horizontal'
					x : 'right', // 'center' | 'left' | {number},
					//y : '10%', // 'center' | 'bottom' | {number}
					// x2:'10%',
					// padding : [5, 0, 5, 20],
					/*
					 * backgroundColor: '#eee', borderColor: '#CCCCCC',
					 * borderWidth: 1,
					 */
					// itemGap : 10,
					// itemWidth:5,
					// itemHeight:10,
					textStyle : {
						color : 'black',
						align : 'left',
						baseline : 'top'
					},
					data : onedp
				},

				series : [ {
					name : '面积模式',
					type : 'pie',
					radius : [ 110, 210 ],
					center : [ '48%', '55%' ],
					//roseType : 'area',
					x : '50%',
					/*y :'40%',
					x2: '60%',
					y2 : '70%',*/
					width : '2%',
					minSize : '0%',
					maxSize : '10%',
					sort : 'descending', // 'ascending', 'descending'
					gap : 10,
					
					itemStyle : labelFromatter,
					tooltip : {
						trigger : 'item',
						formatter : '{b}<br/>{c}({d}%)'
					},
					data : (function() {

						var res = [];
						var len = indexdata.total.length;
						while (len--) {
							res.push({
								name : indexdata.category[len],
								value : indexdata.total[len],

							});
						}
						return res;
					})()
				}, ]
			};
			myChart.setOption(option);
		});
