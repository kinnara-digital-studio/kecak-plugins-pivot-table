<link href="${request.contextPath}/plugin/com.kinnara.kecakplugins.pivottable.DataListPivotTable/css/pivot.min.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${request.contextPath}/plugin/com.kinnara.kecakplugins.pivottable.DataListPivotTable/js/pivot.min.js"></script>

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
        $("#output").pivotUI( ${data!} );
     });

    console.log('${dataListId!}');
    console.log(${data!});

</script>