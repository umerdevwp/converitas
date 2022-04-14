<%--
  Created by IntelliJ IDEA.
  User: olaf
  Date: 9/23/21
  Time: 11:11 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html xmlns="http://www.w3.org/1999/html">
<head>
  <asset:stylesheet src="jquery.autoSuggest.min.css"/>

  <meta name="layout" content="main"/>
  <title>Coveritas Company Tracker</title>

  <style>

  .form-control:focus + .list-group {
    display: block;
  }
  </style>
   <script type="module">

   $( document ).ready(function() {
      let pageURL = window.location.href; 
      if (pageURL.indexOf('/organization/index') > -1 ) {
        $('.navbar-nav a:last-child').addClass('selectedTab');
      }
    });
  </script>

</head>

<body>

<content tag="navTitle">Organizations</content>



<div id="content" role="main">
  <section class="row colset-2-its"> 
    <div id="companies" role="navigation">
      <content class="trackCompanySection">
        <h2>Add an organization:</h2>
        <div style="display: block">
          <input id="company" placeholder="Track a Company" size="40">
          <div style="display:inline-block;width:150px;background-color: transparent">
            <g:form method="post" action="startTacking">
              <input type="hidden" id="uuid" name="uuid"/>
              <button type="submit" id="addButton">Start Tracking</button>
            </g:form>
          </div>
          <div style="width:400px;height:30px;background-color: transparent">
            <select class="form-control list-group" id="companyOptions" style="display:none">
            </select>
          </div>
          <div class="messageSection hide">Start tracking the selected organization</div>
        </div>
      </content>     
      <h2>Currently Tracked Companies:</h2>      
      <table class="table">
        <thead class="table-dark">
        <th>ID</th>
        <th>Name</th>
        <th>Ticker</th>
        <th>Country</th>
        <th>Number of Users</th>
        <th>Action</th>
        </thead>
        <tbody>
        <g:each var="org" in="${organizations}">
          <tr>
            <td>
              <g:link uri="info" params="${[uuid:org.uuid]}">${org.uuid}</g:link>
            </td>
            <td>
              <g:link uri="info" params="${[uuid:org.uuid]}">${org.name}</g:link>
            </td>
            <td>
              <g:link uri="info" params="${[uuid:org.uuid]}">${org.country}</g:link>
            </td>
            <td>
              <g:link uri="info" params="${[uuid:org.uuid]}">${org.userCount}</g:link>
            </td>
            <td>
              <g:form method="post" action="remove">
                <input type="hidden" name="uuid" value="${org.uuid}">
                <button type="submit" class="stopTrackingBtn">remove</button>
              </g:form>
            </td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>
      <content class="lookupSection">
        <h2>Company Lookup:</h2>   
        <div style="display: block">
          <input id="companyL" placeholder="Company Lookup" size="40">
          <div style="display:inline-block;width:150px;background-color: transparent">
            <g:form method="post" action="lookup">
              <input type="hidden" id="uuid" name="uuid"/>
              <button type="submit" id="addButtonL">Look Up</button>
            </g:form>
          </div>
          <div style="width:400px;height:30px;background-color: transparent">
            <select class="form-control list-group" id="companyOptionsL" style="display:none">
            </select>
          </div>
        </div>
      </content>      
  </section>
</div>
</body>
</html>
