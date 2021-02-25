<link href="${request.contextPath}/plugin/com.kinnara.kecakplugins.pivottable.DataListPivotTable/css/pivot_custom.min.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${request.contextPath}/plugin/com.kinnara.kecakplugins.pivottable.DataListPivotTable/js/pivot_custom.min.js"></script>
<style>
form {
    margin-bottom:.5em;
}
</style>
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

<div class="row">
    <div class="col-md-12">
        <div id="output"></div>
    </div>
</div>
<script>
     $(function(){
       // var data=${data!};
       // $("#output").pivotUI(${data!});

         var renderers = $.extend($.pivotUtilities.renderers, $.pivotUtilities.c3_renderers, $.pivotUtilities.export_renderers); // Typical pivottable init stuff...

           var data = ${data!};; // Load the data from wherever you want...

           // We store the state against key 'pivotdatakey'
           // here we are getting saved state from LocalStorage...
           var config = localStorage.getItem('pivotdatakey');

           if (config) { // If a saved state found in LocalStorage, then load it
               var configobject = JSON.parse(config); // Make it an object
               configobject.onRefresh = saveState; // Add callback function for onRefresh

               $("#output").pivotUI( // Load with saved state
                       data, configobject
                       );
           } else { // State not found in LocalStorage. So create without a saved state
               $("#output").pivotUI(
                       data,
                       {
                           renderers: renderers,
                           onRefresh: saveState // Add the same callback function here.
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

    console.log('${dataListId!}');
    console.log(${data!});

</script>