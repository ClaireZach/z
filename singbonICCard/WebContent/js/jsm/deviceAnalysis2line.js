require.config({
	paths : {
		echarts : 'js'
	}
});

require(
		[ 'echarts', 'echarts/chart/bar', 'echarts/chart/line',
				'echarts/chart/pie', 'echarts/theme/macarons', ],

		function(ec) {

			var myChart = ec.init(document.getElementById('main'), 'macarons');
			var option = {
				title : {
					/*
					 * text: '部门营业额', subtext: '兴邦科技'
					 */
					show : true,
					text : '终端营业额',
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
					itemGap : 5,
					zlevel : 0,
					z : 2,
					/*
					 * left: '50', top: 'auto', right: 'auto', bottom: 'auto',
					 */
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
				tooltip : {	},

				grid : {
					x:35,
					//y:'21%',
					//x2:'30%',
					y2:'4%',
					borderWidth :0,
					borderColor:'black'
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
					itemGap : 10,
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
							title : {
							},
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
							icon : 'img/echart/question_mark_16.png',
							onclick : function() {
								window
										.open("http://www.singbon.com/article/article_18342.html");
							}
						},

					}
				},
				tooltip : {
					show : true,
					axisPointer : {
						type : 'shadow'
					}

				},
				calculable : true,
				legend : {
					orient : 'horizontal', // 'vertical' 'horizontal'
					//x : '50', // 'center' | 'left' | {number},
					y : '10%', // 'center' | 'bottom' | {number}
					//x2:'10%',
					//padding : [5, 0, 5, 20],
					/*backgroundColor: '#eee',
			        borderColor: '#CCCCCC',
			        borderWidth: 1,*/
					//itemGap : 10,
					//itemWidth:5,
					//itemHeight:10,
					textStyle : {
						color : 'black',
						align : 'left',
						baseline : 'top'
					},
					data : ['终端']
				},
				xAxis : [ {
					type : 'category',
					show : false,
					name : '部门',
					axisLabel : {
						// rotate:60,
						// interval:0 ,
						margin : [ 10, 0, 0, 0 ],
					},
					axisTick : {
						show : true,
						inside : true,
						length : 2,
					},
					position : 'bottom',
					boundaryGap : true,
					name : '设备',
					data : (function() {
						var c = document.getElementById("dp").value.replace(
								'[', '').replace(']', '').split(",");
						return c;
					})(),
				} ],
				yAxis : [ {
					type : 'value',
					show : false,
					name : '营业额(元)',
					splitNumber : 8
				} ],
				series : [ {
					name : '终端',
					type : 'line',
					barGap:'50%',
					// barWidth:30,
					barMaxWidth : 40,
					// barCategoryGap:'90%',
					legendHoverLink : true,
					itemStyle : {
						normal : {
							color : '#60C0DD',
							label : {
								show : false,
								position : 'top',
								formatter : '{b}\n{c}(元)',
								textStyle : {

									fontSize : 10,
									color : 'black'
								}
							}
						}
					},
					data : (function() {
						var dpfare = document.getElementById("fare").value;
						return eval(dpfare);
					})(),
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
//					markPoint : {
//						symbol : 'circle', // 'circle', 'rectangle',
//						// 'triangle',
//						// 'diamond','emptyCircle',
//						// 'emptyRectangle',
//						// 'emptyTriangle', 'emptyDiamond'
//						effect : {
//							show : true,
//							type : 'scale', // type
//							// 特效类型，默认为'scale'（放大），可选还有'bounce'（跳动）
//							loop : true,
//							period : 15,
//							scaleSize : 0.5,
//							bounceDistance : 30,
//							color : '#60C0DD',
//							shadowColor : null,
//							shadowBlur : 0
//						},
//						data : [ {
//							type : 'max',
//							name : '最大值'
//						}, {
//							type : 'min',
//							name : '最小值'
//						} ]
//					},
				}, ]
			};
			myChart.setOption(option);
		});