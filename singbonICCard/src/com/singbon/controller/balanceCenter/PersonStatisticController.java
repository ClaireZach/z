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
import com.singbon.service.CommonService;
import com.singbon.service.systemManager.DeviceService;
import com.singbon.util.ExportUtil;
import com.singbon.util.StringUtil;

/**
 * 
 * 个人统计查询控制类
 * 
 * @author 陈梦
 *
 */
@Controller
@RequestMapping(value = "/balanceCenter/personStat")
public class PersonStatisticController extends BaseController {

	@Autowired
	public CommonService commonService;
	@Autowired
	public DeviceService deviceService;

	private Integer totalConsumeCount = 0;
	private Integer totalSubsidyConsumeCount = 0;
	private float totalConsumeFare = 0f;
	private float totalSubsidyConsumeFare = 0f;

	/**
	 * 列表
	 * 
	 * @param request
	 * @param model
	 * @param module
	 * @return
	 * @throws Exception
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/list.do")
	public String list(@ModelAttribute Pagination pagination, Integer export, Integer exportType, String nameStr, Integer deptId, String deptName, String includeSub, String beginDate, String endDate,
			Integer dateType, String includeOff, HttpServletRequest request, HttpServletResponse response, Model model) {

		List<Map> list = null;
		Company company = (Company) request.getSession().getAttribute("company");

		if (exportType != null && 1 == exportType) {
			pagination.setPageNum(1);
			pagination.setNumPerPage(pagination.getTotalCount());
		}
		String fromSql = "consumerecord c LEFT JOIN user u ON c.userId = u.userId left join userdept ud on u.deptId=ud.id";
		String whereSql = "u.companyId=" + company.getId();
		if (!StringUtils.isEmpty(nameStr)) {
			whereSql += String.format(" and (userNO like '%%%s%%' or username like '%%%s%%')", nameStr, nameStr);
		}
		String dateColumn = "opTime";
		if (!StringUtils.isEmpty(beginDate)) {
			whereSql += String.format(" and %s >= '%s'", dateColumn, beginDate);
		}
		if (!StringUtils.isEmpty(endDate)) {
			whereSql += String.format(" and %s <= '%s'", dateColumn, endDate);
		}

		if (!StringUtils.isEmpty(deptId) && deptId != -1) {
			if (StringUtils.isEmpty(includeSub)) {
				whereSql += String.format(" and find_in_set(u.deptId,getSubIds(%s,0))>0", deptId);

			} else {
				whereSql += String.format(" and ud.id = %s", deptId);
			}
		}

		if (StringUtils.isEmpty(includeOff)) {
			whereSql += " and u.status != 244";
		}

		String sql = String.format(
				"select c.cardNO,u.userNO,u.username,ud.deptName,sum(CASE WHEN (c.consumeType = 2 OR c.consumeType = 102"
						+ " OR c.consumeType = 3 OR c.consumeType = 103 OR c.consumeType = 1 OR c.consumeType = 101) THEN c.opFare + c.subsidyOpFare ELSE 0 END) as consumeFare,sum(CASE WHEN (c.consumeType = 2 OR c.consumeType = 102"
						+ " OR c.consumeType = 3 OR c.consumeType = 103 OR c.consumeType = 1 OR c.consumeType = 101) THEN 1 ELSE 0 END) as consumeCount,"
						+ "sum(case when(c.consumeType = '39' OR c.consumeType = '139') then c.subsidyOpFare else 0 end) subsidyConsumeFare,sum(case when(c.consumeType = '39' OR c.consumeType = '139') then 1 else 0 end) subsidyConsumeCount from %s where %s group by c.userId",
				fromSql, whereSql);
		String sql1 = String.format(
				"select c.cardNO,u.userNO,u.username,ud.deptName,sum(CASE WHEN (c.consumeType = 2 OR c.consumeType = 102"
						+ " OR c.consumeType = 3 OR c.consumeType = 103 OR c.consumeType = 1 OR c.consumeType = 101) THEN c.opFare + c.subsidyOpFare ELSE 0 END) as consumeFare,sum(CASE WHEN (c.consumeType = 2 OR c.consumeType = 102"
						+ " OR c.consumeType = 3 OR c.consumeType = 103 OR c.consumeType = 1 OR c.consumeType = 101) THEN 1 ELSE 0 END) as consumeCount,"
						+ "sum(case when(c.consumeType = '39' OR c.consumeType = '139') then c.subsidyOpFare else 0 end) subsidyConsumeFare,sum(case when(c.consumeType = '39' OR c.consumeType = '139') then 1 else 0 end) subsidyConsumeCount from %s where %s group by c.userId limit %s,%s",
				fromSql, whereSql, pagination.getOffset(), pagination.getNumPerPage());
		list = this.commonService.selectListBySql(sql1);
		List<String> totalList = new ArrayList<String>();
		List<Map> list1 = this.commonService.selectListBySql(sql);
		Integer totalCount = list1.size();

		for (Map m : list) {

			m.put("consumeFare", Float.valueOf(m.get("consumeFare").toString()) / 100);
			m.put("subsidyConsumeFare", Float.valueOf(m.get("subsidyConsumeFare").toString()) / 100);

			totalConsumeCount += StringUtil.objToInt(m.get("consumeCount"));
			totalConsumeFare += (float) m.get("consumeFare");
			totalSubsidyConsumeCount += StringUtil.objToInt(m.get("subsidyConsumeCount"));
			totalSubsidyConsumeFare += (float) (m.get("subsidyConsumeFare"));
		}
		BigDecimal b1 = new BigDecimal(totalConsumeFare);
		BigDecimal b2 = new BigDecimal(totalSubsidyConsumeFare);
		totalConsumeFare = (float) b1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		totalSubsidyConsumeFare = (float) b2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

		totalList.add("汇总");
		totalList.add("");
		totalList.add("");
		totalList.add("");
		totalList.add(StringUtil.objToString(totalConsumeCount));
		totalList.add(StringUtil.objToString(totalConsumeFare));
		totalList.add(StringUtil.objToString(totalSubsidyConsumeCount));
		totalList.add(StringUtil.objToString(totalSubsidyConsumeFare));

		model.addAttribute("totalConsumeCount", totalConsumeCount);

		model.addAttribute("totalConsumeFare", totalConsumeFare);
		model.addAttribute("totalSubsidyConsumeCount", totalSubsidyConsumeCount);
		model.addAttribute("totalSubsidyConsumeFare", totalSubsidyConsumeFare);

		totalConsumeCount = 0;
		totalSubsidyConsumeCount = 0;

		totalConsumeFare = 0.0f;
		totalSubsidyConsumeFare = 0.0f;

		if (export != null && 1 == export) {

			String[] expColumns = { "卡号", "学号", "姓名", "部门", "消费次数", "总消费金额", "宏冲次数", "宏冲金额" };

			List<List<String>> exportList = new ArrayList<List<String>>();
			for (Map m : list) {
				List<String> list2 = new ArrayList<String>();
				list2.add(StringUtil.objToString(m.get("cardNO")));
				list2.add(StringUtil.objToString(m.get("userNO")));
				list2.add(StringUtil.objToString(m.get("username")));
				list2.add(StringUtil.objToString(m.get("deptName")));
				list2.add(StringUtil.objToString(m.get("consumeCount")));
				list2.add(StringUtil.objToString(m.get("consumeFare")));
				list2.add(StringUtil.objToString(m.get("subsidyConsumeCount")));
				list2.add(StringUtil.objToString(m.get("subsidyConsumeFare")));

				exportList.add(list2);

			}
			exportList.add(totalList);
			ExportUtil.exportExcel("个人统计表" + StringUtil.getNowTime(), expColumns, exportList, response);
			return null;
		}

		model.addAttribute("list", list);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("pageNum", pagination.getPageNum());
		model.addAttribute("numPerPage", pagination.getNumPerPage());

		model.addAttribute("nameStr", nameStr);
		model.addAttribute("deptId", deptId);
		model.addAttribute("deptName", deptName);
		model.addAttribute("dateType", dateType);
		model.addAttribute("beginDate", beginDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("includeOff", includeOff);
		model.addAttribute("includeSub", includeSub);

		model.addAttribute("base", StringUtil.requestBase(request));
		return StringUtil.requestPath(request, "list");
	}
}
