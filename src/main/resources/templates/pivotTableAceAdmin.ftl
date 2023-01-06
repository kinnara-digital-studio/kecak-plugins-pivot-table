<link href="${request.contextPath}/plugin/com.kinnara.kecakplugins.pivottable.DataListPivotTable/css/pivot_custom.min.css" rel="stylesheet" type="text/css" />
<script src="https://cdn.plot.ly/plotly-basic-latest.min.js"></script>
<script type="text/javascript" src="${request.contextPath}/plugin/com.kinnara.kecakplugins.pivottable.DataListPivotTable/js/pivot_custom.min.js"></script>

<script type="text/javascript" src="${request.contextPath}/plugin/com.kinnara.kecakplugins.pivottable.DataListPivotTable/js/plotly_renderers.min.js"></script>
<style>
    form {
        margin-bottom:.5em;
    }
    .filters { text-align:right; font-size:smaller }
    .filter-cell{display:inline-block;padding-left:5px;}
</style>

<div class="col-12 mt-35">
    <div class="row">
        <#if showDataListFilter >
            <form name="filters_${dataListId}" id="filters_${dataListId}" action="?" method="POST">
                <div class="filters">
                    <#list filterTemplates! as template>
                        <span class="filter-cell">
                            ${template}
                        </span>
                    </#list>
                     <span class="filter-cell">
                         <input type="submit" value="Show"/>
                     </span>
                </div>
            </form>
        </#if>
    </div>
</div>
<div class="col-md-12 mt-35"">
    <div class="row">
        <div id="${elementName!}" class="table-responsive"></div>
    </div>
</div>
<script>
     $(function(){
         var derivers = $.pivotUtilities.derivers;
         var renderers = $.extend($.pivotUtilities.renderers, $.pivotUtilities.plotly_renderers, $.pivotUtilities.export_renderers); // Initialize Pivot Tabel Config

           var data = ${data!}; // Call Data (JSON)

           // store the state against key 'pivotdatakey'
           // get saved state from LocalStorage...
           var config = localStorage.getItem('pivotdatakey');

           if (config) { // If a saved state found in LocalStorage, then load it
               var configobject = JSON.parse(config);
               configobject.onRefresh = saveState; // Save when page refresh

               $("#${elementName!}").pivotUI( // Load with saved state
                       data,
                       configobject
                       );
           } else { // If not found any saved state lets save it
               $("#${elementName!}").pivotUI(
                       data,
                       {
                           renderers: renderers,
                           onRefresh: saveState

                       }
               );
           }

           // This function stores PivotTable config to LocalStorage.
           function saveState(config) {
               var config_copy = JSON.parse(JSON.stringify(config));
               //delete some values which are functions
               delete config_copy["aggregators"];
               delete config_copy["renderers"];
               //delete some bulky default values
               delete config_copy["rendererOptions"];
               delete config_copy["localeStrings"];
               localStorage.setItem('pivotdatakey', JSON.stringify(config_copy, undefined, 2));
           }

     });
</script>