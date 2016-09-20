<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>兴邦IC卡管理系统</title>
<link href="/themes/default/style.css" rel="stylesheet" type="text/css" media="screen" />
<link href="/themes/css/core.css" rel="stylesheet" type="text/css" media="screen" />

<!--[if IE]>
<link href="/themes/css/ieHack.css" rel="stylesheet" type="text/css" media="screen"/>
<![endif]-->

<!--[if lte IE 9]>
<script src="/js/speedup.js" type="text/javascript"></script>
<![endif]-->

<script type="text/javascript">
	
</script>
<style type="text/css">
	#deviceGroupList{
		display: none;
	}
	.device{
		float: left;
		height: auto;
		width: 80px;
		text-align:center;
/*   		border: 1px solid red; */
		margin-top: 5px;
	}
	#container {
		top: 0;
		left: 0;
	}
	#splitBar {
		display: none;
	}
	.tabsPage .tabsPageContent {
		border-style: none;
		border-width: 0;
	}
	.tabsHeaderContent:hover{ cursor:row-resize;}
	.schedule{
		position:relative; 
		left:2px; 
		color: red;
		font-size: 14px;
		font-weight: bold;
		margin-top: 3px;
		<c:if test="${mode==0}">
			display: none;
		</c:if>
	}
	.transfer{
		color:red;
		float: left;
		<c:if test="${mode==0}">
 			display: none;
		</c:if>
	}
</style>
</head>
<body scroll="no">
	<div class="contextMenu" id="menu" style="display: none;">
		<ul>
			<li id="sysTime">校验时间</li>
			<li id="baseConsumeParam">消费类参数</li>
			<li id="deviceParam">设备参数</li>
			<li class="divide" />
			<li id="grantSubsidy">补助授权</li>
			<li id="disableSubsidy">补助回收</li>
			<li class="divide" />
			<li id="batchUpdate">批次更新</li>
			<li id="blackUpdate">黑名单更新</li>
			<li class="divide" />
			<li id="allCookbook">全部菜肴清单</li>
			<li id="appendCookbook">追加菜肴清单</li>
			<li id="singelCookbook">更新指定菜肴</li>
			<li class="modifyCookbook" style="display: none;">
				<input type="text" id="code" value="编号" style="width: 35px;" onmousedown ="setCodeNull();"/><input type="button" value="确定" onclick="modifyCookbook();"/>
			</li>
			<li class="divide" />
			<li id="clear">清空命令</li>
			<li id="sysInit">初始化</li>
		</ul>
	</div>
	<div class="contextMenu" id="upgradeMenu" style="display: none;">
		<ul>
			<li id="upgrade">设备升级</li>
		</ul>
	</div>
	<div class="contextMenu" id="clearMenu" style="display: none;">
		<ul>
			<li id="clear">清空日志</li>
		</ul>
	</div>
	<div id="layout" layoutH="0">
		<div id="container" layoutH="0">
			<div id="navTab" class="tabsPage" layoutH="0">
				<div class="navTab-panel tabsPageContent layoutBox" layoutH="0">
					<div class="page unitBox">
						
						<div class="tabs">
							<div class="tabsContent" style="border: none;">
								<div>
									<!-- 营业部门-->
									<div id="deptTreeLi">
										<c:forEach items="${deptList }" var="d">
											<li deptId="${d.id }" parentId="${d.parentId }">
												<a deptId="${d.id }" onclick="showDevice(${d.id });">${d.deptName }</a>
											</li>
										</c:forEach>
									</div>
									<div layoutH="10" style="float: left; display: block; overflow: auto; width: 170px; border: solid 1px #CCC; line-height: 21px; background: #fff;">
										<ul class="tree expand deptTree">
											<li deptId="0"><a onclick="showDevice(0);">营业部门列表</a>
											</li>
										</ul>
									</div>
									<!-- 设备显示区域 -->
									<div id="deviceList" class="unitBox" style="margin-left: 175px;height:240px;border: solid 1px #CCC; background: #fff;overflow: auto;padding: 5px 0 0 5px;">
										<!-- 主动udp设备 -->
										<c:forEach items="${posUdpDeviceList }" var="d">
											<c:if test="${d.deviceType!=1}">
												<div class="device" id="${d.sn}" name="${d.transferSn}" deptId="${d.deptId }">
													<div class="schedule" name="${d.sn}">
														<c:if test="${mode==1}">0.00%</c:if>
													</div>
													<c:if test="${d.isOnline==1}">
														<img alt="在线" src="/img/online.png" />
													</c:if>
													<c:if test="${d.isOnline==0}">
														<img alt="离线" src="/img/offline.png" />
													</c:if>
													<div class="name">${d.deviceName}</div>
												</div>
											</c:if>
										</c:forEach>
										<!-- 中转设备 -->
										<c:forEach items="${posTransferDeviceList }" var="list" varStatus="status">
											<c:forEach items="${list }" var="d" varStatus="status">
												<c:if test="${status.index==0}">
													<div style="clear: both;border-bottom: 1px solid red;margin-bottom: 5px;height: 15px;"></div>
													<div class="device transfer" id="${d.sn}" name="${d.transferSn}">
														<div class="schedule" name="${d.sn}">
															<c:if test="${mode==1}">0.00%</c:if>
														</div>
														<c:if test="${d.isOnline==1}">
															<img alt="在线" src="/img/online.png" />
														</c:if>
														<c:if test="${d.isOnline==0}">
															<img alt="离线" src="/img/offline.png" />
														</c:if>
														<div class="name">${d.deviceName}</div>
													</div>
												</c:if>
												<c:if test="${status.index>0}">
													<div class="device" id="${d.sn}" name="${d.transferSn}" deptId="${d.deptId }">
														<div class="schedule" name="${d.sn}">
															<c:if test="${mode==1}">0.00%</c:if>
														</div>
														<c:if test="${d.isOnline==1}">
															<img alt="在线" src="/img/online.png" />
														</c:if>
														<c:if test="${d.isOnline==0}">
															<img alt="离线" src="/img/offline.png" />
														</c:if>
														<div class="name">${d.deviceName}</div>
													</div>
												</c:if>
											</c:forEach>
										</c:forEach>
									</div>
									<!-- 设备显示区域 end-->
									<!-- 监控信息显示区域 -->
									<div id="msgList" class="unitBox" style="margin-left: 175px;">
										<div class="tabs" currentIndex="0" eventType="click" style="margin-top:2px;" >
											<div class="tabsHeader">
												<div class="tabsHeaderContent">
													<ul>
														<li><a href="javascript:void();"><span>消费设备状态</span></a></li>
														<li><a href="javascript:void();"><span>消费记录监控</span></a></li>
														<li><a href="javascript:void();"><span>订餐信息监控</span></a></li>
														<li><a href="javascript:void();"><span>门禁考勤设备状态</span></a></li>
														<li><a href="javascript:void();"><span>考勤记录监控</span></a></li>
														<li><a href="javascript:void();"><span>日记监控</span></a></li>
													</ul>
												</div>
											</div>
											<div id="msgContent" class="tabsContent" style="padding:0;" layoutH="280">
												<!-- 消费设备状态 -->
												<div id="deviceStatusList">
													<table class="table" width="99%" rel="jbsxBox" target="deviceStatus">
														<thead>
															<tr>
																<th width="100">设备名称</th>
																<th width="100">机器号</th>
																<th width="100">未采记录</th>
<!-- 																<th width="100">批次个数</th> -->
<!-- 																<th width="100">黑名单个数</th> -->
																<th width="100">补助版本号</th>
																<th width="100">补助授权</th>
															</tr>
														</thead>
														<tbody>
															<c:forEach items="${deviceList }" var="d">
																<tr class="deviceStatus" id="${d.sn}" deptId="${d.deptId }">
																	<td>${d.deviceName }</td>
																	<td>${d.deviceNum }</td>
																	<td id="status${d.sn}" recordCount>0</td>
<!-- 																	<td batchCount>0</td> -->
<!-- 																	<td blackCount>0</td> -->
																	<td subsidyVersion>0</td>
																	<td subsidyAuth>否</td>
																</tr>
															</c:forEach>
														</tbody>
													</table>
												</div>
												<!-- 消费设备状态 end-->
												<!-- 消费记录监控 -->
												<div id="consumeRecord">
													<table class="table" width="100%" rel="jbsxBox" target="consumeRecord">
														<thead>
															<tr>
																<th width="40">序号</th>
																<th width="100">终端名称</th>
																<th width="80">用户编号</th>
																<th width="80">卡号</th>
																<th width="100">姓名</th>
																<th width="100">钱包余额</th>
																<th width="100">补助余额</th>
																<th width="80">管理费</th>
																<th width="100">操作额</th>
																<th width="100">餐别名称</th>
																<th width="200">操作时间</th>
																<th width="80">卡计数</th>
																<th width="90">补助卡计数</th>
																<th width="80">记录序号</th>
																<th width="120">记录类型</th>
																<th width="100">菜肴名称</th>
																<th width="80">菜肴份数</th>
															</tr>
														</thead>
														<tbody class="clearLog consumeRecord">
															<c:forEach begin="1" end="100" step="1" var="i">
																<tr index="${i}">
																	<td index></td>
																	<td deviceName></td>
																	<td userNO></td>
																	<td cardNO></td>
																	<td username></td>
																	<td oddFare></td>
																	<td subsidyOddFare></td>
																	<td discountFare></td>
																	<td opFare></td>
																	<td mealName></td>
																	<td opTime></td>
																	<td opCount></td>
																	<td subsidyOpCount></td>
																	<td recordNO></td>
																	<td consumeTypeDes></td>
																	<td cookbookName></td>
																	<td cookbookNum></td>
																</tr>
															</c:forEach>
														</tbody>
													</table>
												</div>
												<!-- 消费记录监控 end-->
												<!-- 订餐信息监控 -->
												<div id="cookbookRecord">
													<table class="table" width="99%" rel="jbsxBox" target="cookbookRecord">
														<thead>
															<tr>
																<th width="40">序号</th>
																<th width="100">终端名称</th>
																<th width="80">用户编号</th>
																<th width="80">卡号</th>
																<th width="100">姓名</th>
																<th width="100">操作额</th>
																<th width="100">餐别名称</th>
																<th width="100">就餐时间</th>
																<th width="100">订餐时间</th>
																<th width="100">菜肴名称</th>
																<th width="100">菜肴份数</th>
																<th width="100">记录序号</th>
																<th width="100">类别</th>
															</tr>
														</thead>
														<tbody>
															<c:forEach begin="1" end="100" step="1" var="i">
																<tr index="${i}">
																	<td index></td>
																	<td deviceName></td>
																	<td userNO></td>
																	<td cardNO></td>
																	<td username></td>
																	<td opFare></td>
																	<td mealName></td>
																	<td mealTime></td>
																	<td orderTime></td>
																	<td cookbookName></td>
																	<td cookbookNum></td>
																	<td recordNO></td>
																	<td typeName></td>
																</tr>
															</c:forEach>
														</tbody>
													</table>
												</div>
												<!-- 订餐信息监控 end-->
												<!-- 门禁考勤设备状态 -->
												<div id="attendanceStatus">
													<table class="table" width="99%" rel="jbsxBox" target="attendanceStatus">
														<thead>
															<tr>
																<th width="40">序号</th>
																<th width="100">终端名称</th>
																<th width="100">地址</th>
																<th width="100">未采记录</th>
																<th width="100">门状态</th>
																<th width="100">连机认证</th>
																<th width="100">获取时间</th>
																<th width="100">连机状态</th>
															</tr>
														</thead>
														<tbody>
															<c:forEach items="${attendanceStatusList }" var="d">
																<tr>
																</tr>
															</c:forEach>
														</tbody>
													</table>
												</div>
												<!-- 门禁考勤设备状态 end-->
												<!-- 考勤记录监控 -->
												<div id="attendanceRecord">
													<table class="table" width="99%" rel="jbsxBox" target="attendanceRecord">
														<thead>
															<tr>
																<th width="40">序号</th>
																<th width="100">终端编号</th>
																<th width="100">类型</th>
																<th width="100">用户编号</th>
																<th width="100">姓名</th>
																<th width="100">卡号</th>
																<th width="100">物理卡号</th>
																<th width="100">记录号</th>
																<th width="100">操作时间</th>
															</tr>
														</thead>
														<tbody>
															<c:forEach items="${attendanceRecordList }" var="d">
																<tr>
																</tr>
															</c:forEach>
														</tbody>
													</table>
												</div>
												<!-- 考勤记录监控 end-->
												<!-- 日记监控 -->
												<div id="log">
													<table class="table" width="99%" rel="jbsxBox" target="log">
														<thead>
															<tr>
																<th width="20">序号</th>
																<th width="70">日志时间</th>
																<th width="70">日期来源</th>
																<th width="500">日志描述</th>
															</tr>
														</thead>
														<tbody class="clearLog log">
															<c:forEach begin="1" end="100" step="1" var="i">
																<tr index="${i}"><td index></td><td time></td><td from></td><td des alt="dfdf"></td></tr>
															</c:forEach>
														</tbody>
													</table>
												</div>
												<!-- 日记监控 end-->
										</div>
									</div>
									<!-- 监控信息显示区域 end -->
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="${base}/js.do"/>
</body>
</html>