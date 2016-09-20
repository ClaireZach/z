<!-- 管理员设置列表 -->
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<script type="text/javascript">
	
</script>
<style type="text/css">

</style>
<table class="table" width="99%" layoutH="30" rel="jbsxBox">
	<thead>
		<tr>
			<th width="100">登录名</th>
			<th width="100">姓名</th>
			<th width="100">超级管理员</th>
			<th width="100">天微信限次</th>
			<th width="100">天微信剩余</th>
			<th width="100">天短信限次</th>
			<th width="100">天短信剩余</th>
			<th width="100">备注</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${list }" var="s">
			<tr target="smartSchoolAdmin" userId="${s.id}" loginName="${s.loginName}" adminName="${s.adminName}" loginPwd="${s.loginPwd}" isAdmin="${s.isAdmin}" weixinLimit="${s.weixinLimit}" remainingWeixin="${s.remainingWeixin}" shortLimit="${s.shortLimit}" remainingShort="${s.remainingShort}" remark="${s.remark }">
				<td loginName>${s.loginName}</td>
				<td>${s.adminName }</td>
				<td>
					<c:if test="${s.isAdmin==1}">是</c:if>
					<c:if test="${s.isAdmin==0}">否</c:if>
				</td>
				<td>${s.weixinLimit }</td>
				<td>${s.remainingWeixin }</td>
				<td>${s.shortLimit }</td>
				<td>${s.remainingShort }</td>
				<td>${s.remark }</td>
			</tr>
		</c:forEach>
	</tbody>
</table>