package com.kinnara.kecakplugins.pivottable;

import org.joget.apps.datalist.model.*;
import org.joget.plugin.base.PluginManager;
import org.joget.apps.app.dao.DatalistDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.DatalistDefinition;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.service.DataListService;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.userview.model.Userview;
import org.joget.apps.userview.model.UserviewMenu;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.StringUtil;
import org.joget.workflow.util.WorkflowUtil;
import org.kecak.apps.userview.model.AceUserviewMenu;
import org.kecak.apps.userview.model.AdminLteUserviewMenu;
import org.kecak.apps.userview.model.BootstrapUserviewTheme;
import org.mockito.internal.matchers.Null;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.Model;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataListPivotTable extends UserviewMenu implements AceUserviewMenu, AdminLteUserviewMenu {
    private WeakHashMap<String, DataList> datalistCache = new WeakHashMap<>();

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
//        try {
//            Map<String, Object> dataModel = new HashMap<String, Object>();
//            ApplicationContext appContext    = AppUtil.getApplicationContext();
//            PluginManager      pluginManager = (PluginManager) appContext.getBean("pluginManager");
//            String formDefId    =  getPropertyString("formDefId");
//            String tableName    =   "";
//            AppDefinition appDef = AppUtil.getCurrentAppDefinition();
//            if (appDef != null && formDefId != null) {
//                AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
//                tableName = appService.getFormTableName(appDef, formDefId);
//            }
//
//            //DataList dataList = getDataList();
//            DataListCollection<Map<String, Object>> resultList = new DataListCollection<>();
//            FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");
//            FormRowSet rowSetData = formDataDao.find(formDefId,tableName, null, null, null, false, null, null);
//
//            //String rowId = getPropertyString("pivotRowId");
//            String coloumnId = getPropertyString("pivotColoumnId");
//
//           /* data.put("label",getPropertyString("pivotRowId"));
//            resultList.add(data);*/
//           String[] tempCol = coloumnId.split(";");
//           for(String coloumnName : tempCol){
//               HashMap<String, Object> data = new HashMap<String, Object>();
//               data.put("label",coloumnName);
//               resultList.add(data);
//               if(rowSetData!=null){
//                   for(FormRow rowData : rowSetData){
//                       HashMap<String, Object> datarow = new HashMap<String, Object>();
//                       if(rowData.getProperty(coloumnName)!=null) {
//                           datarow.put("value", rowData.getProperty(coloumnName));
//                       }else {
//                           datarow.put("value", "");
//                       }
//                       resultList.add(datarow);
//                   }
//               }
//           }
//            dataModel.put("data",resultList);
//            LogUtil.info(getClassName(), "data: "+resultList.getList()); //Masih error saat ingin memasukkan list ini ke js
//            //LogUtil.info(getClassName(), "dataValue: "+resultList.getList());
//            /*dataModel.put("total",resultListRow.getList().size());*/
//            String htmlContent = pluginManager.getPluginFreeMarkerTemplate(dataModel, getClassName(), "/templates/pivotTableAceAdmin.ftl",null);
//            return htmlContent;
//        }catch (Exception e) {
//            LogUtil.error(getClassName(), e, "------" + e.getMessage());
//            return null;
//        }

        Map<String, Object> dataModel = new HashMap<String, Object>();

        ApplicationContext appContext    = AppUtil.getApplicationContext();
        PluginManager      pluginManager = (PluginManager) appContext.getBean("pluginManager");

        DataList dataList = getDataList(getPropertyString("dataListId"));

//        LogUtil.info(getClass().getName(),"DatalisId ["+getPropertyString("dataListId")+"]");


        if (dataList != null) {
            getCollectFilters(dataList, ((Map<String, Object>) getRequestParameters()));
            DataListCollection<Map<String, String>> collections = dataList.getRows(DataList.MAXIMUM_PAGE_SIZE, 0);
            JSONArray data = new JSONArray();
//            for (Map<String, String> row : collections) {
//                try {
//                    JSONObject jsonRow = new JSONObject();
//                    for (String field : row.keySet()) {
//                        String value = format(dataList, row, field);
//                        if (value != null)
//                            jsonRow.put(field, value);
//                        else if (row.get(field) != null)
//                            jsonRow.put(field, row.get(field));
//                    }
//                    data.put(jsonRow);
//
//                } catch (JSONException e) {
//                    data.put(new JSONObject(row));
//                    LogUtil.error(getClassName(), e, "");
//                }
//            }

            // use datalist's primary key if label field not specified
            if (getPropertyString("labelField") == null || getPropertyString("labelField").isEmpty())
                setProperty("labelField", dataList.getBinder().getPrimaryKeyColumnName());

            dataModel.put("data", data);
            LogUtil.info(getClassName(),"data ["+data+"]");
        }



        DataListColumn[] columns = dataList.getColumns();
        Comparator<DataListColumn> comparator = new Comparator<DataListColumn>() {
            public int compare(DataListColumn o1, DataListColumn o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };

        Arrays.sort(columns, comparator);
        DataListColumn column = new DataListColumn();
        for (Object o : (Object[]) getProperty("valueFields")) {
            Map<String, String> row = (Map<String, String>) o;

            column.setName(row.get("field"));
            int index = Arrays.binarySearch(columns, column, comparator);
            row.put("label", index >= 0 ? columns[index].getLabel() : row.get("field"));
        }

        dataModel.put("column",column);
        String htmlContent = pluginManager.getPluginFreeMarkerTemplate(dataModel, getClassName(), "/templates/pivotTableAceAdmin.ftl",null);
        return htmlContent;
    }

    @Override
    public boolean isHomePageSupported() {
        return true;
    }


    @Override
    public String getDecoratedMenu() {
        return null;
    }


    /*protected void viewList() {
        try {
            DataList dataList = getDataList();
            if (dataList != null) {
                LogUtil.info(getClass().getName(),"datalist: "+dataList);
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
    }*/
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
        return AppUtil.readPluginResource(getClass().getName(), "/properties/pivotTable.json", null, true, null);
    }


    private DataList getDataList(String datalistId) {
        ApplicationContext ac     = AppUtil.getApplicationContext();
        AppDefinition      appDef = AppUtil.getCurrentAppDefinition();

        if (datalistCache.containsKey(datalistId))
            return datalistCache.get(datalistId);

        DataListService       dataListService       = (DataListService) ac.getBean("dataListService");
        DatalistDefinitionDao datalistDefinitionDao = (DatalistDefinitionDao) ac.getBean("datalistDefinitionDao");
        DatalistDefinition    datalistDefinition    = (DatalistDefinition) datalistDefinitionDao.loadById(datalistId, appDef);
        if (datalistDefinition != null) {
            DataList dataList = dataListService.fromJson(datalistDefinition.getJson());
            datalistCache.put(datalistId, dataList);
            return dataList;
        }
        return null;
    }

    private void getCollectFilters(DataList dataList, Map<String, Object> requestParameters) {
        DataListColumn[] columns = dataList.getColumns();

        Comparator<DataListColumn> comparator = Comparator.comparing(DataListColumn::getName);

        Arrays.sort(columns, comparator);
        DataListColumn key = new DataListColumn();
        for(Map.Entry<String, Object> entry : requestParameters.entrySet()) {
            key.setName(entry.getKey());
            int index = Arrays.binarySearch(columns, key, comparator);
            if(index >= 0) {
                try {
                    // parameter is one of the filter
                    DataListFilterQueryObject filter = new DataListFilterQueryObject();
                    filter.setOperator("AND");
                    // this is the default pattern of datalist filter query is "lower([field]) like lower(?)"
                    filter.setQuery("lower(" + entry.getKey() + ") like lower(?)");
                    if(entry.getValue() instanceof String[]) {
                        String[] parameterValues = (String[])entry.getValue();
                        String[] values = new String[parameterValues.length];
                        for(int i = 0, size = parameterValues.length; i< size; i++) {
                            // this is the default pattern of datalist filter value is %[value]%
                            values[i] = "%" + parameterValues[i] + "%";
                        }
                        filter.setValues(values);
                    } else {
                        filter.setValues( new String[] { "%" + entry.getValue().toString() + "%"});
                    }
                    dataList.addFilterQueryObject(filter);
                } catch(Exception e) {
                    LogUtil.error(getClassName(), e, "Error creating filter [" + entry.getKey() + "]");
                }
            }
        }
    }

    private String format(DataList dataList, Map<String, String> row, String field) {
        if(dataList.getColumns() != null) {
            for(DataListColumn column : dataList.getColumns()) {
                if(field.equals(column.getName())) {
                    String value = String.valueOf(row.get(field));
                    if(column.getFormats() != null) {
                        for(DataListColumnFormat format : column.getFormats()) {
                            if(format != null) {
                                return format.format(dataList, column, row, value).replaceAll("<[^>]*>", "");
                            }
                        }
                    } else {
                        return value;
                    }
                }
            }
        }
        return null;
    }




    @Override
    public String getAceJspPage(BootstrapUserviewTheme bootstrapUserviewTheme) {
        return null;
    }

    @Override
    public String getAceRenderPage() {
        Map<String, Object> dataModel = new HashMap<String, Object>();

        ApplicationContext appContext    = AppUtil.getApplicationContext();
        PluginManager      pluginManager = (PluginManager) appContext.getBean("pluginManager");

        DataList dataList = getDataList(getPropertyString("dataListId"));

        LogUtil.info(getClass().getName(),"DatalisId ["+getPropertyString("dataListId")+"]");


        if (dataList != null) {
            getCollectFilters(dataList, ((Map<String, Object>) getRequestParameters()));
            DataListCollection<Map<String, String>> collections = dataList.getRows(DataList.MAXIMUM_PAGE_SIZE, 0);
            JSONArray data = new JSONArray();
            for (Map<String, String> row : collections) {
                try {
                    JSONObject jsonRow = new JSONObject();
                    for (String field : row.keySet()) {
                        String value = format(dataList, row, field);
                        if (value != null)
                            jsonRow.put(field, value);
                        else if (row.get(field) != null)
                            jsonRow.put(field, row.get(field));
                    }
                    data.put(jsonRow);

                } catch (JSONException e) {
                    data.put(new JSONObject(row));
                    LogUtil.error(getClassName(), e, "");
                }
            }

            // use datalist's primary key if label field not specified
            if (getPropertyString("labelField") == null || getPropertyString("labelField").isEmpty())
                setProperty("labelField", dataList.getBinder().getPrimaryKeyColumnName());

            dataModel.put("data", data);
            LogUtil.info(getClassName(),"data ["+data+"]");
        }


        dataModel.put("dataListId", dataList.getId());

        String htmlContent = pluginManager.getPluginFreeMarkerTemplate(dataModel, getClassName(), "/templates/pivotTableAceAdmin.ftl",null);
        return htmlContent;
    }

    @Override
    public String getAceDecoratedMenu() {
        return null;
    }

    @Override
    public String getAdminLteJspPage(BootstrapUserviewTheme bootstrapUserviewTheme) {
        return null;
    }

    @Override
    public String getAdminLteRenderPage() {
        return null;
    }

    @Override
    public String getAdminLteDecoratedMenu() {
        return null;
    }



}
