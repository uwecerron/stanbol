<h4>Subresource entityhub/entity</h4>
<p>Service to get/create/update and delete Entities managed by the Entityhub.
<h4> GET entityhub/entity</h4>
<table>
<tbody>
	<tr>
		<th>Description</th>
		<td>Service to get</td>
	</tr>
	<tr>
		<th>Request</th>
		<td>GET /entityhub/entity?id={uri}</td>
	</tr>
	<tr>
		<th>Parameter</th>
		<td>id: the URI of the entity</th>
	</tr>
	<tr>
		<th>Produces</th>
		<td>Depends on requested media type</td>
	</tr>
</tbody>
</table>

<h5>Example</h5>

<pre>curl "${it.publicBaseUri}entityhub/entity?id=</pre>
<h5>Test</h5>

<form id="getEntityForUriForm">
<p>Get entity for URI
<input type="text" size="50" id="entityId" name="id" value="" />
<input type="submit" value="Get Entity" onclick="getEntityForUri(); return false;" /></p>
</form>

<script language="javascript">
function getEntityForUri() {
 $("#entityResult").show();
 $.ajax({
   type: "GET",
   url: "${it.publicBaseUri}entityhub/entity",
   data: $("#getEntityForUriForm").serialize(),
   dataType: "text",
   cache: false,
   success: function(data, textStatus, jqXHR) {
     $("#entityResultText").text(data);
   },
   error: function(jqXHR, textStatus, errorThrown) {
     $("#entityResultText").text(jqXHR.statusText + " - " + jqXHR.responseText);
   }
 });		  
}
</script>

<div id="entityResult" style="display: none">
<p><a href="#" onclick="$('#entityResult').hide(); return false;">Hide results</a>
<pre id="entityResultText">... waiting for results ...</pre>
</div>

<h4> Create an Entity</h4>
<table>
<tbody>
    <tr>
        <th>Description</th>
        <td>Service to create entities for the Entityhub.</td>
    </tr>
    <tr>
        <th>Request</th>
        <td>POST /entityhub/entity?[id={uri}]</td>
    </tr>
    <tr>
        <th>Parameter</th>
        <td>id: optional the id of the Entity to add. If an id is parsed it is
        ensured that regardless of the included data only the entity with the
        parsed id is created. Information for other ids will be ignored.</th>
    </tr>
    <tr>
        <th>Produces</th>
        <td>201 with an link to the created entity</td>
    </tr>
</tbody>
</table>

<h4> Update an Entity</h4>
<table>
<tbody>
    <tr>
        <th>Description</th>
        <td>Service to update an Entity already managed by the Entityhub</td>
    </tr>
    <tr>
        <th>Request</th>
        <td>POST /entityhub/entity?[id={uri}]</td>
    </tr>
    <tr>
        <th>Parameter</th>
        <td>id: optional the id of the Entity to update. If an id is parsed it is
        ensured that regardless of the parsed data only information of this entity
        are updated</th>
    </tr>
    <tr>
        <th>Produces</th>
        <td>200 with the data of the entity encoded in the format specified by
        the Accept header</td>
    </tr>
</tbody>
</table>

<h4> Delete an Entity  entityhub/entity</h4>
<table>
<tbody>
    <tr>
        <th>Description</th>
        <td>Service to update an Entity already managed by the Entityhub</td>
    </tr>
    <tr>
        <th>Request</th>
        <td>DELETE /entityhub/entity?id={uri}</td>
    </tr>
    <tr>
        <th>Parameter</th>
        <td>id: The id of the Entity to delete.</th>
    </tr>
    <tr>
        <th>Produces</th>
        <td>200 with the data of the deleted entity encoded in the format specified by
        the Accept header</td>
    </tr>
</tbody>
</table>