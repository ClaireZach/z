<!-- 系统升级列表 -->
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript">

</script>
<style type="text/css">
</style>
<div id="upgradeList">
	<table class="table" width="99%" rel="jbsxBox">
		<thead>
			<tr>
				<th width="40">序号</th>
				<th width="100">文件名</th>
				<th width="100">文件大小</th>
				<th width="150">上传时间</th>
				<th width="100">操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${list }" var="u" varStatus="status">
				<tr>
					<td>${status.index+1}</td>
					<td>${u.truename }</td>
					<td>${u.filesize}字节</td>
					<td>${u.uploadTime }</td>
					<td><a title="确认删除吗？" target="ajaxTodo" callback="refreshList" href="${base}/delete.do?id=${u.id}&filename=${u.filename}" class="btnDel">删除</a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>