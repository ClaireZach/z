<!-- 智慧校园 -->
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">
	
</script>
<div class="tabs" currentIndex="0" loadIndex="0" eventType="click" style="margin: 1px;" >
	<div class="tabsHeader">
		<div class="tabsHeaderContent">
			<ul>
				<security:authorize ifAnyGranted="ROLE_ADMIN">
				<li><a href="${base }/adminIndex.do" class="j-ajax"><span>管理员设置</span></a></li>
				</security:authorize>
				<security:authorize ifAnyGranted="ROLE_ADMIN">
				<li><a href="${base }/adminDeptIndex.do" class="j-ajax"><span>部门权限设置</span></a></li>
				</security:authorize>
			</ul>
		</div>
	</div>
	<div class="tabsContent" style="padding:0;" layoutH="0">
		<div></div>
		<div></div>
	</div>
</div>
