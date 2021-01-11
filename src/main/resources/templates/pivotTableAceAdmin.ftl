<div class="container">
		<table class="table table-striped" border="1">
			<thead>
			    <tr>
			        <th colspan= "1" rowspan="1"></th>
			        <th>${coloum}</th>
                        <#list dataColoumn as dat>
                            <#if dat.coloumn?? && dat.coloumn !=''>
                        		<th colspan="1" rowspan="2">${dat.coloumn}</th>
                        	</#if>
                        </#list>
				        <th rowspan ="2">Total</th>
				</tr>
				<tr>
				    <th>${row}</th>
				    <th></th>
				</tr>
			</thead>
			<tbody>
			    <#list dataRow as dt>
                    <tr>
                        <td rowspan = "1" colspan ="2">${dt.rows}</td>
                            <#list dataColoumn as dt>
            		            <td></td>
            		        </#list>
            		    <td>${total}</td>
                    </tr>
                </#list>
            </tbody>
</div>