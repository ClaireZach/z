<!-- 结算记录  陈梦  -->
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">
	
</script>

<style type="text/css">
</style>

<div class="tabs">
	<div class="tabsContent" style="border: none;">
		<div>
			<div id="balanceRecordList" class="unitBox">
				<jsp:include page="${base }/list.do?pageNum=1&numPerPage=50" />
				
			</div>
		</div>
	</div>
</div>