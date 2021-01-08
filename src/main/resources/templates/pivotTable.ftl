<div class="container">
		<table class="table table-striped">
			<thead>
				<tr>
					<th>kolom1</th>
					<th>kolom2</th>
				</tr>
			</thead>
			<tbody>
				<#list data as dt>
				<tr>
					<td>${dt.id}</td>
					<td>${dt.name}</td>
				</tr>
				</#list>
			</tbody>
		</table>
</div>