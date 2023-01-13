<%@ page import="com.ruimin.ifb.acct.process.util.AcctConstant"%>
<%@ page import="com.ruimin.ifs.integrate.ifinflow.process.WfConstant"%>
<%@ page import="com.ruimin.ifb.util.IfbConstant"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/snowweb" prefix="snow"%>
<snow:page title="承兑审批">
<snow:dataset id="acptApprove" path="com.ruimin.ifb.dataset.AcptApprove" init="true" parameters="contId=${param.bussId};op=qry" submitMode="current"></snow:dataset>
<snow:dataset id="acptListMng" path="com.ruimin.ifb.dataset.AcptApprove" init="true" ></snow:dataset>
<snow:form id="formModifyId" dataset="acptApprove" label="协议信息"  
	fieldstr="contNo,drftTyp,custId,acptAmt" collapsible="false" colnum="4"></snow:form>
<snow:form id="totFormId" dataset="acptApprove" label="汇总信息"
	fieldstr="totNum,totAmt,totFeeAmt,totExpAmt" collapsible="false" colnum="8"></snow:form>
<snow:grid dataset="acptListMng" id="gridId" label="票据清单列表" fieldstr="drftNo[240],isseAmt[120],isseDt[90],dueDt[90],drwrCustIdName,pyeeNm,feeAmt[80],expAmt[80],coreAppStat[80],acctStat[80],coreCredStat[80],acptStat[80],op[50]"></snow:grid>
<jsp:include page="/pages/wf/appDtl.jsp">
	<jsp:param value="false" name="_mpf_"/>
</jsp:include>
<snow:form dataset="acptApprove"  label="审批信息" fieldstr="apprRmk"/>
<div class="ifb-button-div">
	<!--<snow:button id="btnAcctPrev" desc="分录预览" dataset="acptApprove" hidden="true"></snow:button>
	<snow:button id="btnDownload" desc="导出数据" dataset="acptApprove" ></snow:button>-->
	<snow:button id="btnEcdsSend" dataset="acptApprove" hidden="true"></snow:button>
	<snow:button id="btnAgree" desc="同意" dataset="acptApprove" hidden="true"></snow:button>
	<snow:button id="btnGoBack" desc="退回" dataset="acptApprove"  hidden="false"></snow:button>
	<snow:button id="btnCmsCorrect" desc="冲正" dataset="acptApprove" hidden="true"></snow:button>
	<snow:button id="btnGoBackAcct" desc="退回记账" dataset="acptApprove" hidden="true"></snow:button>
	<snow:button id="btnBack" desc="返回" dataset="acptApprove"></snow:button>
</div>
<iframe src="" style="display: none;" id="filedown"></iframe>

<%-- <jsp:include page="/pages/common/extFields.jsp">
	<jsp:param name="_mpf_" value="false" />
	<jsp:param name="bussId" value="202007" />
	<jsp:param name="txnCd" value="3002506" />
	<jsp:param name="extType" value="1" />
</jsp:include>  --%>
<script>

var taskNm = "${param.taskNm}";
function initCallGetter_post(){
	if (taskNm=="<%=WfConstant.WF_NODE_APPROVE%>"){
		setButtonDesc("btnGoBack","不同意");
		$("#btnAgree").show();
		$("#btnGoBack").show();
	}else if (taskNm=="<%=WfConstant.WF_NODE_INPUT%>"){
		$("#btnAgree").show();
		$("#btnGoBack").hide();
	}
	else if(taskNm=="<%=WfConstant.WF_NODE_SIGN%>"){
		$("#btnGoBack").hide();
		$("#btnEcdsSend").show();
		$("#btnGoBackAcct").show();
	}
}
//承兑协议号超链接
function editor_contNo_onRefresh(element, v) {
	var contId = acptApprove_dataset.getValue("id");
	if (v) {
		if(taskNm == "CmsAccount"){
		     return "<a href=\"Javascript:openElecAcptCont('"+contId+"','"+v+"')\">" + v + "</a>";
		}else{
		     return "<a href=\"Javascript:openElecAcptCont('"+contId+"','"+v+"')\">" + v + "</a>";
		}	
	} else {
		return " ";
	}
}

function grid_op_onRefresh(record, fieldId, fieldValue){
	var detailId = record.getValue("id");
	var drftNo = record.getValue("drftNo");
	if(taskNm == "CmsAccount"){
	       return printListExtend(detailId,drftNo);
    }
}

//签收按钮
function btnEcdsSend_onClickCheck(button,commit){
	acptApprove_dataset.setParameter("atti","<%=WfConstant.WF_ATTI_AGREE%>");
	$.confirm("是否确认提交？", function() {
		commit();
	}, function() {
	});
}

function btnEcdsSend_postSubmit(button){
	$.success("操作成功",function(){
		window.location.href=_application_root+"/pages/wf/todoTaskList.jsp";
	});
}

function btnCmsCorrect_postSubmit(button){
	$.success("操作成功",function(){
		window.location.href=_application_root+"/pages/wf/todoTaskList.jsp";
	});
}


//签收，记账按钮
function btnAgree_onClickCheck(button,commit){
	acptApprove_dataset.setParameter("atti","<%=WfConstant.WF_ATTI_AGREE%>");
	$.confirm("是否确认提交？", function() {
		commit();
	}, function() {
	});
}


function btnAgree_postSubmit(button){
	$.success("操作成功",function(){
		window.location.href=_application_root+"/pages/wf/todoTaskList.jsp";
	});
}


//返回按钮
function btnBack_onClick() {
	window.location.href=_application_root+"/pages/wf/todoTaskList.jsp";
}


function btnGoBackAcct_onClickCheck(button,commit){
	acptApprove_dataset.setParameter("atti","<%=WfConstant.WF_ATTI_GOBACK_ACCOUNT%>");
	$.confirm("是否确认退回记账？", function() {
		commit();
	}, function() {
	});
}

function btnGoBackAcct_postSubmit(button){
	$.success("操作成功",function(){
		window.location.href=_application_root+"/pages/wf/todoTaskList.jsp";
	});
}


function btnGoBack_onClickCheck(button,commit){
	acptApprove_dataset.setParameter("atti","<%=WfConstant.WF_ATTI_GOBACK%>");
	$.confirm("是否确认退回？", function() {
		commit();
	}, function() {
	});
}

function btnCmsCorrect_onClickCheck(button,commit){
	$.confirm("是否确认冲正？", function() {
		commit();
	}, function() {
	});
}

function btnGoBack_postSubmit(button){
	$.success("操作成功",function(){
		window.location.href=_application_root+"/pages/wf/todoTaskList.jsp";
	});
}

//出账预览
function btnAcctPrev_onClickCheck(button){
	var bussId = acptApprove_dataset.getValue("id");
<%-- 	var tranNo = "<%=AcctConstant.TRAN_NO_0101%>"; --%>
	$.open("acctPrevWin", "分录预览", "/pages/acct/acctEntryPrev.jsp?bussId="+bussId+"&tranNo="+tranNo,"800","400",false, true, null, false,"关闭");
}

function acctPrevWin_onButtonClick (i,win,framewin){
	acctPrevWin.close();
}

function btnDownload_needCheck(){
 	return false;
}
//下载文件
function btnDownload_postSubmit(){
 	var url = '<snow:url flowId="com.ruimin.ifb.elc.acpt.comp.AcptContMngAction:downLoadExcel" isDownLoad="true"/>';
 	document.getElementById("filedown").src = url;
 	//$.success("导出成功!");
}
</script>
</snow:page>
