package com.singbon.controller.balanceCenter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.singbon.entity.Company;
import com.singbon.entity.Dept;
import com.singbon.service.CommonService;
import com.singbon.service.systemManager.systemSetting.DeptService;
import com.singbon.util.StringUtil;

/***
 * 终端营业分析2控制层
 * 
 * @author 梁敏
 *
 */

@Controller
@RequestMapping(value = "/balanceCenter/deviceAnalysis2")
public class AnalysisController {

	@Autowired
	public CommonService commonService;
	@Autowired
	public DeptService deptService;

	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value = "/index.do")
	public String index(HttpServletRequest req, Model model) {
		Company company = (Company) req.getSession().getAttribute("company");
		List<Dept> deptList = (List<Dept>) this.deptService.selectListByCompanyId(company.getId());
		model.addAttribute("deptList", deptList);

		String url = req.getRequestURI();
		model.addAttribute("base", url.replace("/index.do", ""));
		return url.replace(".do", "");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/list.do")
	public String list(Integer deptId, String deptName, String beginDate, String endDate, String chartJs, Integer chartType, String includeSub, HttpServletRequest req, HttpServletResponse res,
			Model model) throws Exception {

		Company company = (Company) req.getSession().getAttribute("company");

		String fromSql = "consumerecord  c  LEFT JOIN device de ON c.deviceId = de.id LEFT JOIN dept d ON de.deptId = d.id";
		String whereSql = "d.companyId = " + company.getId();

		if (!StringUtils.isEmpty(beginDate)) {
			whereSql += String.format(" and c.opTime >= '%s'", beginDate);
		}
		if (!StringUtils.isEmpty(endDate)) {
			whereSql += String.format(" and c.opTime <= '%s'", endDate);
		}
		if (!StringUtils.isEmpty(deptId) && deptId != -1) {
			// if (StringUtils.isEmpty(includeSub)) {
			// whereSql += String.format(" and
			// find_in_set(d.id,getSubIds(%s,1))>0", deptId);
			// } else {
			// whereSql += String.format(" and d.id = %s", deptId);
			// }

			whereSql += String.format(" and d.id = %s", deptId);
		}
		if (StringUtils.isEmpty(includeSub)) {
			whereSql += String.format(" or d.parentId = %s", deptId);
		}
		chartJs = "js/jsm/deviceAnalysis2.js";
		if (!StringUtils.isEmpty(chartType) && chartType != -1) {
			chartJs = "js/jsm/deviceAnalysis2line.js";
		}
		String sql = String.format(
				"select d.deptName deptName,de.deviceName deviceName," + "SUM(CASE WHEN (c.consumeType = 2 OR c.consumeType = 102 OR c.consumeType = 3 OR c.consumeType = 103 "
						+ "OR c.consumeType=1 OR c.consumeType = 101) THEN c.opFare+c.subsidyOpFare ELSE 0 END) totalling from" + " %s where %s GROUP BY d.id,de.id ORDER BY totalling DESC",
				fromSql, whereSql);

		List<Map> list = this.commonService.selectListBySql(sql);
		for (Map map : list) {
			map.put("totalling", (float) StringUtil.objToInt(map.get("totalling")) / 100);
		}

		Map map = new HashMap();
		for (int i = 0; i < list.size(); i++) {
			map.put(list.get(i).get("deviceName"), list.get(i).get("totalling"));
		}
		Collection dp = map.keySet();
		Collection fare = map.values();
		/*******************************************************************************/
		double dataWidth = 0;
		double barGap = (dp.size() + 1) * 30;// 柱体间隔宽度
		for (Iterator it = dp.iterator(); it.hasNext();) {
			Object o = it.next();
			dataWidth += o.toString().length();
		}
		dataWidth = (dataWidth * 12 + barGap);
		// System.out.println(" dataWidth "+dataWidth);
		model.addAttribute("dataWidth", dataWidth);
		/*******************************************************************************/
		model.addAttribute("dp", dp);
		model.addAttribute("fare", fare);
		model.addAttribute("deptId", deptId);
		model.addAttribute("deptName", deptName);
		model.addAttribute("includeSub", includeSub);
		model.addAttribute("beginDate", beginDate);
		model.addAttribute("endDate", endDate);

		model.addAttribute("chartType", chartType);
		model.addAttribute("chartJs", chartJs);
		model.addAttribute("base", StringUtil.requestBase(req));
		return StringUtil.requestPath(req, "list");
	}

}
