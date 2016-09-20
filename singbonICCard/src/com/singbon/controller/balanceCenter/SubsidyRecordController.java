package com.singbon.controller.balanceCenter;

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
import com.singbon.device.ConsumeType;
import com.singbon.entity.CardType;
import com.singbon.entity.Company;
import com.singbon.entity.Pagination;
import com.singbon.service.CommonService;
import com.singbon.service.systemManager.DeviceService;
import com.singbon.util.ExportUtil;
import com.singbon.util.StringUtil;

/**
 * 补助记录查询控制类
 * 
 * @author 陈梦
 *
 */
@Controller
@RequestMapping(value = "/balanceCenter/subsidyRecord")
public class SubsidyRecordController extends BaseController {

	@Autowired
	public CommonService commonService;
	@Autowired
	public DeviceService deviceService;

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
	public String list(@ModelAttribute Pagination pagination, Integer export, Integer exportType, String nameStr, Integer deptId, String deptName, Integer cardTypeId, String includeSub,
			Integer deviceId, String deviceName, Integer consumeGain, Integer consumeToZero, Integer waterGain, Integer waterToZero, Integer dateType, String beginDate, String endDate,
			String includeOff, HttpServletRequest request, HttpServletResponse response, Model model) {

		Company company = (Company) request.getSession().getAttribute("company");
		List<Map> list = null;
		if (exportType != null && 1 == exportType) {
			pagination.setPageNum(1);
			pagination.setNumPerPage(pagination.getTotalCount());
		}

		String columns = "d.deviceName,u.userNO,u.username,u.cardNO,u.cardTypeId,u.status,u.totalFare,c.oddFare,c.subsidyOpFare,c.subsidyOddFare,c.subsidyOpCount,c.consumeType,c.opTime";
		String fromSql = "subsidy s LEFT JOIN consumerecord c ON s.userId = c.userId left join user u on u.userId=s.userId" + " LEFT JOIN device d ON c.deviceId = d.id";
		String fromSql1 = "subsidyhistory s LEFT JOIN consumerecord c ON s.userId = c.userId left join user u on u.userId=s.userId" + " LEFT JOIN device d ON c.deviceId = d.id";

		String whereSql = "u.companyId=" + company.getId();

		if (!StringUtils.isEmpty(nameStr)) {
			whereSql += String.format(" and (userNO like '%%%s%%' or username like '%%%s%%' or shortName like '%%%s%%')", nameStr, nameStr, nameStr);
		}
		if (!StringUtils.isEmpty(cardTypeId) && cardTypeId != -1) {
			whereSql += String.format(" and cardTypeId = %s", cardTypeId);
		}
		if (!StringUtils.isEmpty(deptId) && deptId != -1) {
			if (StringUtils.isEmpty(includeSub)) {
				whereSql += String.format(" and find_in_set(u.deptId,getSubIds(%s,0))>0", deptId);

			} else {
				whereSql += String.format(" and u.deptId = %s", deptId);
			}
		}
		if (!StringUtils.isEmpty(deviceId) && deviceId != -1) {
			whereSql += String.format(" and deviceId = %s", deviceId);
		}
		if (!StringUtils.isEmpty(consumeGain) && consumeGain != -1 || !StringUtils.isEmpty(consumeToZero) && consumeToZero != -1 || !StringUtils.isEmpty(waterGain) && waterGain != -1
				|| !StringUtils.isEmpty(waterToZero) && waterToZero != -1) {
			if ((!StringUtils.isEmpty(consumeGain) && consumeGain != -1) && (StringUtils.isEmpty(consumeToZero) || consumeToZero == -1) && (StringUtils.isEmpty(waterGain) || waterGain == -1)
					&& (StringUtils.isEmpty(waterToZero) || waterToZero == -1)) {
				whereSql += String.format(" and consumeType = %s", consumeGain);
			} else if ((!StringUtils.isEmpty(consumeGain)) && (!StringUtils.isEmpty(consumeToZero) || !StringUtils.isEmpty(waterGain) || !StringUtils.isEmpty(waterToZero)))
				whereSql += String.format(" and (consumeType = %s", consumeGain);
			{

				if (!StringUtils.isEmpty(consumeToZero) && consumeToZero != -1) {
					if (StringUtils.isEmpty(consumeGain) && StringUtils.isEmpty(waterGain) && StringUtils.isEmpty(waterToZero)) {
						whereSql += String.format(" and consumeType = %s", consumeToZero);
					} else if (StringUtils.isEmpty(consumeGain) && (!StringUtils.isEmpty(waterGain) || !StringUtils.isEmpty(waterToZero))) {
						whereSql += String.format(" and (consumeType = %s", consumeToZero);
					} else if ((!StringUtils.isEmpty(consumeGain) && consumeGain != -1) && (StringUtils.isEmpty(waterGain) || waterGain == -1)
							&& (StringUtils.isEmpty(waterToZero) || waterToZero == -1)) {
						whereSql += String.format(" or consumeType = %s)", consumeToZero);
					} else {
						whereSql += String.format(" or consumeType = %s", consumeToZero);
					}
				}
				if (!StringUtils.isEmpty(waterGain) && waterGain != -1) {
					if (StringUtils.isEmpty(consumeGain) && StringUtils.isEmpty(consumeToZero) && StringUtils.isEmpty(waterToZero)) {
						whereSql += String.format(" and consumeType = %s", waterGain);
					} else if (StringUtils.isEmpty(consumeGain) && StringUtils.isEmpty(consumeToZero) && !StringUtils.isEmpty(waterToZero)) {
						whereSql += String.format(" and (consumeType = %s", waterGain);
					} else if ((waterToZero == null)) {
						whereSql += String.format(" or consumeType = %s)", waterGain);
					} else {
						whereSql += String.format(" or consumeType = %s", waterGain);
					}
				}
				if (!StringUtils.isEmpty(waterToZero) && waterToZero != -1) {
					if (StringUtils.isEmpty(consumeGain) && StringUtils.isEmpty(consumeToZero) && StringUtils.isEmpty(waterGain)) {
						whereSql += String.format(" and consumeType = %s", waterToZero);
					} else if (!StringUtils.isEmpty(consumeGain) && consumeGain != -1 || !StringUtils.isEmpty(consumeToZero) && consumeToZero != -1
							|| !StringUtils.isEmpty(waterGain) && waterGain != -1) {
						whereSql += String.format(" or consumeType = %s)", waterToZero);
					}
				}
			}
		} else {
			whereSql += String.format(" and (consumeType = %s or consumeType = %s or consumeType = %s or consumeType = %s)", 9, 39, 109, 139);
		}
		String dateColumn = "opTime";
		if (!StringUtils.isEmpty(beginDate)) {
			whereSql += String.format(" and %s >= '%s'", dateColumn, beginDate);
		}
		if (!StringUtils.isEmpty(endDate)) {
			whereSql += String.format(" and %s <= '%s'", dateColumn, endDate);
		}

		if (StringUtils.isEmpty(includeOff)) {
			whereSql += " and u.status != 244";
		}
		String sql = String.format("select %s from %s where %s union  select %s from %s where %s limit %s,%s", columns, fromSql, whereSql, columns, fromSql1, whereSql, pagination.getOffset(),
				pagination.getNumPerPage());
		String sql1 = String.format("select %s from %s where %s union  select %s from %s where %s", columns, fromSql, whereSql, columns, fromSql1, whereSql);

		list = this.commonService.selectListBySql(sql);
		List<Map> list1 = this.commonService.selectListBySql(sql1);
		Integer totalCount = list1.size();
		for (Map m : list) {

			m.put("status", CardType.getTypeDes(Integer.valueOf(m.get("status").toString())));
			m.put("oddFare", Float.valueOf(m.get("oddFare").toString()) / 100);
			m.put("totalFare", Float.valueOf(m.get("totalFare").toString()) / 100);
			m.put("subsidyOddFare", Float.valueOf(m.get("subsidyOddFare").toString()) / 100);
			m.put("subsidyOpFare", Float.valueOf(m.get("subsidyOpFare").toString()) / 100);
			m.put("consumeType", ConsumeType.getTypeDes(Integer.valueOf(m.get("consumeType").toString())));
		}

		if (export != null && 1 == export) {

			String[] expColumns = { "终端名称", "用户编号", "学号", "卡号", "姓名", "卡类型", "卡状态", "卡总额", "卡余额", "补助操作额", "补助余额", "补助操作计数", "操作时间" };

			List<List<String>> exportList = new ArrayList<List<String>>();
			for (Map m : list) {
				List<String> list2 = new ArrayList<String>();

				list2.add(StringUtil.objToString(m.get("deviceName")));
				list2.add(StringUtil.objToString(m.get("userId")));
				list2.add(StringUtil.objToString(m.get("userNO")));
				list2.add(StringUtil.objToString(m.get("cardNO")));
				list2.add(StringUtil.objToString(m.get("username")));
				list2.add(StringUtil.objToString(m.get("cardTypeId")));
				list2.add(StringUtil.objToString(m.get("status")));
				list2.add(StringUtil.objToString(m.get("sumFare")));
				list2.add(StringUtil.objToString(m.get("oddFare")));
				list2.add(StringUtil.objToString(m.get("subsidyOpFare")));
				list2.add(StringUtil.objToString(m.get("subsidyOddFare")));

				list2.add(StringUtil.objToString(m.get("subsidyOpCount")));
				list2.add(StringUtil.objToString(m.get("opTime")));
				exportList.add(list2);
			}

			ExportUtil.exportExcel("补助记录表" + StringUtil.getNowTime(), expColumns, exportList, response);
			return null;
		}
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("pageNum", pagination.getPageNum());
		model.addAttribute("numPerPage", pagination.getNumPerPage());

		model.addAttribute("nameStr", nameStr);
		model.addAttribute("deptId", deptId);
		model.addAttribute("deptName", deptName);

		model.addAttribute("consumeGain", consumeGain);
		model.addAttribute("consumeToZero", consumeToZero);
		model.addAttribute("waterGain", waterGain);
		model.addAttribute("waterToZero", waterToZero);

		model.addAttribute("cardTypeId", cardTypeId);
		model.addAttribute("deviceId", deviceId);
		model.addAttribute("deviceName", deviceName);
		model.addAttribute("dateType", dateType);
		model.addAttribute("beginDate", beginDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("includeSub", includeSub);
		model.addAttribute("includeOff", includeOff);
		model.addAttribute("list", list);

		model.addAttribute("base", StringUtil.requestBase(request));
		return StringUtil.requestPath(request, "list");
	}
}
