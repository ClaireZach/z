package com.singbon.controller.balanceCenter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.singbon.controller.BaseController;
import com.singbon.entity.Company;
import com.singbon.entity.Pagination;
import com.singbon.entity.UserDept;
import com.singbon.service.CommonService;
import com.singbon.service.systemManager.systemSetting.UserDeptService;
import com.singbon.util.ExportUtil;
import com.singbon.util.StringUtil;

/**
 * 用户信息查询控制类
 * 
 * @author 郝威
 * 
 */
@Controller
@RequestMapping(value = "/balanceCenter/userInfo")
public class UserInfoController extends BaseController {

	@Autowired
	public UserDeptService userDeptService;
	@Autowired
	public CommonService commonService;

	/**
	 * 列表
	 * 
	 * @param request
	 * @param model
	 * @param module
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/list.do")
	public String list(@ModelAttribute Pagination pagination, Integer export, Integer exportType, String nameStr, Integer deptId, String deptName, Integer cardTypeId, Integer sex, Integer status,
			Integer dateType, String beginDate, String endDate, HttpServletRequest request, HttpServletResponse response, Model model) {
		Company company = (Company) request.getSession().getAttribute("company");

		if (exportType != null && 1 == exportType) {
			pagination.setPageNum(1);
			pagination.setNumPerPage(pagination.getTotalCount());
		}

		String[] columns = { "u.userId", "u.userNO", "u.username", "u.sex", "u.cardID", "udp.deptName", "u.cardTypeId", "u.`status`", "u.oddFare", "u.subsidyOddFare" };
		String fromSql = " `user` u LEFT JOIN userdept udp on udp.id = u.deptId";
		String whereSql = "u.companyId=" + company.getId();
		if (!StringUtils.isEmpty(nameStr)) {
			whereSql += String.format(" and (userNO like '%%%s%%' or username like '%%%s%%' or shortName like '%%%s%%')", nameStr, nameStr, nameStr);
		}
		/* if (!StringUtils.isEmpty(deptId)) { */
		if (!StringUtils.isEmpty(deptId) && (deptId != -1)) {
			whereSql += String.format(" and deptId = %s", deptId);
		}
		if (!StringUtils.isEmpty(cardTypeId) && cardTypeId != -1) {
			whereSql += String.format(" and cardTypeId = %s", cardTypeId);
		}
		if (!StringUtils.isEmpty(sex) && sex != -1) {
			whereSql += String.format(" and sex = %s", sex);
		}
		if (!StringUtils.isEmpty(status)) {
			if (status != -1 && status != -2) {
				whereSql += String.format(" and status = %s", status);
			} else if (status == -2) {
				whereSql += " and status not in(0,241,243,244)";
			}
		}
		// if (!StringUtils.isEmpty(dateType) && dateType != -1) {
		// String dateColumn = "cardMakeDate";
		// if (dateType == 1) {
		// dateColumn = "invalidDate";
		// }
		// if (!StringUtils.isEmpty(beginDate)) {
		// whereSql += String.format(" and %s >= '%s'", dateColumn, beginDate);
		// }
		// if (!StringUtils.isEmpty(endDate)) {
		// whereSql += String.format(" and %s <= '%s'", dateColumn, endDate);
		// }
		// }
		// 2016.09.02 zch 添加默认显示当天的
		String dateColumn = "cardMakeDate";
		if (StringUtils.isEmpty(endDate)) {
			endDate = StringUtil.getNowTimeForDataCenterEd();
			whereSql += String.format(" and %s <= '%s'", dateColumn, endDate);
		}
		if (StringUtils.isEmpty(beginDate)) {
			beginDate = StringUtil.getNowTimeForDataCenterBg();
			whereSql += String.format(" and %s >= '%s'", dateColumn, beginDate);
		}

		if (!StringUtils.isEmpty(dateType) && dateType != -1) {
			// sString dateColumn = "cardMakeDate";
			if (dateType == 1) {
				dateColumn = "invalidDate";
			}
			if (!StringUtils.isEmpty(beginDate)) {
				whereSql += String.format(" and %s >= '%s'", dateColumn, beginDate);
			}
			if (!StringUtils.isEmpty(endDate)) {
				whereSql += String.format(" and %s <= '%s'", dateColumn, endDate);
			}
		}

		List<Map> userList = this.commonService.selectByPage(columns, null, fromSql, whereSql, pagination);
		List<Map> userListAll = this.commonService.selectNoPage(columns, null, fromSql, whereSql);

		int totalCount = Integer.valueOf(userList.get(0).get("userId").toString());
		userList.remove(0);
		userListAll.remove(0);

		BigDecimal sumoddFareAll = new BigDecimal(0);
		BigDecimal sumsubsidyOddFareAll = new BigDecimal(0);
		BigDecimal sumallFareAll = new BigDecimal(0);
		for (Map m : userListAll) {
			//sumoddFareAll += (StringUtil.objToFloat(m.get("oddFare")) / 100);
			sumoddFareAll = sumoddFareAll.add(new BigDecimal(m.get("oddFare").toString()).divide(new BigDecimal(100)));
			//sumsubsidyOddFareAll += (StringUtil.objToFloat(m.get("subsidyOddFare")) / 100);
			sumsubsidyOddFareAll = sumsubsidyOddFareAll.add(new BigDecimal(m.get("subsidyOddFare").toString()).divide(new BigDecimal(100)));
			
			//Float allFareAll = (StringUtil.objToFloat(m.get("oddFare")) / 100 + StringUtil.objToFloat(m.get("subsidyOddFare")) / 100);
			BigDecimal allFareAll = new BigDecimal(m.get("oddFare").toString()).divide(new BigDecimal(100)).add(new BigDecimal(m.get("subsidyOddFare").toString()).divide(new BigDecimal(100)));
			//sumallFareAll += allFareAll;
			sumallFareAll = sumallFareAll.add(allFareAll);
		}
		BigDecimal sumoddFare = new BigDecimal(0);
		BigDecimal sumsubsidyOddFare = new BigDecimal(0);
		BigDecimal sumallFare = new BigDecimal(0);
		for (Map m : userList) {
			int sex2 = Integer.valueOf(m.get("sex").toString());
			if (sex2 == 0) {
				m.put("sexDesc", "男");
			} else {
				m.put("sexDesc", "女");
			}
			int status2 = Integer.valueOf(m.get("status").toString());
			String statusDesc = "";
			if (status2 == 0) {
				statusDesc = "未发卡";
			} else if (status2 == 241) {
				statusDesc = "正常";
			} else if (status2 == 243) {
				statusDesc = "挂失";
			} else if (status2 == 244) {
				statusDesc = "注销";
			} else {
				statusDesc = "异常卡";
			}
			m.put("statusDesc", statusDesc);
			// m.put("oddFare",
			// StringUtil.objToLong(m.get("oddFare").toString()) / 100);
			if (StringUtils.isEmpty(m.get("oddFare"))) {
				m.put("oddFare", 0);
			}
			m.put("oddFare", (Float.valueOf(m.get("oddFare").toString()) / 100));
			m.put("subsidyOddFare", (Float.valueOf(m.get("subsidyOddFare").toString()) / 100));
			Float allFare = StringUtil.objToFloat(m.get("oddFare")) + StringUtil.objToFloat(m.get("subsidyOddFare"));
			m.put("allFare", allFare);

			//sumoddFare += StringUtil.objToFloat(m.get("oddFare"));
			sumoddFare = sumoddFare.add(new BigDecimal(m.get("oddFare").toString()));
			//sumsubsidyOddFare += StringUtil.objToFloat(m.get("subsidyOddFare"));
			sumsubsidyOddFare = sumsubsidyOddFare.add(new BigDecimal(m.get("subsidyOddFare").toString()));
			//sumallFare += allFare;
			sumallFare = sumallFare.add(new BigDecimal(allFare).setScale(2,BigDecimal.ROUND_HALF_UP));
		}

		if (export != null && 1 == export) {
			String[] expColumns = { "编号", "姓名", "性别", "身份证号", "所属部门", "卡类别", "卡状态", "卡余额", "卡补助余额", "总额" };

			List<List<String>> list = new ArrayList<List<String>>();
			for (Map m : userList) {
				List<String> list2 = new ArrayList<String>();
				list2.add(m.get("userNO").toString());
				list2.add(m.get("username").toString());
				list2.add(m.get("sexDesc").toString());
				list2.add(m.get("cardID").toString());
				list2.add(m.get("deptName").toString());
				list2.add(m.get("cardTypeId").toString());
				list2.add(m.get("statusDesc").toString());
				list2.add(m.get("oddFare").toString());
				list2.add(m.get("subsidyOddFare").toString());
				list2.add(m.get("allFare").toString());
				// list2.add(m.get("statusDesc").toString());
				list.add(list2);
			}

			ExportUtil.exportExcel("用户信息表" + StringUtil.getNowTime(), expColumns, list, response);
			return null;
		}

		model.addAttribute("list", userList);
		model.addAttribute("pageNum", pagination.getPageNum());
		model.addAttribute("numPerPage", pagination.getNumPerPage());
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("nameStr", nameStr);
		model.addAttribute("deptId", deptId);
		model.addAttribute("deptName", deptName);
		model.addAttribute("cardTypeId", cardTypeId);
		model.addAttribute("sex", sex);
		model.addAttribute("status", status);
		model.addAttribute("dateType", dateType);
		model.addAttribute("beginDate", beginDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("sumoddFare", sumoddFare);
		model.addAttribute("sumsubsidyOddFare", sumsubsidyOddFare);
		model.addAttribute("sumallFare", sumallFare);
		model.addAttribute("sumoddFareAll", sumoddFareAll);
		model.addAttribute("sumsubsidyOddFareAll", sumsubsidyOddFareAll);
		model.addAttribute("sumallFareAll", sumallFareAll);
		model.addAttribute("base", StringUtil.requestBase(request));
		return StringUtil.requestPath(request, "list");
	}

	/**
	 * 人员部门树列表
	 * 
	 * @param userDept
	 * @param request
	 * @param model
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/treeList.do")
	public String treeList(HttpServletRequest request, HttpServletResponse response, Model model) {
		Company company = (Company) request.getSession().getAttribute("company");
		List<UserDept> list = (List<UserDept>) this.userDeptService.selectListByCompanyId(company.getId());
		model.addAttribute("list", list);
		return StringUtil.requestPath(request, "userDeptTreeList");
	}
}
