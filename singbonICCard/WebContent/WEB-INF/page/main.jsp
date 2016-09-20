<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>${company.companyName}——IC卡管理系统</title>
<link href="themes/default/style.css" rel="stylesheet" type="text/css" media="screen" />
<link href="themes/css/core.css" rel="stylesheet" type="text/css" media="screen" />
<link href="themes/css/print.css" rel="stylesheet" type="text/css" media="print" />
<!--[if IE]>
<link href="themes/css/ieHack.css" rel="stylesheet" type="text/css" media="screen"/>
<![endif]-->

<!--[if lte IE 9]>
<script src="js/speedup.js" type="text/javascript"></script>
<![endif]-->

<script src="js/jquery-1.7.2.min.js" type="text/javascript"></script>
<script src="js/jquery.cookie.js" type="text/javascript"></script>
<script src="js/jquery.validate.js" type="text/javascript"></script>
<script src="js/jquery.bgiframe.js" type="text/javascript"></script>
<!-- <script src="xheditor/xheditor-1.2.1.min.js" type="text/javascript"></script> -->
<!-- <script src="xheditor/xheditor_lang/zh-cn.js" type="text/javascript"></script> -->

<!-- svg图表  supports Firefox 3.0+, Safari 3.0+, Chrome 5.0+, Opera 9.5+ and Internet Explorer 6.0+ -->
<!-- <script type="text/javascript" src="chart/raphael.js"></script> -->
<!-- <script type="text/javascript" src="chart/g.raphael.js"></script> -->
<!-- <script type="text/javascript" src="chart/g.bar.js"></script> -->
<!-- <script type="text/javascript" src="chart/g.line.js"></script> -->
<!-- <script type="text/javascript" src="chart/g.pie.js"></script> -->
<!-- <script type="text/javascript" src="chart/g.dot.js"></script> -->

<script src="js/dwz.core.js" type="text/javascript"></script>
<script src="js/dwz.util.date.js" type="text/javascript"></script>
<script src="js/dwz.validate.method.js" type="text/javascript"></script>
<script src="js/dwz.regional.zh.js" type="text/javascript"></script>
<script src="js/dwz.barDrag.js" type="text/javascript"></script>
<script src="js/dwz.drag.js" type="text/javascript"></script>
<script src="js/dwz.tree.js" type="text/javascript"></script>
<script src="js/dwz.accordion.js" type="text/javascript"></script>
<script src="js/dwz.ui.js" type="text/javascript"></script>
<script src="js/dwz.theme.js" type="text/javascript"></script>
<script src="js/dwz.switchEnv.js" type="text/javascript"></script>
<script src="js/dwz.alertMsg.js" type="text/javascript"></script>
<script src="js/dwz.contextmenu.js" type="text/javascript"></script>
<script src="js/dwz.navTab.js" type="text/javascript"></script>
<script src="js/dwz.tab.js" type="text/javascript"></script>
<script src="js/dwz.resize.js" type="text/javascript"></script>
<script src="js/dwz.dialog.js" type="text/javascript"></script>
<script src="js/dwz.dialogDrag.js" type="text/javascript"></script>
<script src="js/dwz.sortDrag.js" type="text/javascript"></script>
<script src="js/dwz.cssTable.js" type="text/javascript"></script>
<script src="js/dwz.stable.js" type="text/javascript"></script>
<script src="js/dwz.taskBar.js" type="text/javascript"></script>
<script src="js/dwz.ajax.js" type="text/javascript"></script>
<script src="js/dwz.pagination.js" type="text/javascript"></script>
<script src="js/dwz.database.js" type="text/javascript"></script>
<script src="js/dwz.datepicker.js" type="text/javascript"></script>
<script src="js/dwz.effects.js" type="text/javascript"></script>
<script src="js/dwz.panel.js" type="text/javascript"></script>
<script src="js/dwz.checkbox.js" type="text/javascript"></script>
<script src="js/dwz.history.js" type="text/javascript"></script>
<script src="js/dwz.combox.js" type="text/javascript"></script>
<script src="js/dwz.print.js" type="text/javascript"></script>
<!-- <script src="/js/comet4j.js" type="text/javascript"></script> -->
<script src="/js/jquery.contextmenu.r2.js" type="text/javascript"></script>
<script src="/js/jQuery.Hz2Py-min.js" type="text/javascript"></script>
<script src="/js/common.js" type="text/javascript"></script>
<script src="/js/jquery.timers.js" type="text/javascript"></script>
<script src="/js/jquery.tablesorter.js" type="text/javascript"></script>
<script src="/js/jquery.tablesorter.widgets.js" type="text/javascript"></script>
<script src="/js/echarts.js"></script>
<!--
<script src="bin/dwz.min.js" type="text/javascript"></script>
-->
<script src="js/dwz.regional.zh.js" type="text/javascript"></script>
<script type="text/javascript">
	$(function() {
		$.cookie('companyName', '${sessionScope.company.companyName}', {
			expires : 7
		});

		DWZ.init('dwz.frag.xml', {
			loginUrl : 'login_dialog.html',
			loginTitle : '登录', // 弹出登录对话框
			//		loginUrl:'login.html',	// 跳到登录页面
			statusCode : {
				ok : 200,
				error : 300,
				timeout : 301
			}, //【可选】
			pageInfo : {
				pageNum : 'pageNum',
				numPerPage : 'numPerPage',
				orderField : 'orderField',
				orderDirection : 'orderDirection'
			}, //【可选】
			debug : false, // 调试模式 【true|false】
			callback : function() {
				initEnv();
				$('#themeList').theme({
					themeBase : 'themes'
				}); // themeBase 相对于index页面的主题base路径
				if('${diffDays}'!=''){
					alertMsg.warn('剩余试用${diffDays}天！');
				}
			}
		});
	});
	
	function editPwd(){
		var options = {
			width : 300,
			height : 200,
			max : false,
			mask : true,
			mixable : false,
			minable : false,
			resizable : true,
			drawable : true,
			fresh : false
		};
		$.pdialog.open('/systemManager/sysUser/editPwd.do', 'dialog', '修改密码', options);
	}
</script>
</head>
<body scroll="no">
	<div id="layout">
		<div id="header">
			<div class="headerNav">
				<a class="logo" href="#" style="width: 210px;">标志</a>
				<div style="margin: 30px 0 0; float: left; color: #15428B; font-size: 11px; font-weight: bold;">欢迎您：${sessionScope.sysUser.loginName}</div>
			</div>
		</div>
		<div id="leftside">
			<div id="sidebar_s">
				<div class="collapse">
					<div class="toggleCollapse">
						<div></div>
					</div>
				</div>
			</div>
			<div id="sidebar">
				<div class="toggleCollapse">
					<h2>主菜单</h2>
					<a style="padding: 7px 0 0 20px;float: left;color: #15428B;font-size: 11px;font-weight: bold;" href="javascript:;" onclick="editPwd();" title="修改密码"><span>修改密码</span></a>
					<a style="padding: 7px 0 0 20px; float: left; color: #15428B; font-size: 11px; font-weight: bold;" href="/loginout.do">退出</a>
					<div>收缩</div>
				</div>
				<div class="accordion" fillSpace="sidebar">
					<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_SYSTEMMANAGER">
						<div class="accordionHeader">
							<h2>
<!-- 								<span>Folder</span> <a href="/systemManager/index.do" target="navTab">系统管理</a> -->
								<span>Folder</span>系统管理
							</h2>
						</div>
						<div class="accordionContent">
							<ul class="tree">
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_SYSTEMSETTING">
									<li><a href="/systemManager/systemSetting/index.do" target="navTab">系统设置</a></li>
								</security:authorize>
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_DEVICEMANAGER">
									<li><a href="/systemManager/deviceManager/index.do" target="navTab">设备管理</a></li>
								</security:authorize>
<%-- 								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_SYSUSER"> --%>
<%-- 								<security:authorize ifAnyGranted="ROLE_ADMIN"> --%>
									<c:if test="${sessionScope.sysUser.loginName eq 'admin'}">
									<li><a href="/systemManager/sysUser/index.do" target="navTab">用户管理</a></li>
									</c:if>
<%-- 								</security:authorize> --%>
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_USERROLES">
									<li><a href="/systemManager/userRoles/index.do" target="navTab">授权管理</a></li>
								</security:authorize>
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_SUBSIDY">
									<li><a href="/systemManager/subsidy/index.do" target="navTab">补助管理</a></li>
								</security:authorize>
<%-- 								<security:authorize ifAnyGranted="ROLE_ADMIN"> --%>
									<c:if test="${sessionScope.sysUser.loginName eq 'admin'}">
									<li><a href="/systemManager/smartSchool/index.do" target="navTab">智慧校园</a></li>
									</c:if>
<%-- 								</security:authorize> --%>
							</ul>
						</div>
					</security:authorize>
					<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_CARDMANAGER">
						<div class="accordionHeader">
							<h2>
								<span>Folder</span>制卡中心
							</h2>
						</div>
						<div class="accordionContent">
							<ul class="tree">
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_MAINCARD">
									<li><a href="/cardManager/mainCard/index.do" target="navTab">用户卡管理</a></li>
								</security:authorize>
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_FUNCCARD">
									<li><a url="/cardManager/funcCard/index.do" target="navTab">功能卡制作</a></li>
								</security:authorize>
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_CARDREADER_PARAM">
									<li><a url="/cardManager/cardReader/param.do" target="navTab">读卡机参数下载</a></li>
								</security:authorize>
								<c:if test="${sessionScope.sysUser.loginName=='admin'}">
									<li><a url="/cardManager/cardReader/upgrade.do" target="navTab">读卡机升级</a></li>
								</c:if>
							</ul>
						</div>
					</security:authorize>
					<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ACCOUNTCENTER">
						<div class="accordionHeader">
							<h2>
								<span>Folder</span>结算中心
							</h2>
						</div>
						<div class="accordionContent">
							<ul class="tree">
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ACCOUNTCENTER_USERINFO">
									<li><a href="/balanceCenter/userInfo/index.do" target="navTab">用户信息查询</a></li>
								</security:authorize>
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ACCOUNTCENTER_CONSUME">
									<li><a href="/balanceCenter/consumeRecord/index.do" target="navTab">消费记录查询</a></li>
								</security:authorize>
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ACCOUNTCENTER_CARDOP">
									<li><a href="/balanceCenter/cardRecord/index.do" target="navTab">卡操作记录查询</a></li>
								</security:authorize>
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ACCOUNTCENTER_PERSONAL">
								<!-- 陈梦 -->
									<li><a href="/balanceCenter/personRecord/index.do" target="navTab">个人记录查询</a></li>
								</security:authorize>
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ACCOUNTCENTER_PERSONALSTAT">
									<li><a href="/balanceCenter/personStat/index.do" target="navTab">个人统计查询</a></li>
								</security:authorize>
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ACCOUNTCENTER_SUBSIDY">
									<li><a href="/balanceCenter/subsidyRecord/index.do" target="navTab">补助记录查询</a></li>
								</security:authorize>
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ACCOUNTCENTER_SUBSIDYSTAT">
									<li><a href="/balanceCenter/subsidyStat/index.do" target="navTab">补助统计查询</a></li>
								</security:authorize>
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ACCOUNTCENTER_BALANCE">
									<li><a href="/balanceCenter/balanceRecord/index.do" target="navTab">结算</a></li>
								</security:authorize>
								<!-- 梁敏 -->
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ACCOUNTCENTER_DEPTSTAT">
									<li><a href="/balanceCenter/deptStat/index.do" target="navTab">部门营业统计</a></li>
								</security:authorize>
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ACCOUNTCENTER_DEVICESTAT">
									<li><a href="/balanceCenter/deviceStat/index.do" target="navTab">终端营业统计</a></li>
								</security:authorize>
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ACCOUNTCENTER_DEPTMEAL">
									<li><a href="/balanceCenter/deptMeal/index.do" target="navTab">部门就餐统计</a></li>
								</security:authorize>
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ACCOUNTCENTER_DEVICE">
									<li><a href="/balanceCenter/deviceAnalysis2/index.do" target="navTab">终端营业分析</a></li>
								</security:authorize>
								<!-- 赵春辉 -->
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ACCOUNTCENTER_ACCOUNTSTAT">
									<li><a href="/balanceCenter/balancePool/index.do" target="navTab">结算汇总</a></li>
								</security:authorize>
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ACCOUNTCENTER_CASHSTAT">
									<li><a href="/balanceCenter/cashierPool/index.do" target="navTab">出纳汇总</a></li>
								</security:authorize>
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ACCOUNTCENTER_BALANCESTAT">
									<li><a href="/balanceCenter/poisePool/index.do" target="navTab">平衡汇总</a></li>
								</security:authorize>
								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ACCOUNTCENTER_DEPT">
									<li><a href="/balanceCenter/depbusanyPool/index.do" target="navTab">部门营业分析</a></li>
								</security:authorize>
<%-- 								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ACCOUNTCENTER_DEPT2"> --%>
<!-- 									<li><a href="/balanceCenter/depbusanyPool2/index.do" target="navTab">部门营业分析2</a></li> -->
<%-- 								</security:authorize> --%>
<%-- 								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ACCOUNTCENTER_DEPT3"> --%>
<!-- 									<li><a href="/balanceCenter/depbusanyPool3/index.do" target="navTab">部门营业分析3</a></li> -->
<%-- 								</security:authorize> --%>
<%-- 								<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_ACCOUNTCENTER_DEPT4"> --%>
<!-- 									<li><a href="/balanceCenter/depbusanyPool4/index.do" target="navTab">部门营业分析4</a></li> -->
<%-- 								</security:authorize> --%>
							</ul>
						</div>
					</security:authorize>
					<security:authorize ifAnyGranted="ROLE_ADMIN,ROLE_MONITOR">
						<div class="accordionHeader">
							<h2>
								<!-- 							<span>Folder</span><a onclick="javascript:window.open ('/monitor/index.do','监控平台','width=1024,height=500,top=80,left=80,toolbar=no, menubar=no, scrollbars=no, resizable=no');">监控平台</a> -->
								<span>Folder</span><a onclick="javascript:window.open ('/monitor/index.do','监控平台','width=2024,height=1000,top=80,left=80,toolbar=no, menubar=no, scrollbars=no, resizable=no');">监控平台</a>
							</h2>
						</div>
					</security:authorize>
				</div>
			</div>
		</div>
		<div id="container">
			<div id="navTab" class="tabsPage">
				<div class="tabsPageHeader">
					<div class="tabsPageHeaderContent">
						<!-- 显示左右控制时添加 class="tabsPageHeaderMargin" -->
						<ul class="navTab-tab">
							<li tabid="main" class="main"><a href="javascript:;"><span><span class="home_icon">欢迎首页</span></span></a></li>
						</ul>
					</div>
				</div>
				<div class="navTab-panel tabsPageContent layoutBox">
					<div class="page unitBox">
						<div class="pageFormContent" layoutH="80" style="margin-right: 230px"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>