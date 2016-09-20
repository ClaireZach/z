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
						lineStyle:{
							type:'solid'	//线条样可选'solid''dotted''dashed'树图还可以选：'curve' | 'broken'
						},
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
				grid : {
					x : 35,
					y : '21%',
					x2 : '35%',
					y2 : '4%',
					borderWidth : 0,
					borderColor : 'black'
				},
				dataZoom : {
					show : true,
					realtime : false,
					orient : 'horizontal',
					realtime : true,
					// y : 445,
					showDetail : true,
					height : 20,
					//backgroundColor : '#60C0DD',
					//dataBackgroundColor : '#60C0DD',
					//fillerColor : '#60C0DD',
					//handleColor : 'black',
					start : 0,
					end : 100
				},
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
							type : [ 'line', 'bar', /* 'pie', 'funnel' */],
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
					orient : 'horizontal', // 'vertical' 'horizontal'
					// x : '50', // 'center' | 'left' | {number},
					y : '10%', // 'center' | 'bottom' | {number}
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
				xAxis : [ {
					type : 'category',
					show : false,
					name : '部门',
					splitLine : {
						show : true
					},
					//boundaryGap : [ 500, 500 ],
					axisTick : {
						show : true,
						inside : true,
						length : 2,
					},
					axisLabel : {
						show : true,
						margin : 20,
					},
					data : (function() {
						var c = document.getElementById("twodp").value.replace(
								'[', '').replace(']', '').split(",");

						// c.push(" ");c.push(" ");c.push(" ");c.push(" ");
						return c;
					})(),
				} ],
				yAxis : [ {
					type : 'value',
					show : false,
					name : '营业额(元)',
				} ],
				series : [ {
					name : '部门营业额',
					type : 'bar',
					//barGap : '50%',
					barMaxWidth : 40,
					legendHoverLink : true,
					markPoint : {
						symbol : 'circle',
						effect : {
							show : true,
							type : 'scale', // 默认'scale'（放大）还有'bounce'（跳动）
							loop : true,
							period : 15,
							scaleSize : 0.5,
							bounceDistance : 30,
							color : '#60C0DD',
							shadowColor : null,
							shadowBlur : 0
						},
						data : [ {
							type : 'max',
							name : '最大值'
						}, {
							type : 'min',
							name : '最小值'
						} ]
					},
					markLine : {
						symbol : 'emptyCircle',
						symbolSize : [ 0, 0 ],
						smooth : false,
						effect : {
							show : false,
							scaleSize : 1,
							period : 30,
							color : '#fff',
							shadowBlur : 10
						},
						itemStyle : {
							normal : {
								/*label : {
									show : true,
								},*/
								label : {
									show : true,
									position : 'right',
									formatter : '{b}\n{c}',
									textStyle : {
										fontSize : 4,
										color : 'black'
									}
								},
								color : '#60C0DD',
								borderWidth : 2,
								lineStyle : {
									type : 'solid',
									shadowBlur : 10
								}
							}
						},
						data : [ {
							type : 'average',
							name : '平均值'
						} ]
					},
					itemStyle : {
						normal : {
							color : '#60C0DD',
							label : {
								show : true,
								position : 'top',
								formatter : '{b}\n{c}',
								textStyle : {
									fontSize :4,
									color : 'black'
								}
							}
						}
					},
					data : (function() {
						var dpfare = document.getElementById("twofare").value;
						return eval(dpfare);
					})(),
				}, {
					name : '面积模式',
					type : 'pie',
					radius : [ 20, 75 ],
					center : [ '83%', '45%' ],
					roseType : 'area',
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
