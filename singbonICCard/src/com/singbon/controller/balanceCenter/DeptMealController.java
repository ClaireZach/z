package com.singbon.controller.balanceCenter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.singbon.entity.Pagination;
import com.singbon.service.CommonService;
import com.singbon.util.ExportUtil;
import com.singbon.util.StringUtil;

/***
 * 部门就餐统计
 * 
 * @author 梁敏
 *
 */

@Controller
@RequestMapping(value = "/balanceCenter/deptMeal")
public class DeptMealController {

	@Autowired
	public CommonService commonService;

	float totalSum = 0.0f;
	float honchonFareSum = 0.0f;
	/************************************/
	// float zaoccs = 0.0f;
	// float wuccs = 0.0f;
	// float wanccs = 0.0f;
	// float yxcs = 0.0f;
	// float jbcs = 0.0f;
	// float qtcs = 0.0f;
	// float zcs = 0.0f;
	// float tjxfe = 0.0f;
	// float tjxfcs = 0.0f;
	// float jcl = 0.0f;
	/************************************/

	// float avgFare = 0.0f;
	//
	// float avgCount = 0.0f;
	//
	// float mealRate = 0.0f;

	Integer opCounts = 0;

	Integer honchonCount = 0;

	@RequestMapping(value = "/index.do")
	public String index(HttpServletRequest req, Model model) {

		Company company = (Company) req.getSession().getAttribute("company");
		model.addAttribute("company", company);

		Calendar calendar = Calendar.getInstance();// 此时打印它获取的是系统当前时间
		calendar.add(Calendar.DATE, -1); // 得到前一天
		String beginDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
		String endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String url = req.getRequestURI();
		model.addAttribute("base", url.replace("/index.do", ""));
		model.addAttribute("endDate", endDate);
		model.addAttribute("beginDate", beginDate);
		return url.replace(".do", "");

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/list.do")
	public String list(@ModelAttribute Pagination pagination, Integer deptId, String deptName, Integer export, Integer exportType, String beginDate, String endDate, String includeSub,
			HttpServletRequest req, HttpServletResponse res, Model model) throws Exception {

		Company company = (Company) req.getSession().getAttribute("company");

		if (exportType != null && 1 == exportType) {
			pagination.setPageNum(1);
			pagination.setNumPerPage(pagination.getTotalCount());
		}
		List<Map> list0 = null;

		String fromSql = " consumerecord c LEFT JOIN device de ON c.deviceId = de.id LEFT JOIN dept d ON de.deptId = d.id LEFT JOIN meal m ON m.id = c.mealId ";
		String whereSql = " d.companyId = " + company.getId();
		String whereSql0 = " c.companyId = " + company.getId();

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
			whereSql0 += String.format(" and c.opTime >= '%s'", beginDate);
		}
		if (!StringUtils.isEmpty(endDate)) {
			whereSql += String.format(" and c.opTime <= '%s'", endDate);
			whereSql0 += String.format(" and c.opTime <= '%s'", endDate);
		}
		if (!StringUtils.isEmpty(deptId) && deptId != -1) {
			whereSql += String.format(" and d.id = %s", deptId);
		}
		if (StringUtils.isEmpty(includeSub)) {
			whereSql += String.format(" or d.parentId = %s", deptId);

		}

		String sql0 = String.format(
				"SELECT n.deptName deptName,n.days,n.totalFares totalFares,n.totalCounts totalCounts,n.honChonFares honChonFares,n.honChonCounts honChonCounts,n.breakfast breakfast,"
						+ "n.lunch lunch,n.dinner dinner,n.night night,n.stayup stayup,n.others others,SUM(n.breakfast+n.lunch+n.dinner+n.night+n.stayup+n.others) sumOpCounts,n.totalFares/(100*TIMESTAMPDIFF(day,'"
						+ beginDate + "','" + endDate + "')) avgFare," + "n.totalCounts/TIMESTAMPDIFF(day,'" + beginDate + "','" + endDate + "') avgCount,n.deptCount/t.mealSum mealRate from"
						+ " (SELECT c.companyId companyId,d.deptName deptName,TIMESTAMPDIFF(day,'" + beginDate + "','" + endDate + "') days,SUM(CASE WHEN c.consumeType = 1 OR c.consumeType = 2 OR"
						+ " c.consumeType = 3 OR c.consumeType = 101 OR c.consumeType = 102 OR c.consumeType = 103 THEN c.opFare + c.subsidyOpFare ELSE 0 END) totalFares,"
						+ " SUM(CASE WHEN c.consumeType = 1 OR c.consumeType = 2 OR c.consumeType = 3 OR c.consumeType = 101 OR c.consumeType = 102 OR c.consumeType = 103"
						+ " THEN 1 ELSE 0 END) totalCounts,SUM(CASE WHEN c.consumeType = 39 OR c.consumeType =139 THEN c.subsidyOpFare ELSE 0 END) honChonFares,SUM(CASE WHEN"
						+ " c.consumeType = 39 OR c.consumeType =139 THEN 1 ELSE 0 END) honChonCounts,SUM(CASE WHEN m.mealName = '早餐' THEN 1 ELSE 0 END) breakfast,"
						+ " SUM(CASE WHEN m.mealName = '午餐' THEN 1 ELSE 0 END) lunch,SUM(CASE WHEN m.mealName = '晚餐' THEN 1 ELSE 0 END) dinner,SUM(CASE WHEN m.mealName"
						+ " = '夜宵' THEN 1 ELSE 0 END) night,SUM(CASE WHEN m.mealName = '加班一' OR m.mealName = '加班二' THEN 1 ELSE 0 END) stayup,SUM(CASE WHEN c.mealId = 0 THEN 1 ELSE 0 END) others,"
						+ " COUNT(c.id) deptCount from %s where %s GROUP BY d.deptName) n,(SELECT SUM( CASE WHEN c.consumeType = 1 OR c.consumeType = 2 OR c.consumeType = 3 OR c.consumeType = 101 OR c.consumeType = 102 OR c.consumeType = 103 THEN 1 ELSE 0 END ) totalCounts,COUNT(c.mealId) mealSum from consumerecord c where %s) t GROUP BY n.deptName",
				fromSql, whereSql, whereSql0);
		list0 = this.commonService.selectListBySql(sql0);

		String sql1 = String.format(
				"SELECT n.deptName deptName,n.days,n.totalFares totalFares,n.totalCounts totalCounts,n.honChonFares honChonFares,n.honChonCounts honChonCounts,n.breakfast breakfast,"
						+ "n.lunch lunch,n.dinner dinner,n.night night,n.stayup stayup,n.others others,SUM(n.breakfast+n.lunch+n.dinner+n.night+n.stayup+n.others) sumOpCounts,n.totalFares/(100*TIMESTAMPDIFF(day,'"
						+ beginDate + "','" + endDate + "')) avgFare," + "n.totalCounts/TIMESTAMPDIFF(day,'" + beginDate + "','" + endDate + "') avgCount,n.deptCount/t.mealSum mealRate from"
						+ " (SELECT c.companyId companyId,d.deptName deptName,TIMESTAMPDIFF(day,'" + beginDate + "','" + endDate + "') days,SUM(CASE WHEN c.consumeType = 1 OR c.consumeType = 2 OR"
						+ " c.consumeType = 3 OR c.consumeType = 101 OR c.consumeType = 102 OR c.consumeType = 103 THEN c.opFare + c.subsidyOpFare ELSE 0 END) totalFares,"
						+ " SUM(CASE WHEN c.consumeType = 1 OR c.consumeType = 2 OR c.consumeType = 3 OR c.consumeType = 101 OR c.consumeType = 102 OR c.consumeType = 103"
						+ " THEN 1 ELSE 0 END) totalCounts,SUM(CASE WHEN c.consumeType = 39 OR c.consumeType =139 THEN c.subsidyOpFare ELSE 0 END) honChonFares,SUM(CASE WHEN"
						+ " c.consumeType = 39 OR c.consumeType =139 THEN 1 ELSE 0 END) honChonCounts,SUM(CASE WHEN m.mealName = '早餐' THEN 1 ELSE 0 END) breakfast,"
						+ " SUM(CASE WHEN m.mealName = '午餐' THEN 1 ELSE 0 END) lunch,SUM(CASE WHEN m.mealName = '晚餐' THEN 1 ELSE 0 END) dinner,SUM(CASE WHEN m.mealName"
						+ " = '夜宵' THEN 1 ELSE 0 END) night,SUM(CASE WHEN m.mealName = '加班一' OR m.mealName = '加班二' THEN 1 ELSE 0 END) stayup,SUM(CASE WHEN c.mealId = 0 THEN 1 ELSE 0 END) others,"
						+ " COUNT(c.id) deptCount from %s where %s GROUP BY d.deptName) n,(SELECT SUM( CASE WHEN c.consumeType = 1 OR c.consumeType = 2 OR c.consumeType = 3 OR c.consumeType = 101 OR c.consumeType = 102 OR c.consumeType = 103 THEN 1 ELSE 0 END ) totalCounts,COUNT(c.mealId) mealSum from consumerecord c where %s) t GROUP BY n.deptName limit %s,%s",
				fromSql, whereSql, whereSql0, pagination.getOffset(), pagination.getNumPerPage());
		List<Map> limitList = this.commonService.selectListBySql(sql1);

		for (Map map : limitList) {
			map.put("totalFares", (float) StringUtil.objToInt(map.get("totalFares")) / 100);
			map.put("honChonFares", (float) StringUtil.objToInt(map.get("honChonFares")) / 100);

			// avgFare = (float) map.get("avgFare");
			// avgCount = (float) map.get("avgCount");
			// mealRate = (float) map.get("mealRate");

			// String avgFare = new
			// DecimalFormat("###,###,###.##").format((float)
			// map.get("avgFare"));
			// String avgCount = new
			// DecimalFormat("###,###,###.##").format((float)
			// map.get("avgCount"));
			// String mealRate = new
			// DecimalFormat("###,###,###.##").format((float)
			// map.get("mealRate"));

			// BigDecimal a = new BigDecimal(avgCount);
			// BigDecimal b = new BigDecimal(avgFare);
			// BigDecimal c = new BigDecimal(mealRate);
			// avgFare = (double) a.setScale(2,
			// BigDecimal.ROUND_HALF_UP).doubleValue();
			// avgCount = (double) b.setScale(2,
			// BigDecimal.ROUND_HALF_UP).doubleValue();
			// mealRate = (double) c.setScale(2,
			// BigDecimal.ROUND_HALF_UP).doubleValue();

			// map.put("avgFare",avgFare );
			// map.put("avgCount", avgCount);
			// map.put("mealRate", mealRate);

			totalSum += (float) map.get("totalFares");
			opCounts += StringUtil.objToInt(map.get("totalCounts"));
			honchonFareSum += (float) map.get("honChonFares");
			honchonCount += StringUtil.objToInt(map.get("honChonCounts"));
			/******************************************************/
			// totalSum += (float) map.get("totalFares") ;
			// totalSum += (float) map.get("totalFares") ;
			// totalSum += (float) map.get("totalFares") ;
			// totalSum += (float) map.get("totalFares") ;
			// totalSum += (float) map.get("totalFares") ;
			// totalSum += (float) map.get("totalFares") ;
			// totalSum += (float) map.get("totalFares") ;
			// totalSum += (float) map.get("totalFares") ;
			// totalSum += (float) map.get("totalFares") ;
			// totalSum += (float) map.get("totalFares") ;
			/******************************************************/
		}

		BigDecimal b1 = new BigDecimal(totalSum);
		BigDecimal b2 = new BigDecimal(honchonFareSum);
		totalSum = (float) b1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		honchonFareSum = (float) b2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

		if (export != null && 1 == export) {
			String[] expColumns = { "营业部门", "消费总额", "消费次数", "宏冲总额", "宏冲次数", "早餐次数", "午餐次数", "晚餐次数", "夜宵次数", "加班次数", "其他次数", "总次数", "总天数", "天均消费额", "天均消费次数", "就餐率" };
			List<String> list1 = new ArrayList<String>();
			List<List<String>> exportList = new ArrayList<List<String>>();
			for (Map m : limitList) {
				List<String> list2 = new ArrayList<String>();
				list2.add(StringUtil.objToString(m.get("deptName")));
				list2.add(StringUtil.objToString(m.get("totalFares")));
				list2.add(StringUtil.objToString(m.get("totalCounts")));
				list2.add(StringUtil.objToString(m.get("honChonFares")));
				list2.add(StringUtil.objToString(m.get("honChonCounts")));
				list2.add(StringUtil.objToString(m.get("breakfast")));
				list2.add(StringUtil.objToString(m.get("lunch")));
				list2.add(StringUtil.objToString(m.get("dinner")));
				list2.add(StringUtil.objToString(m.get("night")));
				list2.add(StringUtil.objToString(m.get("stayup")));
				list2.add(StringUtil.objToString(m.get("others")));
				list2.add(StringUtil.objToString(m.get("sumOpCounts")));
				list2.add(StringUtil.objToString(m.get("days")));
				list2.add(StringUtil.objToString(m.get("avgFare")));
				list2.add(StringUtil.objToString(m.get("avgCount")));
				list2.add(StringUtil.objToString(m.get("mealRate")));
				exportList.add(list2);
			}
			list1.add("汇总");
			list1.add(StringUtil.objToString(totalSum));
			list1.add(StringUtil.objToString(opCounts));
			list1.add(StringUtil.objToString(honchonFareSum));
			list1.add(StringUtil.objToString(honchonCount));
			list1.add("");
			list1.add("");
			list1.add("");
			list1.add("");
			list1.add("");
			list1.add("");
			list1.add("");
			list1.add("");
			list1.add("");
			list1.add("");
			list1.add("");

			exportList.add(list1);
			ExportUtil.exportExcel("部门就餐统计" + StringUtil.getNowTime(), expColumns, exportList, res);

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
		model.addAttribute("totalSum", totalSum);
		model.addAttribute("honchonFareSum", honchonFareSum);
		model.addAttribute("opCounts", opCounts);
		model.addAttribute("honchonCount", honchonCount);

		totalSum = 0.0f;
		honchonFareSum = 0.0f;
		opCounts = 0;
		honchonCount = 0;

		// avgFare = 0.0f;
		//
		// avgCount = 0.0f;
		//
		// mealRate = 0.0f;
		model.addAttribute("base", StringUtil.requestBase(req));
		return StringUtil.requestPath(req, "list");
	}

}
