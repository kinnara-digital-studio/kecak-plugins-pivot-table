package com.kinnara.kecakplugins.pivottable;

import com.kinnarastudio.commons.Declutter;
import com.kinnarastudio.commons.jsonstream.JSONCollectors;
import org.joget.apps.app.dao.DatalistDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.DatalistDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.*;
import org.joget.apps.datalist.service.DataListService;
import org.joget.apps.form.service.FormUtil;
import org.joget.apps.userview.model.UserviewMenu;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginManager;
import org.json.JSONArray;
import org.kecak.apps.userview.model.AceUserviewMenu;
import org.kecak.apps.userview.model.AdminLteUserviewMenu;
import org.kecak.apps.userview.model.BootstrapUserviewTheme;
import org.springframework.context.ApplicationContext;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataListPivotTable extends UserviewMenu implements AceUserviewMenu, AdminLteUserviewMenu, Declutter {
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
        return getRenderPage("/templates/pivotTable.ftl");
    }

    @Override
    public boolean isHomePageSupported() {
        return true;
    }

    @Override
    public String getDecoratedMenu() {
        return null;
    }

    @Override
    public String getName() {
        return getLabel() + getVersion();
    }

    @Override
    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }

    @Override
    public String getDescription() {
        return getClass().getPackage().getImplementationTitle();
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
        return AppUtil.readPluginResource(getClass().getName(), "/properties/pivotTable.json", null, true, "/messages/pivotTable");
    }

    protected DataList getDataList(String datalistId) {
        ApplicationContext ac     = AppUtil.getApplicationContext();
        AppDefinition      appDef = AppUtil.getCurrentAppDefinition();

        DataListService       dataListService       = (DataListService) ac.getBean("dataListService");
        DatalistDefinitionDao datalistDefinitionDao = (DatalistDefinitionDao) ac.getBean("datalistDefinitionDao");
        DatalistDefinition    datalistDefinition    = datalistDefinitionDao.loadById(datalistId, appDef);
        if (datalistDefinition != null) {
            DataList dataList = dataListService.fromJson(datalistDefinition.getJson());
            return dataList;
        }
        return null;
    }

    protected void getCollectFilters(DataList dataList, Map<String, Object> requestParameters) {
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

    @Override
    public String getAceJspPage(BootstrapUserviewTheme bootstrapUserviewTheme) {
        return null;
    }

    @Override
    public String getAceRenderPage() {
        LogUtil.info(getClass().getName(),"getAceRenderPage");
        return getRenderPage("/templates/pivotTable.ftl");
    }

    /**
     * Render page using template
     *
     * @param templatePath Path to FTL template file
     * @return
     */
    protected String getRenderPage(String templatePath) {
        Map<String, Object> dataModel = new HashMap<>();

        ApplicationContext appContext    = AppUtil.getApplicationContext();
        PluginManager      pluginManager = (PluginManager) appContext.getBean("pluginManager");

        DataList dataList = getDataList(getPropertyString("dataListId"));
        String elementName = getPropertyString("elementName");
        dataModel.put("elementName",elementName);
        if (dataList != null) {
            getCollectFilters(dataList, ((Map<String, Object>) getRequestParameters()));
            JSONArray data = getRowsAsJson(dataList);

            dataModel.put("data", data);
            LogUtil.info(getClassName(),"data ["+data+"]");

            dataModel.put("dataListId", dataList.getId());

            // filter template
            List<String> filterTemplates = new ArrayList<String>();

            Pattern pagePattern = Pattern.compile("id='d-[0-9]+-p'|id='d-[0-9]+-ps'");
            for(String filterTemplate : dataList.getFilterTemplates()) {
                if(!pagePattern.matcher(filterTemplate).find()) {
                    filterTemplates.add(filterTemplate);
                }
            }

            dataModel.put("filterTemplates", filterTemplates.toArray(new String[0]));
            dataModel.put("showDataListFilter", dataList.getFilters().length > 0);
        }

        String htmlContent = pluginManager.getPluginFreeMarkerTemplate(dataModel, getClassName(), templatePath,null);
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

    @Nonnull
    protected Map<String, Object> formatRow(@Nonnull DataList dataList, @Nonnull Map<String, Object> row) {
        Map<String, Object> formattedRow = Optional.of(dataList)
                .map(DataList::getColumns)
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .filter(Objects::nonNull)
                .filter(not(DataListColumn::isHidden))
                .map(DataListColumn::getName)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toMap(s -> s, s -> formatValue(dataList, row, s)));

        String primaryKeyColumn = getPrimaryKeyColumn(dataList);
        formattedRow.putIfAbsent("_" + FormUtil.PROPERTY_ID, row.get(primaryKeyColumn));

        return formattedRow;
    }

    /**
     * Get Primary Key
     *
     * @param dataList
     * @return
     */
    @Nonnull
    protected String getPrimaryKeyColumn(@Nonnull final DataList dataList) {
        return Optional.of(dataList)
                .map(DataList::getBinder)
                .map(DataListBinder::getPrimaryKeyColumnName)
                .orElse("id");
    }

    /**
     * Format
     *
     * @param dataList DataList
     * @param row      Row
     * @param field    Field
     * @return
     */
    @Nonnull
    protected String formatValue(@Nonnull final DataList dataList, @Nonnull final Map<String, Object> row, String field) {
        String value = Optional.of(field)
                .map(row::get)
                .map(String::valueOf)
                .orElse("");

        return Optional.of(dataList)
                .map(DataList::getColumns)
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .filter(c -> field.equals(c.getName()))
                .findFirst()
                .map(column -> Optional.of(column)
                        .map(DataListColumn::getFormats)
                        .map(Collection::stream)
                        .orElseGet(Stream::empty)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .map(f -> f.format(dataList, column, row, value))
                        .map(s -> s.replaceAll("<[^>]*>", ""))
                        .orElse(value))
                .orElse(value);
    }

    protected JSONArray getRowsAsJson(DataList dataList) {
        return Optional.of(dataList)
                .map(DataList::getRows)
                .map(collection -> (DataListCollection<Map<String, Object>>) collection)
                .orElse(new DataListCollection<>())
                .stream()

                // reformat content value
                .map(row -> formatRow(dataList, row))

                // collect as JSON
                .collect(JSONCollectors.toJSONArray());
    }

    protected JSONArray getColumns(DataList dataList) {
        return Arrays.stream(dataList.getColumns())
                .map(DataListColumn::getName)
                .collect(JSONCollectors.toJSONArray());
    }
}
