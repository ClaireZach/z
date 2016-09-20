<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">
	var selectDeptTree=$('.selectDeptTree');
	var list = $('#selectDeptTreeLi li');
	if(list.length!=0){
		$('li', selectDeptTree).append("<ul class='expand'></ul>");
	}
	list.each(function() {
		var $this = $(this);
		var parentId = $this.attr('parentId');		
		if (parentId == '0') {
			$('.expand', selectDeptTree).append($this);
		} 
		else {
			var $li = $('li[deptId="' + parentId + '"]', selectDeptTree);
			if ($('>ul', $li).size() == 0) {
				$('<ul></ul>', selectDeptTree).appendTo($li);
			}
 			$('>ul', $li).append($(this));
		}
	});
	//提交
// 	$('#sub').click(function(){
// 		var deptNames = "";
// 		var  deptids = "";
// 		 //  遍历选中
// 		$("div .checked").each(function(){
// 			var deptName = $(this).next().html();
// 			var deptid = $(this).parent().parent().attr("deptid");
// 			deptNames += deptName +",";
// 			deptids += deptid +",";
// 		});
//  		alert(deptNames+"   "+deptids);
//         $.bringBack({deptName:deptNames,deptId:deptids});
//     });
	
</script>

<div id="selectDeptTreeLi">
	<c:forEach items="${deptList}" var="d">
		<li deptId="${d.id }" parentId="${d.parentId }"><a href="javascript:;" onclick="$.bringBack({deptId:'${d.id}', deptName:'${d.deptName }'})">${d.deptName }</a></li>
	</c:forEach>
<!-- 	<input type="button" id="sub" value="确定" /> -->
</div>


<!-- <ul class="tree collapse selectUserDeptTree"> -->
<!-- <ul class="tree collapse selectUserDeptTree"> -->
<!-- <ul class="tree expand selectDeptTree"> -->
<ul class="tree collapse  selectDeptTree">
	<li deptId="0"><a href="javascript:;" onclick="$.bringBack({deptId:'-1', deptName:'全部部门'})">营业部门列表</a>
	</li>
</ul>