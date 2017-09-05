<%@ page language="java" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.novell.nidp.*" %>
<%@ page import="com.novell.nidp.servlets.*" %>
<%@ page import="com.novell.nidp.resource.*" %>
<%@ page import="com.novell.nidp.resource.jsp.*" %>
<%@ page import="com.novell.nidp.ui.*" %>
<%
    String target = (String)request.getAttribute("target");
    ContentHandler handler = new ContentHandler(request,response);
    String actionURL = (String) request.getAttribute("url");
%>

<!DOCTYPE html>
<html>
<head>
    <title>Rogaland fylkeskommune</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />

    <link rel="Shortcut icon" href="<%= handler.getImage("rfk/img/favicon.png",false)%>" type="image/x-icon" />

    <!-- Bootstrap -->
    <link href="<%= handler.getImage("rfk/css/bootstrap.min.css",false)%>" rel="stylesheet">
    <link href="<%= handler.getImage("rfk/css/signin.css",false)%>" rel="stylesheet">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->

    <script src="<%= handler.getImage("rfk/js/modernizr.min.js",false)%>"></script>

</head>
<body>

<div class="container">

    <form class="form-signin" role="form"  name="IDPLogin" enctype="application/x-www-form-urlencoded" method="POST" action="<%= actionURL.replace("https://namidp01.rogfk.no", "") %>" autocomplete="off">


        <!--
                                <div class="row form-signin-heading">
                                    <div class="col-md-3"><img src="<%= handler.getImage("rfk/img/rogfk_vapen.jpg",false)%>" height="50" width="42" /> </div>
                                    <div class="col-md-9"><h2 class="">Velkommen</h2></div>
                                </div>
                -->

        <img src="<%= handler.getImage("rfk/img/rogfk_farger.jpg",false)%>" class="form-signin-heading" height="102" width="142" />
        <h2 class="form-signin-heading">SMS kode</h2>

        <%
            String err = (String) request.getAttribute(NIDPConstants.ATTR_LOGIN_ERROR);
            if (err != null)
            {
        %>
        <div class="alert alert-danger"><%=err%></div>
        <%  } %>
        <%
            if (NIDPCripple.isCripple())
            {
        %>
        <div class="alert alert-danger"><%=NIDPCripple.getCrippleAdvertisement(request.getLocale())%></div>
        <%
            }
        %>

        <input type="hidden" name="option" value="credential"/>
        <% if (target != null) { %>
        <input type="hidden" name="target" value="<%=target%>"/>
        <% } %>


        <input type="password" name="Response" placeholder="SMS kode" class="form-control" size="30" required autofocus>

        <button class="btn btn-lg btn-primary btn-block" type="submit">Logg inn</button>
        <br>
    </form>

</div> <!-- /container -->

<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="<%= handler.getImage("rfk/js/jquery.min.js",false)%>"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="<%= handler.getImage("rfk/js/bootstrap.min.js",false)%>"></script>

<script>

    $(window).load(function() {
        $(document).ready(function() {

            if (!Modernizr.input.placeholder) {
                $("input").each(
                    function() {
                        if ($(this).val() == "" && $(this).attr("placeholder") != "") {
                            $(this).val($(this).attr("placeholder"));
                            $(this).focus(function() {
                                if ($(this).val() == $(this).attr("placeholder"))
                                    $(this).val("");
                            });
                            $(this).blur(function() {
                                if ($(this).val() == "")
                                    $(this).val($(this).attr("placeholder"));
                            });
                        }
                    });
            }

        });
    });


</script>
</body>
</html>