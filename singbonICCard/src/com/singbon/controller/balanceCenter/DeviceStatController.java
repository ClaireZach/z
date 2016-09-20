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

import com.singbon.entity.Company;
import com.singbon.entity.Meal;
import com.singbon.entity.Pagination;
import com.singbon.service.CommonService;
import com.singbon.service.systemManager.systemSetting.MealService;
import com.singbon.util.ExportUtil;
import com.singbon.util.StringUtil;

/**
 * 终端营业日统计
 * 
 * @author 梁敏
 *
 */

@Controller
@RequestMapping(value = "/balanceCenter/deviceStat")
public class DeviceStatController {

	@Autowired
	public CommonService commonService;
	@Autowired
	public MealService mealService;
	List<Meal> mealList = null;

	float totalSum = 0.0f;

	float honchonFareSum = 0.0f;

	Integer opCounts = 0;

	Integer honchonCount = 0;

	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value = "/index.do")
	public String index(HttpServletRequest req, Model model) {

		Company company = (Company) req.getSession().getAttribute("company");
		model.addAttribute("company", company);

		mealList = (List<Meal>) this.mealService.selectListByCompanyId(company.getId());

		String url = req.getRequestURI();

		model.addAttribute("mealList", mealList);
		model.addAttribute("base", url.replace("/index.do", ""));

		return url.replace(".do", "");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/list.do")
	public String list(@ModelAttribute Pagination pagination, Integer deptId, String deptName, Integer export, Integer exportType, String beginDate, String endDate, String includeSub, Integer mealId,
			String mealName, HttpServletRequest req, HttpServletResponse res, Model model) {

		Company company = (Company) req.getSession().getAttribute("company");

		if (exportType != null && 1 == exportType) {
			pagination.setPageNum(1);
			pagination.setNumPerPage(pagination.getTotalCount());
		}

		// String fromSql = "consumerecord c LEFT JOIN device de ON c.deviceId =
		// de.id LEFT JOIN sysuser syu on c.deviceId=syu.deviceId LEFT JOIN
		// cardrecord crd ON syu.operId = crd.operId LEFT JOIN dept d ON
		// de.deptId = d.id LEFT JOIN meal m ON c.mealId = m.id";
		String fromSql = "consumerecord c LEFT JOIN device de ON c.deviceId = de.id  LEFT JOIN dept d ON d.id = de.deptId LEFT JOIN meal m ON c.mealId = m.id";
		String whereSql = "d.companyId = " + company.getId();

		// if (!StringUtils.isEmpty(deptId) && deptId != -1) {
		// if (StringUtils.isEmpty(includeSub)) {
		// whereSql += String.format(" and find_in_set(d.id,getSubIds(%s,1))>0",
		// deptId);
		//
		// } else {
		// whereSql += String.format(" and d.id = %s", deptId);
		// }
		// }
		if (!StringUtils.isEmpty(beginDate)) {
			whereSql += String.format(" and c.opTime >= '%s'", beginDate);
		}
		if (!StringUtils.isEmpty(endDate)) {
			whereSql += String.format(" and c.opTime <= '%s'", endDate);
		}
		if (!StringUtils.isEmpty(mealId) && mealId != -1) {
			whereSql += String.format(" and m.id = %s", mealId);
		}
		if (!StringUtils.isEmpty(deptId) && deptId != -1) {
			whereSql += String.format(" and d.id = %s", deptId);
		}
		if (StringUtils.isEmpty(includeSub)) {
			whereSql += String.format(" or d.parentId = %s", deptId);

		}

		String sql0 = String.format(
				" select d.deptName," + "de.deviceName," + "IFNULL(m.mealName,'其他') as mealName," + "c.opTime opTime," + "c.opTime endTime,"
						+ "SUM(CASE WHEN (c.consumeType = 2 OR c.consumeType = 102 OR c.consumeType = 3 OR c.consumeType = 103	OR c.consumeType=1 OR c.consumeType = 101) THEN c.opFare+c.subsidyOpFare ELSE	0 END) totalling,"
						+ "SUM(CASE WHEN (c.consumeType = 2 OR c.consumeType = 102 OR c.consumeType = 3 OR c.consumeType = 103 OR c.consumeType=1 OR c.consumeType = 101) THEN 1 ELSE 0 END) opCounts,"
						+ "SUM( CASE WHEN ( c.consumeType = 39 OR c.consumeType = 139 ) THEN c.subsidyOpFare ELSE 0 END) honchonFare,"
						+ "SUM( CASE WHEN ( c.consumeType = 39 OR c.consumeType = 139 ) THEN 1 ELSE 0 END) honchonCount from %s where %s group by left(c.opTime,10),de.deviceName,d.id,m.mealName",
				fromSql, whereSql);
		List<Map> list0 = this.commonService.selectListBySql(sql0);

		String sql1 = String.format(
				" select d.deptName," + "de.deviceName," + "IFNULL(m.mealName,'其他') as mealName," + "c.opTime opTime," + "c.opTime endTime,"
						+ "SUM(CASE WHEN (c.consumeType = 2 OR c.consumeType = 102 OR c.consumeType = 3 OR c.consumeType = 103	OR c.consumeType=1 OR c.consumeType = 101) THEN c.opFare+c.subsidyOpFare ELSE	0 END) totalling,"
						+ "SUM(CASE WHEN (c.consumeType = 2 OR c.consumeType = 102 OR c.consumeType = 3 OR c.consumeType = 103 OR c.consumeType=1 OR c.consumeType = 101) THEN 1 ELSE 0 END) opCounts,"
						+ "SUM( CASE WHEN ( c.consumeType = 39 OR c.consumeType = 139 ) THEN c.subsidyOpFare ELSE 0 END) honchonFare,"
						+ "SUM( CASE WHEN ( c.consumeType = 39 OR c.consumeType = 139 ) THEN 1 ELSE 0 END) honchonCount from %s where %s group by left(c.opTime,10),de.deviceName,d.id,m.mealName limit %s,%s",
				fromSql, whereSql, pagination.getOffset(), pagination.getNumPerPage());
		List<Map> limitList = this.commonService.selectListBySql(sql1);

		for (Map map : limitList) {
			map.put("totalling", (float) StringUtil.objToInt(map.get("totalling")) / 100);
			map.put("honchonFare", (float) StringUtil.objToInt(map.get("honchonFare")) / 100);
			map.put("opTime", StringUtil.objToString(map.get("opTime")).substring(0, 10) + " 00:00:00");
			map.put("endTime", StringUtil.objToString(map.get("endTime")).substring(0, 10) + " 23:59:59");

			totalSum += (float) map.get("totalling");
			opCounts += StringUtil.objToInt(map.get("opCounts"));
			honchonFareSum += (float) map.get("honchonFare");
			honchonCount += StringUtil.objToInt(map.get("honchonCount"));
		}
		BigDecimal b1 = new BigDecimal(totalSum);
		BigDecimal b2 = new BigDecimal(honchonFareSum);
		totalSum = (float) b1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		honchonFareSum = (float) b2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		if (export != null && 1 == export) {
			String[] expColumns = { "营业部门", "设备名称", "餐别", "消费时间", "统计时间", "日消费营业额", "日消费次数", "日宏冲额", "日宏冲次数" };
			List<String> list1 = new ArrayList<String>();
			List<List<String>> exportList = new ArrayList<List<String>>();
			for (Map m : limitList) {
				List<String> list2 = new ArrayList<String>();
				list2.add(StringUtil.objToString(m.get("deptName")));
				list2.add(StringUtil.objToString(m.get("deviceName")));
				list2.add(StringUtil.objToString(m.get("mealName")));
				list2.add(StringUtil.objToString(m.get("opTime")));
				list2.add(StringUtil.objToString(m.get("endTime")));
				list2.add(StringUtil.objToString(m.get("totalling")));
				list2.add(StringUtil.objToString(m.get("opCounts")));
				list2.add(StringUtil.objToString(m.get("honchonFare")));
				list2.add(StringUtil.objToString(m.get("honchonCount")));

				exportList.add(list2);
			}

			list1.add("汇总");
			list1.add(beginDate);
			list1.add(endDate);
			list1.add("");
			list1.add("");
			list1.add(StringUtil.objToString(totalSum));
			list1.add(StringUtil.objToString(opCounts));
			list1.add(StringUtil.objToString(honchonFareSum));
			list1.add(StringUtil.objToString(honchonCount));

			exportList.add(list1);
			ExportUtil.exportExcel("终端营业日统计" + StringUtil.getNowTime(), expColumns, exportList, res);

			totalSum = 0.0f;
			honchonFareSum = 0.0f;
			opCounts = 0;
			honchonCount = 0;

			return null;
		}
		int totalCount = list0.size();
		model.addAttribute("list0", list0);
		model.addAttribute("limitList", limitList);
		model.addAttribute("pageNum", pagination.getPageNum());
		model.addAttribute("numPerPage", pagination.getNumPerPage());
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("deptId", deptId);
		model.addAttribute("deptName", deptName);
		model.addAttribute("includeSub", includeSub);
		model.addAttribute("beginDate", beginDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("mealList", mealList);
		model.addAttribute("mealId", mealId);
		model.addAttribute("mealName", mealName);
		model.addAttribute("totalSum", totalSum);
		model.addAttribute("opCounts", opCounts);
		model.addAttribute("honchonFareSum", honchonFareSum);
		model.addAttribute("honchonCount", honchonCount);

		totalSum = 0.0f;
		honchonFareSum = 0.0f;
		opCounts = 0;
		honchonCount = 0;

		model.addAttribute("base", StringUtil.requestBase(req));
		return StringUtil.requestPath(req, "list");
	}

}
