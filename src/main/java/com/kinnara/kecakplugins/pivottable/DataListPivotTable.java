package com.kinnara.kecakplugins.pivottable;

import org.joget.apps.app.dao.DatalistDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.DatalistDefinition;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListActionResult;
import org.joget.apps.datalist.service.DataListService;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.userview.model.Userview;
import org.joget.apps.userview.model.UserviewMenu;
import org.joget.commons.util.StringUtil;
import org.joget.mofiz.model.CommonUtil;
import org.joget.workflow.util.WorkflowUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DataListPivotTable extends UserviewMenu {
    @Override
    public String getCategory() {
        return "Kecak";
    }

    @Override
    public String getIcon() {
        return "/plugin/org.joget.apps.userview.lib.HtmlPage/images/grid_icon.gif";
    }

    @Override
    public String getRenderPage() {
        return null;
    }

    @Override
    public boolean isHomePageSupported() {
        return true;
    }

    @Override
    public String getDecoratedMenu() {
        DataList dataList;
        String menuItem = null;
        boolean showRowCount = Boolean.valueOf(getPropertyString("rowCount"));
        if (showRowCount && (dataList = getDataList()) != null) {
            int rowCount = dataList.getTotal();
            String label = getPropertyString("label");
            if (label != null) {
                label = StringUtil.stripHtmlRelaxed(label);
            }
            menuItem = "<a href=\"" + getUrl() + "\" class=\"menu-link default\"><span>" + label + "</span> <span class='rowCount'>(" + rowCount + ")</span></a>";
        }
        return menuItem;
    }

    @Override
    public String getJspPage(){
        return getJspPage("userview/plugin/datalist.jsp", "userview/plugin/form.jsp", "userview/plugin/unauthorized.jsp");
    }

    public String getJspPage(String listFile, String formFile, String unauthorizedFile) {
        /*String mode = getRequestParameterString("_mode");
        if ("add".equals(mode) || "edit".equals(mode)) {
            this.setProperty("customHeader", getPropertyString(mode + "-customHeader"));
            this.setProperty("customFooter", getPropertyString(mode + "-customFooter"));
            this.setProperty("messageShowAfterComplete", getPropertyString(mode + "-messageShowAfterComplete"));
            return this.handleForm(formFile,unauthorizedFile);
        }
        this.setProperty("customHeader", getPropertyString("list-customHeader"));
        this.setProperty("customFooter", getPropertyString("list-customFooter"));*/
        this.viewList();
        return listFile;
    }

    protected void viewList() {
        try {
            DataList dataList = getDataList();
            if (dataList != null) {
                /*dataList.setActionPosition(getPropertyString("buttonPosition"));
                dataList.setSelectionType(getPropertyString("selectionType"));
                dataList.setCheckboxPosition(getPropertyString("checkboxPosition"));
                dataList = addDatalistButtons(dataList);
                DataListActionResult ac = dataList.getActionResult();
                if (ac != null) {
                    if (ac.getMessage() != null && !ac.getMessage().isEmpty()) {
                        this.setAlertMessage(ac.getMessage());
                    }
                    if (ac.getType() != null && "REDIRECT".equals(ac.getType()) && ac.getUrl() != null && !ac.getUrl().isEmpty()) {
                        if ("REFERER".equals(ac.getUrl())) {
                            HttpServletRequest request = WorkflowUtil.getHttpServletRequest();
                            if (request != null && request.getHeader("Referer") != null) {
                                this.setRedirectUrl(request.getHeader("Referer"));
                            } else {
                                this.setRedirectUrl("REFERER");
                            }
                        } else {
                            this.setRedirectUrl(ac.getUrl());
                        }
                    }
                }*/
                this.setProperty("dataList", dataList);
            } else {
                this.setProperty("error", ("Data List \"" + getPropertyString("datalistId") + "\" not exist."));
            }
        }
        catch (Exception ex) {
            StringWriter out = new StringWriter();
            ex.printStackTrace(new PrintWriter(out));
            String message = ex.toString();
            message = message + "\r\n<pre class=\"stacktrace\">" + out.getBuffer() + "</pre>";
            this.setProperty("error", message);
        }
    }
    @Override
    public String getName() {
        return "Pivot Table";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "Show Datalist Pivot Table";
    }

    @Override
    public String getLabel() {
        return "Pivot Table";
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        String appId = appDef.getId();
        String appVersion = appDef.getVersion().toString();
        Object[] arguments = new Object[]{appId, appVersion, appId, appVersion, appId, appVersion};
        String json = AppUtil.readPluginResource(getClass().getName(), "/properties/pivotTable.json", arguments, true,null);
        return json;
    }

    protected DataList getDataList() throws BeansException {
        /*ApplicationContext ac = AppUtil.getApplicationContext();
        AppService appService = (AppService)ac.getBean("appService");
        DataListService dataListService = (DataListService)ac.getBean("dataListService");
        DatalistDefinitionDao datalistDefinitionDao = (DatalistDefinitionDao)ac.getBean("datalistDefinitionDao");
        DatalistDefinition datalistDefinition = datalistDefinitionDao.loadById(getPropertyString("datalistId"), appService.getAppDefinition(getRequestParameterString("appId"), getRequestParameterString("appVersion")));
        if (datalistDefinition != null) {
            DataList dataList = dataListService.fromJson(datalistDefinition.getJson());
            if (getPropertyString(Userview.USERVIEW_KEY_NAME) != null && getPropertyString(Userview.USERVIEW_KEY_NAME).trim().length() > 0) {
                dataList.addBinderProperty(Userview.USERVIEW_KEY_NAME, getPropertyString(Userview.USERVIEW_KEY_NAME));
            }
            if (getKey() != null && getKey().trim().length() > 0) {
                dataList.addBinderProperty(Userview.USERVIEW_KEY_VALUE, getKey());
            }
            return dataList;
        }
        return null;
    }*/
        //ApplicationContext ac = AppUtil.getApplicationContext();
        //AppService appService = (AppService)ac.getBean("appService");
        //DataListService dataListService = (DataListService)ac.getBean("dataListService");
        DataList dataList = new DataList();
        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
        FormRowSet rowSetData = formDataDao.find( getPropertyString("formId"),getPropertyString("tableName"), null, null, null, false, null, null);
        if(CommonUtil.isNotNullOrEmpty(rowSetData)){
            for(FormRow rowData : rowSetData){
                dataList.addBinderProperty("Kolom1",rowData.getProperty(getPropertyString("coloumnName")));
                dataList.addBinderProperty("Kolom2",rowData.getProperty(getPropertyString("tableName")));
            }
            return  dataList;
        }
        return null;

    }
}
